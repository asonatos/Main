package com.example.bis.computerhelp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bis.computerhelp.EventListener;
import com.example.bis.computerhelp.R;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by BIS on 05.01.2018.
 */
public class CustomListFragment extends Fragment {
    ConnectionClass conn;
    EventListener mListener;
    SimpleAdapter adapter;
    String login="",type="";
    boolean mAlreadyLoaded=false;
    String[] status_data={"Ожидает","Принят","Выполняется","Выполнен"};
    ListView lv;
    ArrayList<Map<String,Object>> data = new ArrayList<>();
    TextView isListNullTv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public void setLogin(String login,String type)
    {
        this.login=login;
        this.type=type;

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (EventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.custom_list_layout,container,false);


        data=getCustomList(login);

        isListNullTv = (TextView)v.findViewById(R.id.custom_list_tv);
        isListNullTv.setVisibility(View.INVISIBLE);

        lv = (ListView)v.findViewById(R.id.custom_list);
        setRetainInstance(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListener.someEvent("show custom;"+data.get(i).get("id"));
            }
        });

        return v;
    }
    @Override
    public void onResume()
    {
        super.onResume();

    }
    private ArrayList<Map<String,Object>> getCustomList(String login)
    {
        conn =new ConnectionClass();
        ArrayList<Map<String, Object>> data= new ArrayList<Map<String, Object>>();
        try {
            String sql = getResources().getString(R.string.baseUrl)+"getUserCustoms&Login="+login;
            if (type.contains("1"))
                sql = getResources().getString(R.string.baseUrl)+"getWorkerCustoms&Login="+login;
            conn.execute(sql);
        }
        catch (Exception ex){Log.d("error",ex.toString());}
        return data;
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(conn!=null)
        {
            conn.cancel(true);
        }
    }
    private class ConnectionClass extends AsyncTask<String, Void, String> {
        String result;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... param) {
            try {
                HttpGet http = new HttpGet(param[0].replaceAll(" ","%20"));
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler rez = new BasicResponseHandler();
                result = (String) hc.execute(http, rez);
            } catch (Exception ex) {
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try
            {
            if (result.length()!=0) {
                String[] customs = result.split("\n");
                Map<String, Object> m;
                for (int i = 0; i < customs.length; i++) {
                    m = new HashMap<String, Object>();
                    m.put("name", customs[i].split(";")[0]);
                    m.put("cost", customs[i].split(";")[1]);
                    m.put("status", status_data[Integer.parseInt(customs[i].split(";")[2])]);
                    m.put("id", customs[i].split(";")[3]);
                    data.add(m);
                }
                String[] from = {"name", "cost", "status"};
                int[] to = {R.id.textView, R.id.textView3, R.id.textView2};
                adapter = new SimpleAdapter(getActivity(), data, R.layout.custom_list_item, from, to);
                lv.setAdapter(adapter);
            }
                else
            {
                isListNullTv.setVisibility(View.VISIBLE);
            }
            }catch (Exception ex){}
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }


}