package com.example.bis.computerhelp.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bis.computerhelp.ConnectionClass;
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
public class NewUserCustomFragment extends Fragment {
    saveCustom saveCustom;
    EventListener mListener;
    ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
    ArrayList<Map<String, String>> list=new ArrayList<Map<String, String>>();
    ListView lv;
    String login="",Custom_services="";
    String[]sinner_data={""};
    EditText nameEd,telephoneEd,adressEd;
    Spinner city;
    Button add_type,save;
    SimpleAdapter adapter;
    int total_cost=0;
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
        View v = inflater.inflate(R.layout.new_custom_layout,container,false);
        lv = (ListView)v.findViewById(R.id.custom_lv);
        String[] from={"name","cost"};
        int[] to = {R.id.textView4,R.id.textView5};

        adapter = new SimpleAdapter(getActivity(),list,R.layout.new_custom_list_item, from,to);
        lv.setAdapter(adapter);
        nameEd=(EditText) v.findViewById(R.id.new_custom_name);
        telephoneEd=  (EditText) v.findViewById(R.id.new_custom_tel_ed);
        adressEd = (EditText) v.findViewById(R.id.adressEd);

        city = (Spinner) v.findViewById(R.id.new_custom_layout_city);
        add_type = (Button) v.findViewById(R.id.new_custom_add_tye_btn);
        add_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.someEvent("add_type");
            }
        });

        save = (Button) v.findViewById(R.id.new_custom_save_btn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_custom();
            }
        });
        return v;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        city.setAdapter(load_spinner_adapter());
        city.setSelection(0);
    }
    public void setData(String login)
    {
        this.login=login;
    }
    public void setServices(String str)
    {
        if(str.length()!=0)
        {
            Map<String, String> map;
            list.clear();
            total_cost = 0;
            for (int i = 0; i < str.split("\n").length; i++) {
                map = new HashMap<>();
                map.put("name", str.split("\n")[i].split(";")[0]);
                map.put("cost", str.split("\n")[i].split(";")[1]);
                total_cost += Integer.parseInt(str.split("\n")[i].split(";")[1]);
                map.put("id", str.split("\n")[i].split(";")[2]);
                Custom_services += str.split("\n")[i].split(";")[2] + ",";
                list.add(map);
            }
            map = new HashMap<>();
            map.put("name", "Всего");
            map.put("cost", total_cost + "");
            list.add(map);
            adapter.notifyDataSetChanged();
        }

    }
    private void save_custom()
    {
        try
        {
            saveCustom = new saveCustom();
            String sql = getResources().getString(R.string.baseUrl)+"addCustom&CustomName="+nameEd.getText().toString()+
                    "&Telephone="+telephoneEd.getText().toString()+"&Adress="+adressEd.getText().toString()+
                    "&Status=0&Login="+login+"&TotalCost="+total_cost+"&City="+sinner_data[city.getSelectedItemPosition()]+
                    "&Custom_services="+Custom_services;
            sql.replaceAll(" ","%20");
            saveCustom.execute(sql);
        }
        catch (Exception ex){}
    }

    private ArrayAdapter<String> load_spinner_adapter()
    {

        ArrayAdapter<String> adapter;
        try
        {
            String sql = getResources().getString(R.string.baseUrl)+"getCitys";
            ConnectionClass conn = new ConnectionClass();
            conn.execute(sql);
            sinner_data=conn.get().split("\n");
            adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,sinner_data);
            return adapter;

        }catch (Exception ex){return  null;}

    }
    private class saveCustom extends AsyncTask<String,Void,String>
    {
        String result;
        @Override
        protected String doInBackground(String... param) {
            try {
                Log.d("logger",param[0]);
                HttpGet http = new HttpGet(param[0].replaceAll(" ","%20"));
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler rez = new BasicResponseHandler();
                result = (String) hc.execute(http, rez);
            } catch (Exception ex) {
            }
            return result;
        }
        @Override
        protected void onPostExecute(String string)
        {
            if(string.contains("ok"))
                Toast.makeText(getActivity(),"Сохранено",Toast.LENGTH_SHORT).show();
            else Toast.makeText(getActivity(),"Ошибка",Toast.LENGTH_SHORT).show();
        }
    }

}
