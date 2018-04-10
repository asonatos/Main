package com.example.bis.computerhelp;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by BIS on 03.01.2018.
 */
public class LoginActivity extends AppCompatActivity{
    Button loginBtn,registrationBtn;
    EditText loginEd,passwordEd;
    CheckBox isWorkerCb;
    SharedPreferences sPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        if (getIntent().getStringExtra("return")!=null)
        {
            sPref = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("LOGIN", "");
            ed.putString("PASSWORD", "");
            ed.commit();
        }

        sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString("LOGIN", "");
        if(savedText.length()!=0)
        {
            logIn(sPref.getString("LOGIN", ""),sPref.getString("PASSWORD", ""));
        }

        loginBtn = (Button)findViewById(R.id.loginbtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn(loginEd.getText().toString(),passwordEd.getText().toString());
            }
        });
        registrationBtn = (Button)findViewById(R.id.registratebtn);
        registrationBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                registrate(loginEd.getText().toString(),passwordEd.getText().toString());
            }
        });
        loginEd = (EditText)findViewById(R.id.logined);
        passwordEd = (EditText)findViewById(R.id.singInPasswordEd);
        isWorkerCb = (CheckBox)findViewById(R.id.isWorkerCb);
        isWorkerCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(LoginActivity.this);

                    mDialogBuilder
                            .setCancelable(false)
                            .setMessage("Данная функция доступна только при регистрации нового пользователя, после чего с вами свяжется администратор.")
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                        }
                                    });


                    AlertDialog alertDialog = mDialogBuilder.create();

                    alertDialog.show();
                }
            }
        });
        isWorkerCb.setVisibility(View.INVISIBLE);
    }
    private void logIn(String login,String password)
    {
        if(login.length()<=5||password.length()<=5)
        {
            Toast.makeText(this,"Слишком короткие логин или пароль",Toast.LENGTH_SHORT).show();
        }
        else {
            ConnectionClass conn = new ConnectionClass();
            conn.execute((getResources().getString(R.string.baseUrl) + "login&Login=" + login + "&Password=" +password).replaceAll(" ","%20"));
            try {
                if (conn.get().contains("false")) {
                    Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("type", conn.get());
                    intent.putExtra("login",login);

                    sPref = getPreferences(MODE_PRIVATE);
                    String savedText = sPref.getString("LOGIN", "");
                    if(savedText.length()==0)
                    {
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("LOGIN", login);
                        ed.putString("PASSWORD", password);
                        ed.commit();

                    }
                    startActivity(intent);
                    this.finish();
                }

            } catch (Exception ex) {
                Toast.makeText(this,"Ошибка сети",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void registrate(String login,String password)
    {
        if(login.length()<=5||password.length()<=5)
        {
            Toast.makeText(this,"Слишком короткие логин или пароль",Toast.LENGTH_SHORT).show();
        }
        else {
            ConnectionClass conn = new ConnectionClass();
            int isWorker =0;
            if(isWorkerCb.isChecked())
                isWorker=1;
            conn.execute((getResources().getString(R.string.baseUrl) + "registrate&Login=" + loginEd.getText().toString() + "&Password=" + passwordEd.getText().toString() + "&WantsWorker=" + isWorker).replaceAll(" ","%20"));
            try {
                if (conn.get().contains("already use")) {
                    Toast.makeText(this, "Логин уже используется", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("type", "0");
                    intent.putExtra("login",login);
                    sPref = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString("LOGIN", login);
                    ed.putString("PASSWORD", password);
                    ed.commit();
                    startActivity(intent);
                    this.finish();
                }

            } catch (Exception ex) {
                Toast.makeText(this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
