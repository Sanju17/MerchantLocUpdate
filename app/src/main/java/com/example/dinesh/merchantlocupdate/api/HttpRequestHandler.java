package com.example.dinesh.merchantlocupdate.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.auth.UsernamePasswordCredentials;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.impl.auth.BasicScheme;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by dinesh on 7/15/16.
 */
public class HttpRequestHandler {

    public static String getRequest(String url, String username, String password) {
        String json = "";
        StringBuffer stringBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();

            URI uri = new URI(url);
            httpGet.setURI(uri);
           /// httpGet.addHeader(BasicScheme.authenticate(
            //        new UsernamePasswordCredentials(username, password),
            //        HTTP.UTF_8, false));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                stringBuffer.append(readLine);
                stringBuffer.append("\n");
                readLine = bufferedReader.readLine();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                    json = stringBuffer.toString();
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
        }
        return json;
    }

    public static String putRequest(String url, String username, String password,
                                 List<NameValuePair> params) {
        String json = "";
        StringBuffer stringBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPut httpGet = new HttpPut();

            URI uri = new URI(url);
            httpGet.setURI(uri);
//            httpGet.addHeader(BasicScheme.authenticate(
//                    new UsernamePasswordCredentials(username, password),
//                    HTTP.UTF_8, false));
            httpGet.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                stringBuffer.append(readLine);
                stringBuffer.append("\n");
                readLine = bufferedReader.readLine();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                    json = stringBuffer.toString();
                    json = json.replace("\\\"", "\"");
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
        }
        return json;
    }

    // function get json from url
    // by making HTTP POST or GET mehtod
    public static String postRequest(String url, String username, String password,
                                  List<NameValuePair> params) {

        String json = "";
        StringBuffer stringBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpGet = new HttpPost();

            URI uri = new URI(url);
            httpGet.setURI(uri);
//            httpGet.addHeader(BasicScheme.authenticate(
//                    new UsernamePasswordCredentials(username, password),
//                    HTTP.UTF_8, false));
            httpGet.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                stringBuffer.append(readLine);
                stringBuffer.append("\n");
                readLine = bufferedReader.readLine();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                    json = stringBuffer.toString();
                    json = json.replace("\\\"", "\"");
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
        }

        return json;
    }

}
