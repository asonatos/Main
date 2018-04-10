package com.example.bis.computerhelp.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.bis.computerhelp.ConnectionClass;
import com.example.bis.computerhelp.EventListener;
import com.example.bis.computerhelp.R;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BIS on 13.01.2018.
 */
public class ServicesListFragment extends Fragment {
    EventListener mListener;
    ListView listView;
    String[] services;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        View v = inflater.inflate(R.layout.services_list_lv,container,false);
        listView = (ListView) v.findViewById(R.id.services_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        Button btn = (Button)v.findViewById(R.id.services_list_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray sbArray = listView.getCheckedItemPositions();
                String string ="";
                for (int i = 0; i < sbArray.size(); i++) {
                    int key = sbArray.keyAt(i);
                    if (sbArray.get(key))
                        string+=services[key]+"\n";
                }

                mListener.someEvent("return_services%"+string);
            }
        });
        return v;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        getService getService = new getService();
        getService.execute();
    }

    private class getService extends AsyncTask<Void,Void,String>
    {
        String result;
        @Override
        protected String doInBackground(Void... voids) {
            try {
                Log.d("logger",getResources().getString(R.string.baseUrl)+"getServices");
                HttpGet http = new HttpGet(getResources().getString(R.string.baseUrl)+"getServices");
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler rez = new BasicResponseHandler();
                result = (String) hc.execute(http, rez);
            } catch (Exception ex) {
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result)
        {
            try {
                String[] res = result.split("\n");
                String[] data = new String[res.length];
                services=new String[res.length];
                for (int i = 0; i < res.length; i++) {
                    data[i] = res[i].split(";")[0] + " : " + res[i].split(";")[1];
                    services[i] = res[i];
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_single_choice, data);
                listView.setAdapter(adapter);
            }
            catch (Exception ex){Log.d("logger",ex.toString());}
        }
    }
}
