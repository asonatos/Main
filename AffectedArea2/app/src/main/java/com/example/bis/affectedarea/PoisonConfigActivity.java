package com.example.bis.affectedarea;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PoisonConfigActivity extends AppCompatActivity {
    String[] wind_data = {"Северный","Северно-восточный","Восточный","Юго-восточный","Южный","Юго-западный","Западный","Северо-западный"};
    EditText radiusEd,powerEd;
    ArrayList<Map<String,String>> listdata;
    ArrayList<EditText> editList = new ArrayList<>();
    LinearLayout poisonList;
    CheckBox is_small,gaz;
    RadioGroup Rg;
    ArrayList<Integer> name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        Button addPoisonbtn = (Button)findViewById(R.id.addpoisonbtn);
        addPoisonbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PoisonListActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        poisonList = (LinearLayout) findViewById(R.id.listView);

        Rg = (RadioGroup)findViewById(R.id.typeRg);
        RadioButton rgTrue=(RadioButton)findViewById(R.id.RgTrue);
        rgTrue.setChecked(true);
        is_small = (CheckBox)findViewById(R.id.isSmallCh);
        gaz = (CheckBox)findViewById(R.id.isGazCheck);
        final EditText Hed = (EditText)findViewById(R.id.Hed);
        Button next = (Button)findViewById(R.id.button);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(name==null) {
                        Toast.makeText(getApplicationContext(),"Выберите вещества",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    boolean type = false;
                    int id = Rg.getCheckedRadioButtonId();
                    Intent intent = new Intent(getApplicationContext(), AtmosphereConfigActivity.class);
                    intent.putIntegerArrayListExtra("number", name);
                    intent.putIntegerArrayListExtra("mass", getPoisons());
                    intent.putExtra("is_small", is_small.isChecked());
                    if (Hed.getText().toString().length() == 0) {
                        intent.putExtra("h", 0.05);
                    } else
                        intent.putExtra("h", Double.parseDouble(Hed.getText().toString()));
                    intent.putExtra("is_gazz", gaz.isChecked());
                    if (id == R.id.RgTrue)
                        type = true;
                    intent.putExtra("type", type);
                    startActivity(intent);
                } catch (NumberFormatException ex){
                    Toast.makeText(getApplicationContext(),"Введите данные о массе",Toast.LENGTH_SHORT).show();}

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data)
    {
        if (data==null) return;
        poisonList.removeAllViewsInLayout();
        name =new ArrayList<>();
        editList= new ArrayList<>();
        name.addAll(data.getIntegerArrayListExtra("names"));
        for (int i = 0;i<name.size();i++) {
            View view = getLayoutInflater().inflate(R.layout.config_list_item, null);
            TextView tv = (TextView) view.findViewById(R.id.list_item_name_tv);
            tv.setText(getResources().getStringArray(R.array.names)[name.get(i)]);
            EditText ed = (EditText)view.findViewById(R.id.list_item_mass_ed);
            editList.add(ed);
            poisonList.addView(view);
        }
    }
    ArrayList<Integer> getPoisons()
    {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i=0;i<editList.size();i++)
        {
            result.add(Integer.parseInt(editList.get(i).getText().toString()));
        }
        return result;
    }
}
