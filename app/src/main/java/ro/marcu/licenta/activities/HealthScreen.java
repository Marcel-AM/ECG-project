package ro.marcu.licenta.activities;

import static ro.marcu.licenta.activities.MainScreen.redirectActivity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.math.BigDecimal;

import ro.marcu.licenta.R;

public class HealthScreen extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "HealthScreen";

    private static final int PERMISSION_STEPS_COUNT = 100;

    private TextView steps, kilometers, calories, progressPercent;
    private EditText target;
    private Button resetSteps, applyTarget;
    private ProgressBar progressSteps;

    private SensorManager sensorManager;
    private boolean running = false;
    private float totalSteps = 0;
    private float previousTotalSteps = 0;
    private int auxSteps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_screen);

        steps = findViewById(R.id.steps);
        kilometers = findViewById(R.id.kilometers);
        calories = findViewById(R.id.calories);

        target = findViewById(R.id.input_steps);

        progressSteps = findViewById(R.id.steps_progressBar);
        progressPercent = findViewById(R.id.progress_percentage);

        resetSteps = findViewById(R.id.reset_countSteps);
        applyTarget = findViewById(R.id.apply_target);

        loadData();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        String[] permission = {Manifest.permission.ACTIVITY_RECOGNITION};
        askPermission(permission, PERMISSION_STEPS_COUNT);

        resetSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousTotalSteps = totalSteps;
                steps.setText("0");
                saveData();
            }
        });

        applyTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTarget();
                settingProgressBar(auxSteps);
            }
        });


        ImageButton bpmScreen = findViewById(R.id.bpm_bar);
        ImageButton mainScreen = findViewById(R.id.eck_chart);

        mainScreen.setOnClickListener(view -> goToMainScreen());
        bpmScreen.setOnClickListener(view -> goToBPMScreen());
    }

    private void goToMainScreen() {
        redirectActivity(this, MainScreen.class);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void goToBPMScreen() {
        redirectActivity(this, BPMScreen.class);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (running) {
            //running = true;
            Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (countSensor != null) {
                sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);

            } else {
                Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        //for stop counting the steps
        //sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (running && sensorEvent != null) {
            //steps.setText(String.valueOf(sensorEvent.values[0]));
            totalSteps = sensorEvent.values[0];

            int currentSteps = Math.round(totalSteps) - Math.round(previousTotalSteps);
            steps.setText(String.valueOf(currentSteps));

            auxSteps = currentSteps;

            stepsCalculating();
            settingProgressBar(currentSteps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat("key1", previousTotalSteps);
        editor.putString("key2", target.getText().toString().trim());
        editor.apply();
    }

    private void saveTarget() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("key2", target.getText().toString().trim());
        editor.apply();
    }


    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        float savedNumber = sharedPreferences.getFloat("key1", 0f);
        String savedTarget = sharedPreferences.getString("key2", "0");

        Log.d(TAG, "Saved number: " + savedNumber);
        Log.d(TAG, "Saved target: " + savedTarget);

        previousTotalSteps = savedNumber;
        target.setText(savedTarget);

    }

    private void settingProgressBar(int steps) {
        int auxTarget = Integer.parseInt(target.getText().toString());

        progressSteps.setMax(auxTarget);

        progressSteps.setProgress(steps);

        int percentage = (steps * 100) / auxTarget;

        progressPercent.setText(String.valueOf(percentage) + "%");

        Log.d(TAG, "Target: " + auxTarget);

    }

    private void stepsCalculating() {

        BigDecimal resultKm, resultKcal;

        int localSteps = Integer.parseInt(steps.getText().toString().trim());

        float km = (localSteps * 0.75f) / 1000;
        float caloriesBurned = (localSteps * 0.05f) / 1000;

        resultKm = round(km, 2);
        resultKcal = round(caloriesBurned, 2);


        kilometers.setText(String.valueOf(resultKm));
        calories.setText(String.valueOf(resultKcal));


    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    private void askPermission(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(HealthScreen.this, permissions, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_STEPS_COUNT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(HealthScreen.this, "Permission granted, thank you !", Toast.LENGTH_SHORT).show();
                running = true;
            } else {
                Toast.makeText(HealthScreen.this, "I need this permission for counting the steps !", Toast.LENGTH_SHORT).show();
            }
        }

    }
}