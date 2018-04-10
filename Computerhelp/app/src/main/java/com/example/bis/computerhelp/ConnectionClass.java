package com.example.bis.computerhelp;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by BIS on 07.01.2018.
 */
public class ConnectionClass extends AsyncTask<String, Void, String> {
    String result;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... param) {
        try {
            HttpGet http = new HttpGet(param[0]);
            Log.d("connection",param[0]);
            DefaultHttpClient hc = new DefaultHttpClient();
            ResponseHandler rez = new BasicResponseHandler();
            result = (String) hc.execute(http, rez);
        } catch (Exception ex) {
        }
        return result;
    }
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
