package ro.marcu.licenta.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import ro.marcu.licenta.R;

public class HealthScreen extends AppCompatActivity {

    private ImageView bpmScreen, mainScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_screen);

        bpmScreen = findViewById(R.id.bpm_bar);
        mainScreen = findViewById(R.id.eck_chart);

        mainScreen.setOnClickListener(view -> goToMainScreen());
        bpmScreen.setOnClickListener(view -> goToBPMScreen());
    }

    private void goToMainScreen() {
        Intent intentGoToDashboard = new Intent(this, MainScreen.class);
        startActivity(intentGoToDashboard);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void goToBPMScreen() {
        Intent intentGoToDashboard = new Intent(this, BPMScreen.class);
        startActivity(intentGoToDashboard);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}