package com.example.bis.bisnescalculate;

import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by BIS on 21.02.2018.
 */
public class Calculation {
    ArrayList<Double>data = new ArrayList<>();
    Calculation (ArrayList<Double> data)
    {
        this.data.addAll(data);
    }
    public ArrayList<Double> getResult()
    {
        ArrayList<Double> result = new ArrayList<>();
        result.add(data.get(0)-data.get(2));//
        result.add(data.get(1)/(data.get(2)*data.get(6)/100));//
        result.add(1/result.get(1));//
        result.add(data.get(1)/((data.get(3)*data.get(5))/100));//
        result.add(data.get(3)*data.get(5)/100/data.get(1));//
        result.add(data.get(3)*data.get(5)/100/data.get(7));//
        result.add(result.get(0)/data.get(2)*100);//
        result.add(result.get(0)/data.get(0)*100);//
        result.add(result.get(0)/(data.get(3)*data.get(4))*10000);//
        result.add(result.get(0)/(data.get(3)*(100-data.get(4)))*10000);
        result.add(result.get(0)/(data.get(3)*data.get(5)/100)*100);
        result.add(data.get(0)/((100-data.get(5))*data.get(3)/100));//
        result.add(360/result.get(11));//
        result.add(data.get(1)/data.get(7));//
        for (int i=0;i<result.size();i++)
        {
            Log.d("logger",result.get(i)+"");
        }
        return result;
    }
}
