package com.example.bis.affectedarea;

import android.graphics.Color;
import android.location.Criteria;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.security.ProviderInstaller;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int type=0,angle=0,horizon=0,precision;
    private double radius;
    private LocationManager locManager;
    private List<String> providersList;
    private LocationProvider provider;
    private Criteria req;
    private boolean inside;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        radius=Double.parseDouble(getIntent().getStringExtra("radius"));
        type=Integer.parseInt(getIntent().getStringExtra("type"));
        angle=Integer.parseInt(getIntent().getStringExtra("angle"));
        horizon=Integer.parseInt(getIntent().getStringExtra("horizon"));

        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        providersList = locManager.getAllProviders();
        provider =locManager.getProvider(providersList.get(0));
        precision = provider.getAccuracy();
        req = new Criteria();
        req.setAccuracy(Criteria.ACCURACY_FINE);
    }
    private CircleOptions draw_circle(LatLng latLng)
    {
        Log.d("circle",radius+"");
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(radius*1000)
                .fillColor(R.color.fillColor)
                .strokeColor(R.color.fillColor)
                .strokeWidth(1);
        return circleOptions;
    }
    private PolygonOptions draw_sector(LatLng latLng)
    {
        List<LatLng> list =new ArrayList<>();
        for(int i = horizon-(angle/2);i<horizon+(angle/2);i++)
        {
            list.add(getDestinationPoint(latLng,i,radius));
        }
        PolygonOptions polygoneOptions = new PolygonOptions()
                .add(latLng)
                .addAll(list)
                .add(latLng)
                .strokeColor(R.color.fillColor).strokeWidth(1).fillColor(R.color.fillColor);
        return polygoneOptions;
    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap=googleMap;
        try {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapClickListener(new OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    mMap.clear();
                    if (type == 0)
                        mMap.addCircle(draw_circle(latLng));
                    else
                        mMap.addPolygon(draw_sector(latLng));
                }
            });
        }catch (SecurityException ex){}
    }
    private LatLng getDestinationPoint(LatLng source, double brng, double dist) {
        dist = dist / 6371;
        brng = Math.toRadians(brng);

        double lat1 = Math.toRadians(source.latitude), lon1 = Math.toRadians(source.longitude);
        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist) +
                Math.cos(lat1) * Math.sin(dist) * Math.cos(brng));
        double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(dist) *
                        Math.cos(lat1),
                Math.cos(dist) - Math.sin(lat1) *
                        Math.sin(lat2));
        if (Double.isNaN(lat2) || Double.isNaN(lon2)) {
            return null;
        }
        return new LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2));
    }

}
