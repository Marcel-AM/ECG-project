package ro.marcu.licenta.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import ro.marcu.licenta.R;

public class BPMScreen extends AppCompatActivity {

    private ImageView mainScreen, healthScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpmscreen);

        mainScreen = findViewById(R.id.eck_chart);
        healthScreen = findViewById(R.id.health_api);

        mainScreen.setOnClickListener(view -> goToMainScreen());
        healthScreen.setOnClickListener(view -> goToHealthScreen());
    }

    private void goToMainScreen() {
        Intent intentGoToDashboard = new Intent(this, MainScreen.class);
        startActivity(intentGoToDashboard);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goToHealthScreen() {
        Intent intentGoToDashboard = new Intent(this, HealthScreen.class);
        startActivity(intentGoToDashboard);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}