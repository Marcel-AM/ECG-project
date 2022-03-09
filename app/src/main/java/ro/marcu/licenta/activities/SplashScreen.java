package ro.marcu.licenta.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import ro.marcu.licenta.R;

public class SplashScreen extends AppCompatActivity {
    private Handler mWaitHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (!isConnected(SplashScreen.this)) {
            showCustumDialog();
        }

        mWaitHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                try {
                    goToFirstScreen();

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

    private void goToFirstScreen() {
        Intent intent = new Intent(getApplicationContext(), FirstScreen.class);
        startActivity(intent);
    }


    private boolean isConnected(SplashScreen splashScreen) {

        ConnectivityManager connectivityManager = (ConnectivityManager) splashScreen.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    private void showCustumDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
        builder.setMessage("Please connect to the internet to proceed further")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), MainScreen.class));
                        finish();
                    }
                });
    }
}