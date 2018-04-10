package com.example.bis.computerhelp.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bis.computerhelp.ConnectionClass;
import com.example.bis.computerhelp.EventListener;
import com.example.bis.computerhelp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BIS on 19.01.2018.
 */
public class WorkerCustomFragment extends Fragment {
    ConnectionClass conn;
    EventListener mListener;
    Spinner status_spinner;
    ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
    String id="",login="";
    String[] sinner_data={"Ожидает","Принят","Выполняется","Выполнен"};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public void setData(String id,String login)
    {
        this.id=id;
        this.login=login;
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
        View v = inflater.inflate(R.layout.custom_layout,container,false);
        setRetainInstance(true);
        ListView lv = (ListView)v.findViewById(R.id.custom_lv);

        if (data.size()==0)data.addAll(getCustomData());

        TextView custom_adress = (TextView)v.findViewById(R.id.custom_adress_ed);
        custom_adress.setText(data.get(0).get("adress"));
        TextView custom_telephone = (TextView)v.findViewById(R.id.custom_telephone_tv);
        custom_telephone.setText(data.get(0).get("telephone"));

        final Button save_btn= (Button)v.findViewById(R.id.custom_save_btn);
        save_btn.setText("Принять");
        Button cancelBtn = (Button)v.findViewById(R.id.customCansleBtn);
        cancelBtn.setEnabled(false);
        save_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    change_status();
                }
            });
        TextView custom_text_tv = (TextView)v.findViewById(R.id.custom_name_tv);
        custom_text_tv.setText(data.get(0).get("custom_name"));
        status_spinner = (Spinner) v.findViewById(R.id.custom_spinner);

        TextView statusTv = (TextView)v.findViewById(R.id.custom_status_tv);

        String[] from={"name","cost"};
        int[] to = {R.id.textView4,R.id.textView5};

        SimpleAdapter adapter = new SimpleAdapter(getActivity(),getServicesData(),R.layout.new_custom_list_item, from,to);
        lv.setAdapter(adapter);

        status_spinner.setVisibility(View.INVISIBLE);
        statusTv.setText("Статус: "+sinner_data[Integer.parseInt(data.get(0).get("status"))]);
        return v;
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
    private void change_status()
    {
        try
        {
            conn = new ConnectionClass();
            Log.d("logger",getResources().getString(R.string.baseUrl)+"setCustomWorker&Id="+id+"&Login="+login);
            conn.execute(getResources().getString(R.string.baseUrl)+"setCustomWorker&Id="+id+"&Login="+login);
            if (conn.get().contains("ok"))
                Toast.makeText(getActivity(),"OK",Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex){
            Log.d("logger",ex.toString());
        }
    }
    private ArrayList<Map<String,String>> getCustomData()
    {
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        try
        {
            conn= new ConnectionClass();
            String sql = getResources().getString(R.string.baseUrl)+"getCustomData&Id="+id;
            conn.execute(sql);

            String[] result=conn.get().split(";");
            Map<String,String> map=new HashMap<>();
            map.put("custom_name",result[0]);
            map.put("total_cost",result[1]);
            map.put("status",result[2]);
            map.put("adress",result[3]);
            map.put("telephone",result[4]);
            map.put("services",result[5].substring(0,result[5].length()-1));

            data.add(map);

        }
        catch (Exception ex) {
            Log.d("Error custom", ex.toString());
        }
        return data;
    }
    private ArrayList<Map<String,String>> getServicesData()
    {
        ArrayList<Map<String, String>> service_data = new ArrayList<Map<String, String>>();
        try
        {

            conn= new ConnectionClass();
            String servicesId="";
            for (int i =0;i<data.get(0).get("services").split(",").length;i++)
            {
                servicesId+=data.get(0).get("services").split(",")[i];
                if (i<data.get(0).get("services").split(",").length-1)
                    servicesId+=" AND ";
            }

            String sql = getResources().getString(R.string.baseUrl)+"getServicesData&Id="+servicesId;
            conn.execute(sql);
            String[] services_result = conn.get().split("\n");
            Map<String,String> map=new HashMap<>();
            for (int i = 0;i<services_result.length;i++)
            {
                map = new HashMap<String, String>();
                map.put("name", services_result[i].split(";")[0]);
                map.put("cost", services_result[i].split(";")[1]);
                service_data.add(map);
            }
        }
        catch (Exception ex) {
            Log.d("Error custom", ex.toString());
        }
        return service_data;
    }
}
