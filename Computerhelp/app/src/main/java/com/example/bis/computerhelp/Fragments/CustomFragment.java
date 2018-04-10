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
 * Created by BIS on 05.01.2018.
 */
public class CustomFragment extends Fragment {
    EventListener mListener;
    TextView custom_adress,custom_telephone,statusTv,custom_text_tv;
    Button save_btn,cancel_btn;
    ListView lv;
    Spinner status_spinner;
    ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
    String id="",type="";
    String[] sinner_data={"Ожидает","Принят","Выполняется","Выполнен"};
    String[] worker_sinner_data={"Принят","Выполняется","Выполнен"};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public void setData(String id,String type)
    {
        this.id=id;
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
        View v = inflater.inflate(R.layout.custom_layout,container,false);
        setRetainInstance(true);
        lv = (ListView)v.findViewById(R.id.custom_lv);

        custom_adress = (TextView)v.findViewById(R.id.custom_adress_ed);

        custom_telephone = (TextView)v.findViewById(R.id.custom_telephone_tv);

        cancel_btn = (Button)v.findViewById(R.id.customCansleBtn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ConnectionClass connectionClass = new ConnectionClass();
                    if (type.contains("1")) {
                        connectionClass.execute(getResources().getString(R.string.baseUrl) + "cancelCustom&Id=" + id);
                    } else
                        connectionClass.execute(getResources().getString(R.string.baseUrl) + "deleteCustom&Id=" + id);
                    if (connectionClass.get().contains("ok"))
                    {
                        Toast.makeText(getActivity(),"Удалено",Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getActivity(),"Ошибка",Toast.LENGTH_SHORT).show();
                }
                catch (Exception ex){}
            }
        });

        final Button save_btn= (Button)v.findViewById(R.id.custom_save_btn);
        if (type.contains("1")) {
            save_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    change_status();
                }
            });
        }
        else
        {
            save_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.someEvent("show review;"+id+";"+data.get(0).get("custom_name")+";"+data.get(0).get("workerLogin")+";"+data.get(0).get("login"));
                }
            });
        }
        custom_text_tv = (TextView)v.findViewById(R.id.custom_name_tv);

        status_spinner = (Spinner) v.findViewById(R.id.custom_spinner);

        this.save_btn = (Button)v.findViewById(R.id.custom_save_btn);
        statusTv = (TextView)v.findViewById(R.id.custom_status_tv);

        return v;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        try{
        String[] from={"name","cost"};
        int[] to = {R.id.textView4,R.id.textView5};
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),getServicesData(),R.layout.new_custom_list_item, from,to);
        lv.setAdapter(adapter);
        if (data.size()==0)data.addAll(getCustomData());
        custom_adress.setText(data.get(0).get("adress"));
        custom_telephone.setText(data.get(0).get("telephone"));
        custom_text_tv.setText(data.get(0).get("custom_name"));
        if (type.contains("1"))
        {
            status_spinner.setAdapter(load_spinner_adapter());
            status_spinner.setSelection(Integer.parseInt(data.get(0).get("status"))-1);
            save_btn.setText("Сохранить");
        }
        else {
            if (Integer.parseInt(data.get(0).get("status"))==0)
                save_btn.setEnabled(false);
            status_spinner.setVisibility(View.INVISIBLE);
            statusTv.setText("Статус: "+sinner_data[Integer.parseInt(data.get(0).get("status"))]);
            if (Integer.parseInt(data.get(0).get("status"))==3)
            {
                cancel_btn.setEnabled(false);
            }
        }}
        catch (Exception ex){}
    }
    private void change_status()
    {
        try
        {
            ConnectionClass conn = new ConnectionClass();
            Log.d("logger",getResources().getString(R.string.baseUrl)+"changeStatus&Id="+id+"&Status="+(status_spinner.getSelectedItemPosition()+1));
            conn.execute(getResources().getString(R.string.baseUrl)+"changeStatus&Id="+id+"&Status="+(status_spinner.getSelectedItemPosition()+1));
            if (conn.get().contains("Ok"))
                Toast.makeText(getActivity(),"Ок",Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex){}
    }
    private ArrayList<Map<String,String>> getCustomData()
    {
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        try
        {
            ConnectionClass conn= new ConnectionClass();
            String sql = getResources().getString(R.string.baseUrl)+"getCustomData&Id="+id;
            conn.execute(sql);

            String[] result=conn.get().split(";");
            Map<String,String> map=new HashMap<>();
            map.put("custom_name",result[0]);
            map.put("total_cost",result[1]);
            map.put("status",result[2]);
            map.put("adress",result[3]);
            map.put("telephone",result[4]);
            map.put("services",result[5]);
            map.put("workerLogin",result[6]);
            map.put("login",result[7].substring(0,result[7].length()-1));

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

            ConnectionClass conn= new ConnectionClass();
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
    private ArrayAdapter<String> load_spinner_adapter()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,worker_sinner_data);
        return adapter;
    }

}
