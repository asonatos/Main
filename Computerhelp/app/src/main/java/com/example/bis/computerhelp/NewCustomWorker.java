package com.example.bis.computerhelp;


import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bis.computerhelp.Fragments.NewWorkerCustomFragment;
import com.example.bis.computerhelp.Fragments.ServicesListFragment;
import com.example.bis.computerhelp.Fragments.WorkerCustomFragment;

public class NewCustomWorker extends AppCompatActivity  implements EventListener{
    FragmentTransaction ft;
    String login="";
    NewWorkerCustomFragment workerCustomFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_custom_worker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.new_custom_worker_toolbar);
        setSupportActionBar(toolbar);

        login=getIntent().getStringExtra("login");

        ft=getFragmentManager().beginTransaction();
        workerCustomFragment = new NewWorkerCustomFragment();
        workerCustomFragment.setData(login);
        ft.add(R.id.new_custom_container,workerCustomFragment);
        ft.commit();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_custom_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_custom_menu_tags) {
            ft= getFragmentManager().beginTransaction();
            ft.replace(R.id.new_custom_container,new ServicesListFragment());
            ft.addToBackStack(null);
            ft.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void someEvent(String s)
    {
        if(s.split("%")[0].contains("return_services"))
        {

            getFragmentManager().popBackStack();
            if (s.split("%").length>1)
                workerCustomFragment.setServices(s.split("%")[1]);
            else  workerCustomFragment.setServices("");
        }
        if(s.split(";")[0].contains("open_custom"))
        {
            ft = getFragmentManager().beginTransaction();
            WorkerCustomFragment customFragment = new WorkerCustomFragment();
            customFragment.setData(s.split(";")[1],login);
            ft.replace(R.id.new_custom_container,customFragment);
            ft.commit();
        }
    }
}
