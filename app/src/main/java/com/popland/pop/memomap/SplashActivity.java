package com.popland.pop.memomap;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
public static SystemDataBase dataBase;
public MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        CountDownTimer cdt = new CountDownTimer(5000,1) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                 Intent i = new Intent(SplashActivity.this,MapActivity.class);
                 startActivity(i);
            }
        }.start();

        dataBase = new SystemDataBase(this,"PalaceSystem.db",null,1);
        dataBase.queryData("CREATE TABLE IF NOT EXISTS MaSy(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "so VARCHAR, keyword VARCHAR, pic BLOB)");
        Cursor cursor = dataBase.getData("SELECT * FROM MaSy");
        if(cursor.getCount()==0){
            for(int i=0;i<110;i++){
                dataBase.Insert_Data(String.valueOf(i),"");
            }
        }

        mp = MediaPlayer.create(this,R.raw.nhacnen);
        mp.start();
        mp.setLooping(true);


    }
}
