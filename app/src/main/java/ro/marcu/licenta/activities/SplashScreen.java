package ro.marcu.licenta.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private final Handler mWaitHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mWaitHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                try {
                    startActivity(new Intent(SplashScreen.this, FirstScreen.class));
                    finish();

                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Remove all the callbacks otherwise navigation will execute even after activity is killed or closed.
        mWaitHandler.removeCallbacksAndMessages(null);
    }
}