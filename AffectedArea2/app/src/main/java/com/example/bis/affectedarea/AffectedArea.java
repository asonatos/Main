package com.example.bis.affectedarea;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BIS on 22.01.2018.
 */
public class AffectedArea {
    Context ctx;
    ArrayList<Map<String,Integer>> poisonList = new ArrayList<>();
    double atmosphere_state,h;
    boolean snow,type,is_gaz;
    int horizon,time,temperature,aftertime,wind_speed;
    public double setAtmosphereState(boolean cloud,boolean is_small)
    {
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("HH");
        int time = Integer.parseInt(df.format(Calendar.getInstance().getTime()));
        if(wind_speed<2)
        {
            if(((time>=0&&time<6)&&!cloud)||(time>22&&!cloud)||(snow&&(time>6&&time<8)&&!cloud))
                atmosphere_state=1;
            else {
                if (time > 8 && time < 22 && !cloud && !snow)
                    atmosphere_state = 0.08;
                else atmosphere_state=0.23;
            }
        }
        if (wind_speed>=2&&wind_speed<=3.9)
        {
            if((snow&&(time>=22)&&!cloud)||(time<6&&!cloud)||(snow&&(time>=6&&time<=8)&&!cloud))
                atmosphere_state=1;
            else atmosphere_state=0.23;
        }
        if(wind_speed>4)
        {
            atmosphere_state=0.23;
        }
        if (is_small)
            atmosphere_state=1;
        return atmosphere_state;

    }
    AffectedArea (ArrayList<Map<String,Integer>> poisons,double wind_speed,boolean snow,int horizon,boolean type,boolean cloud,Context context,boolean is_small,int time,double h,boolean is_gaz,int temperature,int N)
    {
        poisonList=poisons;
        if(wind_speed<1)
        {
            this.wind_speed=1;
        }
        else
        {
            if(wind_speed>=15)
                wind_speed=14;
            else
                this.wind_speed=(int)wind_speed;
        }


        this.snow=snow;
        this.horizon = horizon;
        if(time==0)this.time=1;
        else
        this.time=time;
        ctx=context;
        this.is_gaz=is_gaz;
        this.h=h;
        this.temperature=temperature;
        aftertime=N;
        setAtmosphereState(cloud,is_small);
    }
    private double getK6(double T)
    {
        double k6;
        if(T<1)
        {
            T=1;
        }
        if(aftertime<T)
            return Math.pow(aftertime,0.8);
                else return Math.pow(T,0.8);

    }
    private double getV()
    {
        if (atmosphere_state==1)
            return Double.parseDouble(ctx.getResources().getStringArray(R.array.V)[0].split(";")[(int)wind_speed-1]);
        else
            if (atmosphere_state==0.08)
                return Double.parseDouble(ctx.getResources().getStringArray(R.array.V)[2].split(";")[(int)wind_speed-1]);
            else if(wind_speed>=15)
                return Double.parseDouble(ctx.getResources().getStringArray(R.array.V)[1].split(";")[14]);
            else return Double.parseDouble(ctx.getResources().getStringArray(R.array.V)[1].split(";")[(int)wind_speed-1]);
    }
    private double getK7(int i,boolean alt)
    {
        String str;
        if(temperature>=40) {
            str = ctx.getResources().getStringArray(R.array.K7)[poisonList.get(i).get("number")].split(";")[4];
            if(alt)
                return Double.parseDouble(str.split(",")[0]);
            else return Double.parseDouble(str.split(",")[1]);
        }
            else if (temperature>=20){
            str = ctx.getResources().getStringArray(R.array.K7)[poisonList.get(i).get("number")].split(";")[3];
            if(alt)
                return Double.parseDouble(str.split(",")[0]);
            else return Double.parseDouble(str.split(",")[1]);}
            else if (temperature>=0)
        {
            str = ctx.getResources().getStringArray(R.array.K7)[poisonList.get(i).get("number")].split(";")[2];
            if(alt)
                return Double.parseDouble(str.split(",")[0]);
            else return Double.parseDouble(str.split(",")[1]);}
            else if (temperature>=-20)
        {
            str = ctx.getResources().getStringArray(R.array.K7)[poisonList.get(i).get("number")].split(";")[1];
            if(alt)
                return Double.parseDouble(str.split(",")[0]);
            else return Double.parseDouble(str.split(",")[1]);}
            else
        {str = ctx.getResources().getStringArray(R.array.K7)[poisonList.get(i).get("number")].split(";")[0];
            if(alt)
                return Double.parseDouble(str.split(",")[0]);
            else return Double.parseDouble(str.split(",")[1]);}
    }
    private double getK4()
    {
        if(wind_speed>=15)
        {
            return Double.parseDouble(ctx.getResources().getStringArray(R.array.K4)[14]);
        }
        else
            return Double.parseDouble(ctx.getResources().getStringArray(R.array.K4)[(int)wind_speed-1]);
    }
    private double getG(double q1)
    {
        Log.d("getG",q1+"");
        String str;
        int i=0,j=15;
        double buffer=0,result,min=0,max=0;
        if(wind_speed>15)
        {
            str = ctx.getResources().getStringArray(R.array.AffectedArea)[14];
        }
        else
            str = ctx.getResources().getStringArray(R.array.AffectedArea)[(int)wind_speed-1];
        while (q1>buffer)
        {
            buffer= Double.parseDouble(ctx.getResources().getStringArray(R.array.AffectedArea)[15].split(";")[i]);
            i++;
            min=buffer;
        }
        i--;
        buffer=3000;
        while (q1<=buffer)
        {
            buffer= Double.parseDouble(ctx.getResources().getStringArray(R.array.AffectedArea)[15].split(";")[j]);
            j--;
            max=buffer;
        }
        j++;
        result=Double.parseDouble(str.split(";")[i])+((Double.parseDouble(str.split(";")[j])-Double.parseDouble(str.split(";")[i]))/(max-min)*(q1-min));
        return result;
    }
    public double onDamage()
    {
        double rez=0;
        double g1,g2;
        for (int i=0;i<poisonList.size();i++)
        {
            double Q1 = Double.parseDouble(ctx.getResources().getStringArray(R.array.K1)[poisonList.get(i).get("number")])*
                    Double.parseDouble(ctx.getResources().getStringArray(R.array.K3)[poisonList.get(i).get("number")])*
                    atmosphere_state*
                    getK7(i,true)*poisonList.get(i).get("mass");
            if (is_gaz)
            {
                g1=getG(Q1);
                double gmax=time*getV();
                rez = Math.min(g1,gmax);
            }
            else {
                double T = (h*getRo(i))
                        /(Double.parseDouble(ctx.getResources().getStringArray(R.array.K2)[poisonList.get(i).get("number")])*
                        getK4()*
                        getK7(i,true));
                double Q2 = (1 - Double.parseDouble(ctx.getResources().getStringArray(R.array.K1)[poisonList.get(i).get("number")])) *
                        Double.parseDouble(ctx.getResources().getStringArray(R.array.K2)[poisonList.get(i).get("number")]) *
                        Double.parseDouble(ctx.getResources().getStringArray(R.array.K3)[poisonList.get(i).get("number")]) *
                        getK4() *
                        atmosphere_state *
                        getK6(T)*getK7(i,false)*(poisonList.get(i).get("mass")/(h*Double.parseDouble(ctx.getResources().getStringArray(R.array.Ro)[poisonList.get(i).get("number")])));
                g1 = getG(Q1);
                g2 = getG(Q2);
                double gres = Math.max(g1,g2)+0.5*Math.min(g1,g2);
                double gmax=time*getV();
                rez = Math.min(gres,gmax);
            }
        }
        return rez;
    }
    private double getRo(int i)
    {
        if (is_gaz)
            return Double.parseDouble(ctx.getResources().getStringArray(R.array.RoGaz)[poisonList.get(i).get("number")]);
        else  return Double.parseDouble(ctx.getResources().getStringArray(R.array.Ro)[poisonList.get(i).get("number")]);
    }
    public double onDestroy()
    {
        double Q = 20*getK4()*atmosphere_state,sum=0;
        for (int i=0;i<poisonList.size();i++)
        {

            double T = (h*getRo(i))
                    /(Double.parseDouble(ctx.getResources().getStringArray(R.array.K2)[poisonList.get(i).get("number")])*
                    getK4()*
                    getK7(i,true));

                sum+=Double.parseDouble(ctx.getResources().getStringArray(R.array.K2)[poisonList.get(i).get("number")])*
                        Double.parseDouble(ctx.getResources().getStringArray(R.array.K3)[poisonList.get(i).get("number")])*
                        getK6(T)*getK7(i,true)*(poisonList.get(i).get("mass")/getRo(i));


        }
        Q*=sum;
        double gmax=aftertime*getV();
        return Math.min(getG(Q),gmax);
    }
}
