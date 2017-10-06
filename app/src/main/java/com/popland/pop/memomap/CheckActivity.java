package com.popland.pop.memomap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Random;

public class CheckActivity extends AppCompatActivity {
TextView tvCheck;
Button btnCheck, btnFeedback , btnVideoAd, btnMap;
    private AdView mAdView;
int r;
    Random random = new Random();
InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        //ca-app-pub-7964058081042027~7387079512
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        tvCheck = (TextView)findViewById(R.id.tvCheck);
        btnCheck = (Button)findViewById(R.id.btnCheck);
        btnFeedback = (Button)findViewById(R.id.btnFeedback);
        btnMap = (Button)findViewById(R.id.btnMap);

        r = random.nextInt(110);
        tvCheck.setText(getResources().getString(R.string.infoat)+"  "+r+" ?");
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r = random.nextInt(110);
                tvCheck.setText(getResources().getString(R.string.infoat)+"  "+r+" ?");
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CheckActivity.this,MapActivity.class);
                startActivity(i);
            }
        });

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i = new Intent(CheckActivity.this,WebActivity.class);
                startActivity(i);
            }
        });

        mInterstitialAd = new InterstitialAd(CheckActivity.this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewAd();
            }

            @Override
            public void onAdLoaded() {
                if(btnVideoAd != null)
                   btnVideoAd.setEnabled(true);// allow click
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Toast.makeText(CheckActivity.this,"Load Failed",Toast.LENGTH_SHORT).show();
            }
        });

        btnVideoAd = (Button)findViewById(R.id.btnVideoAd);
        btnVideoAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInterstitialAd.isLoaded())
                   mInterstitialAd.show();
            }
        });

        btnVideoAd.setEnabled(mInterstitialAd.isLoaded());
    }

    public void requestNewAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {

    }
}
