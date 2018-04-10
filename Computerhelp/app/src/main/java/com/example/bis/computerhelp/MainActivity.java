package com.example.bis.computerhelp;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;

import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.bis.computerhelp.Fragments.AdministratorFragment;
import com.example.bis.computerhelp.Fragments.CustomFragment;
import com.example.bis.computerhelp.Fragments.CustomListFragment;
import com.example.bis.computerhelp.Fragments.NewUserCustomFragment;
import com.example.bis.computerhelp.Fragments.ReviewFragment;
import com.example.bis.computerhelp.Fragments.ServicesListFragment;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,EventListener {
    FragmentTransaction ft;
    ServicesListFragment servicesListFragment;
    NewUserCustomFragment newUserCustomFragment;
    String login="",isWorker="";
    static public final int REQUEST_LOCATION = 1;
    private boolean isSend=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        login=getIntent().getStringExtra("login");
        isWorker=getIntent().getStringExtra("type");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ImageView imageView = (ImageView)findViewById(R.id.advertisingImageView);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();  // deprecated
        int actionBarHeight=0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        Log.d("logger",actionBarHeight+"");
        Picasso.with(this)
                .load(getResources().getString(R.string.imageUrl)+"advertising.png")
                .resize(width,actionBarHeight)
                .into(imageView);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (getFragmentManager().getBackStackEntryCount() ==0) {
            ft = getFragmentManager().beginTransaction();
            CustomListFragment customListFragment = new CustomListFragment();
            customListFragment.setLogin(login, isWorker);
            ft.replace(R.id.container, customListFragment);
            ft.commit();
        }
        if (isWorker.contains("1")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION);
            } else {
               startService();
            }
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() >0) {
                getFragmentManager().popBackStack();


            } else {
                this.finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_custom) {
            if (isWorker.contains("0")) {
                newUserCustomFragment = new NewUserCustomFragment();
                newUserCustomFragment.setData(login);
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, newUserCustomFragment);
                ft.addToBackStack(null);
                ft.commit();
                return true;
            }
            else
            {
                Intent intent = new Intent(this,NewCustomWorker.class);
                intent.putExtra("login",login);
                startActivity(intent);
                return true;
            }
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void someEvent(String s)
    {
        ft=getFragmentManager().beginTransaction();
        if(s.split(";")[0].contains("show custom"))
        {
            CustomFragment cf = new CustomFragment();
            cf.setData(s.split(";")[1],isWorker);
            ft.replace(R.id.container,cf);
            ft.addToBackStack(null);
            ft.commit();
        }
        if(s.contains("show review"))
        {
            ReviewFragment reviewFragment = new ReviewFragment();
            reviewFragment.setData(s.split(";")[1],s.split(";")[2],s.split(";")[3],s.split(";")[4]);
            ft.replace(R.id.container,reviewFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
        if(s.contains("add_type"))
        {
            servicesListFragment = new ServicesListFragment();
            ft.replace(R.id.container,servicesListFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
        if(s.split("%")[0].contains("return_services"))
        {
            if (s.split("%").length>1)
            newUserCustomFragment.setServices(s.split("%")[1]);
            else  newUserCustomFragment.setServices("");
            getFragmentManager().popBackStack();

        }


    }
    private void startService()
    {
        isSend=true;
        Intent intent = new Intent(this,LocationSend.class);
        intent.putExtra("action","start");
        intent.putExtra("login",login);
        startService(intent);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.nav_admin)
        {
            ft=getFragmentManager().beginTransaction();
            AdministratorFragment administratorFragment = new AdministratorFragment();
            administratorFragment.setData(login);
            ft.replace(R.id.container,administratorFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
        if(id == R.id.nav_customs)
        {
            ft=getFragmentManager().beginTransaction();
            CustomListFragment customListFragment = new CustomListFragment();
            customListFragment.setLogin(login,isWorker);
            ft.replace(R.id.container,customListFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
        if(id == R.id.nav_out)
        {
            Intent intent = new Intent(this,LoginActivity.class);
            intent.putExtra("return","");
            startActivity(intent);
            this.finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onDestroy()
    {
        Intent intent = new Intent(this,LocationSend.class);
        intent.putExtra("action","stop");
        startService(intent);
        super.onDestroy();
    }


}
