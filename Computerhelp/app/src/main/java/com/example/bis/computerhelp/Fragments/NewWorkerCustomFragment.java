package com.example.bis.computerhelp.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by BIS on 13.01.2018.
 */
public class NewWorkerCustomFragment extends Fragment {
    EventListener mListener;
    ConnectionClass conn;
    ListView lv;
    Button btn;
    String login,filters="",sql="";
    ArrayList<Map<String,String>> data = new ArrayList<>();
    TextView isListNullTv;

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
        View v = inflater.inflate(R.layout.custom_list_layout,container,false);

        lv = (ListView) v.findViewById(R.id.custom_list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                open_custom(data.get(i).get("id"));
            }
        });
        isListNullTv = (TextView)v.findViewById(R.id.custom_list_tv);
        isListNullTv.setVisibility(View.INVISIBLE);
        conn =new ConnectionClass();
        sql = getResources().getString(R.string.baseUrl);
        return v;
    }
    private void open_custom(String id)
    {
        mListener.someEvent("open_custom;"+id);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(filters.length()!=0)
        {
            conn =new ConnectionClass();
            String ask=sql+"getFreeCustomWithTags&Id=";
            for (int i = 0; i < filters.split("\n").length; i++) {
                ask+= filters.split("\n")[i].split(";")[2];
                if (i<filters.split("\n").length-1)
                    ask+=",";
            }
            conn.execute(ask.replaceAll(" ","%20"));
        }
        else
        { conn =new ConnectionClass();
            conn.execute((sql+"getFreeCustom&Services=SELECT CustomName,CustomId,TotalCost FROM Custom WHERE Status=0").replaceAll(" ","%20"));
    }}
    public void setServices(String str)
    {
        filters=str;
    }
    public void setData(String login)
    {
        this.login=login;
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
                HttpGet http = new HttpGet(param[0]);
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
            if (data.size()>0)
                data.clear();
            if(result!=null&&result.length()!=0)
            {
                String[] customs = result.split("\n");
                Map<String,String> m;
                for (int i = 0; i < customs.length; i++) {
                    m = new HashMap<String, String>();
                    m.put("name", customs[i].split(";")[0]);
                    m.put("cost", customs[i].split(";")[1]);
                    m.put("id",customs[i].split(";")[2]);
                    data.add(m);
                }
                String[] from={"name","cost"};
                int[] to = {R.id.textView,R.id.textView3};
                SimpleAdapter adapter = new SimpleAdapter(
                        getActivity(),
                        data,
                        R.layout.custom_list_item,
                        from,
                        to);
                if (lv.getAdapter()==null)
                    lv.setAdapter(adapter);
                else
                    adapter.notifyDataSetChanged();
            }
            else
            {
                isListNullTv.setVisibility(View.VISIBLE);
            }

        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }


}
