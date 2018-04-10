package com.example.bis.affectedarea;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BIS on 27.01.2018.
 */
public class AtmosphereConfigActivity extends AppCompatActivity {
    String[] wind_data = {"Северный","Северно-восточный","Восточный","Юго-восточный","Южный","Юго-западный","Западный","Северо-западный"};
    EditText windSpeedEd,temperatureEd,aftertimeEd;
    CheckBox cloud,snow;
    ArrayList<Integer> numbers=new ArrayList<>(),mass=new ArrayList<>();
    boolean is_small,type,is_gazz;
    double h;
    Spinner spinner;
    @Override
    public void onCreate(Bundle savedInstantState)
    {
        super.onCreate(savedInstantState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        numbers.addAll(intent.getIntegerArrayListExtra("number"));
        mass.addAll(intent.getIntegerArrayListExtra("mass"));
        is_small=intent.getBooleanExtra("is_small",false);
        h=intent.getDoubleExtra("h",0);
        type=intent.getBooleanExtra("type",false);
        is_gazz = intent.getBooleanExtra("is_gazz",false);
        setContentView(R.layout.atmosfea_config);

        spinner=(Spinner)findViewById(R.id.spinner2);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,wind_data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        cloud=(CheckBox)findViewById(R.id.checkBox4);
        snow = (CheckBox)findViewById(R.id.checkBox3);
        windSpeedEd = (EditText)findViewById(R.id.editText);
        temperatureEd = (EditText)findViewById(R.id.temperaturaEd);
        aftertimeEd = (EditText)findViewById(R.id.afterTimeEd);

        Button btn = (Button)findViewById(R.id.atmosphere_config_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AffectedArea Af = new AffectedArea(getList(), Double.parseDouble(windSpeedEd.getText().toString()), snow.isChecked(), getHorizon(), type, cloud.isChecked(), getApplicationContext(), is_small, getTime(), h, is_gazz, Integer.parseInt(temperatureEd.getText().toString()), Integer.parseInt(aftertimeEd.getText().toString()));
                    if (type) {
                        Log.d("result", Af.onDamage()+"");
                        openMap(Af.onDamage());
                    } else {
                        Log.d("result",Af.onDestroy()+"");
                        openMap(Af.onDestroy());
                    }
                }
                catch (Exception ex)
                {
                    Toast.makeText(getApplicationContext(),"Неверные данные",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private int getTime()
    {
        DateFormat df = new SimpleDateFormat("HH");
        String date = df.format(Calendar.getInstance().getTime());
        return Integer.parseInt(date);
    }
    private int getHorizon()
    {
        return spinner.getSelectedItemPosition()*45;
    }
    private ArrayList<Map<String,Integer>> getList()
    {
        ArrayList<Map<String,Integer>> list = new ArrayList<>();
        Map<String,Integer> map;
        for (int i=0;i<numbers.size();i++)
        {
            map=new HashMap<>();
            map.put("number",numbers.get(i));
            map.put("mass",mass.get(i));
            list.add(map);
        }
        return list;
    }
    private int getAngle()
    {
        if(Double.parseDouble(windSpeedEd.getText().toString())<0.5)
        {
            return 360;
        }
        if(Double.parseDouble(windSpeedEd.getText().toString())>=0.6&&Double.parseDouble(windSpeedEd.getText().toString())<=1)
        {
            return 180;
        }
        else
            if (Double.parseDouble(windSpeedEd.getText().toString())>=1.1&&Double.parseDouble(windSpeedEd.getText().toString())<=2)
                return 90;
            else return 45;

    }
    private void openMap(Double radius)
    {
        int mapType=1;
        if (Double.parseDouble(windSpeedEd.getText().toString())<0.6)
        {
            mapType=0;
        }
        Intent intent = new Intent(this,MapsActivity.class);
        intent.putExtra("radius",radius+"");
        intent.putExtra("type",mapType+"");
        intent.putExtra("horizon",getHorizon()+"");
        intent.putExtra("angle",getAngle()+"");
        startActivity(intent);
    }
}
