package com.example.bis.computerhelp;

import android.app.IntentService;
import android.app.Service;
import android.app.admin.SecurityLog;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;


public class LocationSend extends IntentService {

    private String login,result;
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            String location_string = location.getLatitude() + ";" + location.getLongitude();
            Log.d("logger", (getResources().getString(R.string.baseUrl) + "sendLocation&Location=" + location_string + "&login=" + login).replaceAll(" ", "%20"));
            if (sendFlag) {
                Send send = new Send();
                send.execute((getResources().getString(R.string.baseUrl) + "sendLocation&Location=" + location_string + "&login=" + login).replaceAll(" ", "%20"));
            }}

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };
    private LocationManager locationManager;
    private boolean sendFlag=true;
    public LocationSend() {
        super("LocationSend");
    }
    public void onCreate() {
        super.onCreate();

    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000 * 10, 10, locationListener);
        }
        catch (SecurityException ex){Log.d("asd",ex.toString());}
        if(intent.getStringExtra("action").contains("stop")&&login!=null) {
            sendFlag=false;
            Stop stop = new Stop();
            stop.execute();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent.getStringExtra("action").contains("start")) {
            sendFlag=true;
            login=intent.getStringExtra("login");
           /* try {
                while (sendFlag) {
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    String location_string = location.getLatitude() + ";" + location.getLongitude();
                    Log.d("logger", (getResources().getString(R.string.baseUrl) + "sendLocation&Location=" + location_string + "&login=" + login).replaceAll(" ", "%20"));
                    try {
                        HttpGet http = new HttpGet((getResources().getString(R.string.baseUrl) + "sendLocation&Location=" + location_string + "&login=" + login).replaceAll(" ", "%20"));
                        DefaultHttpClient hc = new DefaultHttpClient();
                        ResponseHandler rez = new BasicResponseHandler();
                        result = (String) hc.execute(http, rez);
                    }
                    catch (IOException e)
                    {
                        Log.d("loggerError",e.toString());
                    }
                    TimeUnit.SECONDS.sleep(5);
                }
            }
            catch (SecurityException ex){}
            catch (Exception ex){}*/
        }
        else
            stopSelf();
    }
    private class Stop extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpGet http = new HttpGet((getResources().getString(R.string.baseUrl) + "deleteLocation&login=" + login).replaceAll(" ", "%20"));
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler rez = new BasicResponseHandler();
                result = (String) hc.execute(http, rez);
            }
            catch (IOException e)
            {
                Log.d("loggerError",e.toString());
            }
            return null;
        }
    }
    private class Send extends AsyncTask<String,Void,Void>
    {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                HttpGet http = new HttpGet(strings[0]);
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler rez = new BasicResponseHandler();
                result = (String) hc.execute(http, rez);
            }
            catch (IOException e)
            {
                Log.d("loggerError",e.toString());
            }
            return null;
        }
    }
}
