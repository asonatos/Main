package wqear.timetable;


import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

/**
 * Загрузочный экран. Его тема описана в drawable/background_splash
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //запускаем activity InputScreen отвечающую за ввод информации
        startActivity(new Intent(this,InputScreen.class));
        //закрываем текущую activity
        finish();
    }

}

