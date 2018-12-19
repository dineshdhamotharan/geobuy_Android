package apps.codette.utils;


import android.os.StrictMode;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.HttpGet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class RestCall {
    //private static final String BASE_URL = "http://server-dot-pingme-191816.appspot.com/";
    private static final String BASE_URL = "https://geobuy-viki19nesh.c9users.io/";
    private static AsyncHttpClient client = new AsyncHttpClient();
    //private static final String BASE_URL = "http://13.71.1.225:8080/";
    private static SyncHttpClient sclient = new SyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
       // client.addHeader("Accept", "application/json");
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }
    public static void sGet(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        //sclient.addHeader("Accept", "application/json");
        sclient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, Object object) {
        client.addHeader("Accept", "application/json");
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.i(url,params.toString());
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.i(url,params.toString());
        client.get(url, params, responseHandler);
    }

    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.i(url,params.toString());
        client.post(url, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        Log.i("relativeUrl",BASE_URL + relativeUrl);
        return BASE_URL + relativeUrl;
    }

    public static String  parseJsonFromDownloadUrl(String url)  {
        Log.i("RestCall", url);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        try {
            if (SDK_INT > 8)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                InputStream inputStream = null;
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                HttpResponse response = client.execute(httpGet);
                inputStream = response.getEntity().getContent();
                String respo = null;
                if(inputStream != null){
                    respo = convertStreamToString(inputStream);
                    return respo;
                }
                return null;
            }
        } catch (Exception ex){
            return null;
        }
        return null;
    }


    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}