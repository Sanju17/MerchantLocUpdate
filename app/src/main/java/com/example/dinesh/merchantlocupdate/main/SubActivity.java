package com.example.dinesh.merchantlocupdate.main;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinesh.merchantlocupdate.R;
import com.example.dinesh.merchantlocupdate.api.HttpRequestHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class SubActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private String terminalID;
    private String selectedCategorty;

    private SurfaceView cameraView;
    private TextView merchantNameTextView;
    private TextView merchantAddressTextView;
    private ProgressBar loadingProgressBar;
    private LinearLayout merchantDetailsContainer;
    private CompoundBarcodeView barcodeView;
    private ImageView codeImageView;
    private Spinner categorySpinner;
    private TextView merchantIdTextView;
    private Button updateLocationBtn;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private  double lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        merchantNameTextView = (TextView) findViewById(R.id.merchant_name);
        merchantAddressTextView = (TextView) findViewById(R.id.merchant_address);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loading_merchant_details);
        merchantDetailsContainer = (LinearLayout) findViewById(R.id.merchant_detail_container);
        barcodeView = (CompoundBarcodeView) findViewById(R.id.barcode_scanner);
        codeImageView = (ImageView) findViewById(R.id.bar_view_image_view);
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        merchantIdTextView = (TextView) findViewById(R.id.merchant_id);
        updateLocationBtn = (Button) findViewById(R.id.update_location_btn);
        lat=getIntent().getDoubleExtra("lat",0);
        lon=getIntent().getDoubleExtra("lon",0);
        Log.d("dinesh","lat lng from subactivity "+ lat+lon);
        updateLocationBtn.setEnabled(true);
        selectedCategorty = getResources().getStringArray(R.array.android_dropdown_arrays)[0];

        categorySpinner.setOnItemSelectedListener(new ItemSelectedListener());

        barcodeView.setVisibility(View.VISIBLE);

        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
//        startQRCodeDetectionUsingVisionApi();
        startQRCodeDetectionUsingZxing();

    }

    public class ItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            selectedCategorty = parent.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg) {
            Toast.makeText(getApplicationContext(), "Please Select a category!!!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
        checkPlayServices();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    private void startQRCodeDetectionUsingZxing() {
        barcodeView.decodeSingle(callback);
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            String resultString = result.toString();
            Bitmap bitmap = result.getBitmap();
            codeImageView.setImageBitmap(bitmap);
            barcodeView.setVisibility(View.GONE);
            terminalID = resultString;
            //codeImageView.setVisibility(View.VISIBLE);
            getDeviceLocation();
            callGetMerchantDetails(resultString);
            //Toast.makeText(SubActivity.this, resultString, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    private void callGetMerchantDetails(String id) {
        new GetMerchantDetailsTask().execute(id);
    }

    public void updateLocation(View view) {
        if (mLastLocation != null) {
//            Toast.makeText(getApplicationContext(),
//                    "You have selected : " + selectedCategorty,
//                    Toast.LENGTH_LONG).show();
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            Log.d("dinesh","latitute : "+latitude + "longitute : "+longitude);
            updateLocationBtn.setEnabled(false);

        } else {
            Toast.makeText(SubActivity.this, "(Couldn't get the location. Make sure location is enabled on the device)", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(SubActivity.this, "lat: " + lat + "\nlong: " + lon, Toast.LENGTH_SHORT).show();
        new UpdateMerchantLocation().execute(lat, lon);
    }

    private void getDeviceLocation() {
        retrieveLocation();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    /**
     * Method to retrieve the location
     * */
    private void retrieveLocation() {

        LocationManager lm = (LocationManager) SubActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            showAlert();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        retrieveLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("Test", "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    /*
    * Get Merchant Details from Server
    * */
    class GetMerchantDetailsTask extends AsyncTask<String, String, String> {

        String url = "http://qpay-utilities.azurewebsites.net/api/Merchant/GetMerchantDetailsByTerId?terminalId=";
        String url1 = "http://qpay-utilities.azurewebsites.net/api/Merchant/GetMerchantDetailsByTerId";
        @Override
        protected void onPreExecute() {
            loadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String fullURL = url + strings[0];
            List<NameValuePair> dataArr = new ArrayList<>();
            dataArr.add(new BasicNameValuePair("terminalId", strings[0]));
            String response = HttpRequestHandler.postRequest(url1,"", "", dataArr);
//            String response = HttpRequestHandler.getRequest(fullURL, "", "");
            return response;
        }

        @Override
        protected void onPostExecute(String jsonStr) {
            String merchantName = "";
            String merchantAddress = "";
            String merchantId = "";
            try {
                loadingProgressBar.setVisibility(View.GONE);
                JSONObject jObj = new JSONObject(jsonStr);
                if(jObj.getInt("status") == 200) {
                    JSONArray merchantDataArr = jObj.getJSONArray("data");
                    if (merchantDataArr.length() == 1) {
                        JSONObject merchantDetailData = (JSONObject) merchantDataArr.get(0);
                        merchantName = merchantDetailData.getString("MerchantName");
                        merchantAddress = merchantDetailData.getString("Address1");
                        merchantId = merchantDetailData.getString("TerminalID");
                        merchantNameTextView.setText("Name: " + merchantName);
                        merchantAddressTextView.setText("Address: " + merchantAddress);
                        merchantIdTextView.setText("Id: " + merchantId);
                        merchantDetailsContainer.setVisibility(View.VISIBLE);

                    }else {
                        Toast.makeText(SubActivity.this, "More Than one Merchant information.",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(SubActivity.this, jObj.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class UpdateMerchantLocation extends AsyncTask<Double, Double, String> {

        String url = "http://qpay-utilities.azurewebsites.net/api/Merchant/UpdateTermianlLocation";

        @Override
        protected void onPreExecute() {
            Toast.makeText(SubActivity.this, "Updating Location...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(Double... params) {
            Double locLat = params[0];
            Double locLng = params[1];
            List<NameValuePair> dataArr = new ArrayList<>();
            dataArr.add(new BasicNameValuePair("terminalId", terminalID));
            dataArr.add(new BasicNameValuePair("loclat", String.valueOf(lat)));
            dataArr.add(new BasicNameValuePair("loclng", String.valueOf(lon)));
            dataArr.add(new BasicNameValuePair("category", selectedCategorty));
            String response = HttpRequestHandler.putRequest(url,"","",dataArr);
            Log.d("dinesh",response+"mechant update result ");
            Log.d("dinesh","post to merchant update :"+dataArr.toString());
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            String msg = "";
            try {
                JSONObject jObj = new JSONObject(s);
                if(jObj.getInt("status") == 200) {
                    msg = "Location Update Successful\n"+"lat: "+lat+" long: "+ lon;

//                    Toast.makeText(SubActivity.this, "Location Update Successful", Toast.LENGTH_SHORT).show();
                }else {
                    msg = "Something went Wrong while updating location. Please Try again.";
//                    Toast.makeText(SubActivity.this, "Something went Wrong while updating location. Please Try again.", Toast.LENGTH_SHORT).show();
                }

                final AlertDialog.Builder dialog = new AlertDialog.Builder(SubActivity.this);
                dialog.setTitle(msg)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                SubActivity.this.onBackPressed();
                            }
                        });
                dialog.show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}