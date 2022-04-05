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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.math.BigDecimal;

import cz.msebera.android.httpclient.Header;
import pl.droidsonroids.gif.GifImageView;
import ro.marcu.licenta.R;
import ro.marcu.licenta.api.WeatherData;

public class HealthScreen extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "HealthScreen";

    private final String API_ID = "d17a1799e816048e7942c9ef63ed9635";
    private final String WEB_URL = "https://api.openweathermap.org/data/2.5/weather";
    private final long MIN_TIME = 5000;
    private final long MIN_DISTANCE = 1000;

    private static final int PERMISSION_STEPS_COUNT = 100;
    private static final int PERMISSION_LOCATION_ACCESS = 200;

    private TextView steps, kilometers, calories, progressPercent;
    private TextView temp, location, weatherState;
    private EditText target;
    private Button resetSteps, applyTarget;
    private ProgressBar progressSteps;
    private GifImageView weatherIcon;

    private SensorManager sensorManager;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private boolean stepsRunning = false;


    private float totalSteps = 0;
    private float previousTotalSteps = 0;
    private int auxSteps = 0;

    private String locationProvider = LocationManager.GPS_PROVIDER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_screen);

        steps = findViewById(R.id.steps);
        kilometers = findViewById(R.id.kilometers);
        calories = findViewById(R.id.calories);

        temp = findViewById(R.id.grade_celsius);
        location = findViewById(R.id.location_text);
        weatherState = findViewById(R.id.location_state);

        weatherIcon = (GifImageView) findViewById(R.id.weather_icon);
        target = findViewById(R.id.input_steps);

        progressSteps = findViewById(R.id.steps_progressBar);
        progressPercent = findViewById(R.id.progress_percentage);

        resetSteps = findViewById(R.id.reset_countSteps);
        applyTarget = findViewById(R.id.apply_target);

        loadData();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        String[] permission1 = {Manifest.permission.ACTIVITY_RECOGNITION};
        //String[] permission2 = {Manifest.permission.ACCESS_FINE_LOCATION};
        askPermission(permission1, PERMISSION_STEPS_COUNT);
        // askPermission(permission2, PERMISSION_LOCATION_ACCESS);

        resetSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                previousTotalSteps = totalSteps;

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
        if (stepsRunning) {
            //running = true;
            Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (countSensor != null) {
                sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);

            } else {
                Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
            }
        }


        getWeatherForCurrentLocation();

    }


    @Override
    protected void onPause() {
        super.onPause();
        stepsRunning = false;

        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }

        //for stop counting the steps
        //sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (stepsRunning && sensorEvent != null) {
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

        String targetValue = target.getText().toString().trim();

        if (targetValue.isEmpty()) {
            target.setError("Target required !");
            target.requestFocus();
        } else {
            steps.setText("0");
            editor.putFloat("key1", previousTotalSteps);
            editor.putString("key2", targetValue);
            editor.apply();
        }

    }

    private void saveTarget() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String targetValue = target.getText().toString().trim();

        if (targetValue.isEmpty()) {
            target.setError("Target required !");
            target.requestFocus();
        } else {
            editor.putString("key2", targetValue);
            editor.apply();
        }

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

        String targetValue = target.getText().toString().trim();

        if (targetValue.isEmpty()) {
            target.setError("Target required !");
            target.requestFocus();
        } else {
            int auxTarget = Integer.parseInt(targetValue);

            progressSteps.setMax(auxTarget);

            progressSteps.setProgress(steps);


            //int percentage = ((steps + 1) * 100) / auxTarget;

            //progressPercent.setText(String.valueOf(percentage) + "%");

            Log.d(TAG, "Target: " + auxTarget);
        }


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

    private void getWeatherForCurrentLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", API_ID);
                networking(params);

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_ACCESS);
            return;
        }

        locationManager.requestLocationUpdates(locationProvider, MIN_TIME, MIN_DISTANCE, locationListener);

    }

    private void networking(RequestParams params) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEB_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Toast.makeText(HealthScreen.this, "Data get success", Toast.LENGTH_SHORT).show();

                WeatherData weather = WeatherData.fromJSON(response);
                updateUI(weather);
                //super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                Toast.makeText(HealthScreen.this, "Fail to get data !", Toast.LENGTH_SHORT).show();
                //super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    private void updateUI(WeatherData data) {
        temp.setText(data.getTemperature());
        location.setText(data.getCity());
        weatherState.setText(data.getWeatherType());

        int getResourceID = getResources().getIdentifier(data.getIcon(), "drawable", getPackageName());
        weatherIcon.setImageResource(getResourceID);
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
                stepsRunning = true;
            } else {
                Toast.makeText(HealthScreen.this, "I need this permission for counting the steps !", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PERMISSION_LOCATION_ACCESS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(HealthScreen.this, "Permission granted, thank you !", Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            } else {
                Toast.makeText(HealthScreen.this, "I need this permission for weather !", Toast.LENGTH_SHORT).show();
            }
        }

    }
}