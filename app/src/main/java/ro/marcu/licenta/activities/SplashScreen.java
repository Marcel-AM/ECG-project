package ro.marcu.licenta.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import ro.marcu.licenta.R;

public class SplashScreen extends AppCompatActivity {
    private Handler mWaitHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mWaitHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                try {

                    goToMainScreen();

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

    private void goToMainScreen(){
        Intent intent = new Intent(getApplicationContext(), FirstScreen.class);
        startActivity(intent);
    }
}