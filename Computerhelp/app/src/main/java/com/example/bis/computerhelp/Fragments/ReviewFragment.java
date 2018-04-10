package com.example.bis.computerhelp.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bis.computerhelp.ConnectionClass;
import com.example.bis.computerhelp.EventListener;
import com.example.bis.computerhelp.R;

/**
 * Created by BIS on 05.01.2018.
 */
public class ReviewFragment extends Fragment {
    EventListener mListener;
    String customId,customName,workerLogin,login;
    ConnectionClass conn;
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
    public void setData(String id,String name,String workerLogin,String login)
    {
        customId=id;
        customName=name;
        this.workerLogin=workerLogin;
        this.login=login;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.review_layout,container,false);
        TextView tv = (TextView)v.findViewById(R.id.reviewNameTv);
        tv.setText(customName);
        final EditText reviewTextEd = (EditText)v.findViewById(R.id.reviewTextEd);
        Button saveBtn = (Button)v.findViewById(R.id.reviewSaveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_review(reviewTextEd.getText().toString().replaceAll(" ","%20"));
            }
        });
        return v;
    }
    private void save_review(String text)
    {
        try
        {
            if (text.length()==0)
                Toast.makeText(getActivity(),"Введите текст",Toast.LENGTH_SHORT).show();
            else {
                conn = new ConnectionClass();
                conn.execute(getResources().getString(R.string.baseUrl) + "setReview&CustomId=" + customId + "&Text=" + text+"&WorkerLogin="+workerLogin+"&login="+login);
                if (conn.get().contains("ok"))
                    Toast.makeText(getActivity(), "Сохранено", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex){}
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (conn!=null)
            conn.cancel(true);
    }
}
