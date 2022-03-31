package ro.marcu.licenta.activities;

import static ro.marcu.licenta.activities.MainScreen.redirectActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import ro.marcu.licenta.R;

public class HealthScreen extends AppCompatActivity {

    private ImageButton bpmScreen, mainScreen;

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
        redirectActivity(this,MainScreen.class);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void goToBPMScreen() {
        redirectActivity(this,BPMScreen.class);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}