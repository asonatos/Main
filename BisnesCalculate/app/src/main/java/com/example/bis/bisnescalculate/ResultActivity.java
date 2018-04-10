package com.example.bis.bisnescalculate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    ArrayList<String> data=new ArrayList<>();
    ArrayList<Map<String,String >> adapterData=new ArrayList<>();
    ListView lv;
    String[] from ={"name","value"};
    int[] to = {R.id.resultNameTv,R.id.resultResTv};
    String[] names={"Прибыль","Материалоотдача","Материалоемкость","Фодоотдача","Фондоемкость","Фондовооруженность","Рентабельность затрат","Рентабельность продаж","Рентабельность собственных средств","Рентабельность заемных средств","Фондорентабельность","Коэффициент оборачиваемости","Продолжительность оборота","Производительность труда"};
    String[] units = {"руб","руб","руб","руб","руб","руб","%","%","%","%","%","","дней","руб/чел"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        data.addAll(getIntent().getStringArrayListExtra("list"));
        lv=(ListView)findViewById(R.id.resultLv);
        SimpleAdapter adapter = new SimpleAdapter(this,setAdapterData(),R.layout.result_list_item,from,to);
        lv.setAdapter(adapter);

    }
    private ArrayList<Map<String,String>> setAdapterData()
    {
        Map<String,String> m;
        for (int i =0;i<data.size();i++)
        {
            m=new HashMap<>();
            m.put(from[0],names[i]);
            m.put(from[1],data.get(i)+" "+units[i]);
            adapterData.add(m);
        }
        return adapterData;
    }
}
