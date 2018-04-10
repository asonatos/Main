package com.example.bis.bisnescalculate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    LinearLayout lv;
    Button btn;
    ArrayList<EditText> editmass;
    String[] names = {"Выручка","Выпуск продукции","Себестоимость","Сумма всех средств","Доля собственных средств","Доля основных средств","Доля мат. затрат в себестоимости","Численность работников"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (LinearLayout) findViewById(R.id.inputListView);
        btn = (Button)findViewById(R.id.input_list_btn);
        btn.setOnClickListener(this);
        makeList();
    }
    private void makeList()
    {
        editmass=new ArrayList<>();
        for(int i=0;i<names.length;i++)
        {
            View v = getLayoutInflater().inflate(R.layout.input_list_item,null);
            EditText ed = v.findViewById(R.id.list_item_edit);
            editmass.add(ed);
            lv.addView(v);
            TextView tv= (TextView)v.findViewById(R.id.list_item_tv);
            tv.setText(names[i]);
        }
    }
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.input_list_btn:
                calculate();

        }
    }
    private void calculate()
    {
        try {
            ArrayList<Double> list = new ArrayList<>();
            for (int i = 0; i < editmass.size(); i++) {
                list.add(Double.parseDouble(editmass.get(i).getText().toString()));
            }
            Calculation calculation = new Calculation(list);
            openResult(calculation.getResult());
        }
        catch (Exception ex)
        {
            Toast.makeText(this,"Неверные данные",Toast.LENGTH_SHORT).show();
        }
    }
    private void openResult(ArrayList<Double> resData)
    {
        ArrayList<String> buf=new ArrayList<>();
        for (int i = 0;i<resData.size();i++)
        {
            double asd = resData.get(i)*1000;
            asd=Math.round(asd);
            buf.add((asd/1000)+"");
        }
        Intent intent = new Intent(this,ResultActivity.class);
        intent.putStringArrayListExtra("list",buf);
        startActivity(intent);
    }

}
