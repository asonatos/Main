package com.example.bis.computerhelp.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.concurrent.TimeUnit;

/**
 * Created by BIS on 05.01.2018.
 */
public class AdministratorFragment extends Fragment {
    EventListener mListener;
    String login;
    ListView lv;
    Button btn;
    Messages message;
    EditText textEd;
    SimpleAdapter adapter;
    Thread getMessageThread;
    ArrayList<Map<String, Object>> data=new ArrayList<>();
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
        View v = inflater.inflate(R.layout.administrator_message,container,false);
        lv = (ListView)v.findViewById(R.id.adminlist);

        btn = (Button)v.findViewById(R.id.admibistratorBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textEd.getText().toString().length()==0){}
                else {
                    String[] from = {"admin", "user"};
                    int[] to = {R.id.admin, R.id.user};
                    Map<String, Object> m;
                    m = new HashMap<String, Object>();
                    m.put("admin", "");
                    m.put("user", textEd.getText().toString());
                    data.add(m);
                    if (adapter == null) {
                        adapter = new SimpleAdapter(getActivity(), data, R.layout.admin_list_item, from, to);
                    }
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.execute(textEd.getText().toString());
                }
            }
        });

        textEd =(EditText)v.findViewById(R.id.administratorEd);
        message = new Messages();
        message.execute();
        return v;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        getMessageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    message = new Messages();
                    message.execute();
                    TimeUnit.SECONDS.sleep(5);
                }
                catch (InterruptedException ex){}
            }
        });
        getMessageThread.start();
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(message!=null)
        {
            message.cancel(true);
        }
    }
    public void setData(String data)
    {
        login=data;
    }
    private class Messages extends AsyncTask<Void,Void,String>
    {
        String result;
        @Override
        protected String doInBackground(Void... voids) {
            try {
                HttpGet http = new HttpGet(getResources().getString(R.string.baseUrl)+"getUserMessages&User="+login.replaceAll(" ","%20"));
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
            if(data!=null)
                data.clear();
            if (result!=null&&result.length()>0) {
                String[] from = {"admin", "user"};
                int[] to = {R.id.admin, R.id.user};
                String[] messages = result.split("\n");
                data = new ArrayList<Map<String, Object>>(
                        messages.length);
                Map<String, Object> m;
                for (int i = 0; i < messages.length; i++) {
                    m = new HashMap<String, Object>();
                    if (messages[i].split(";")[0].contains("admin")) {
                        m.put("admin", messages[i].split(";")[2]);
                        m.put("user", "");
                    } else {
                        m.put("admin", "");
                        m.put("user", messages[i].split(";")[2]);
                    }
                    data.add(m);
                }

                adapter = new SimpleAdapter(getActivity(), data, R.layout.admin_list_item, from, to);
                lv.setAdapter(adapter);
            }
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
    private class SendMessage extends AsyncTask<String,Void,String>
    {
        String result;
        String message;
        @Override
        protected String doInBackground(String... param) {
            try {
                message=param[0];
                HttpGet http = new HttpGet((getResources().getString(R.string.baseUrl)+"setMessage&FromUser="+login+"&ToUser=admin&Text="+param[0]).replaceAll(" ","%20"));
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
            if (result!=null)
            {
                adapter.notifyDataSetChanged();
                textEd.setText("");
            }
            else
            {
                Toast.makeText(getActivity(),"Ошибка",Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }


}

