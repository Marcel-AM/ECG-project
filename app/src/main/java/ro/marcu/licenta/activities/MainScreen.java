package ro.marcu.licenta.activities;

import static ro.marcu.licenta.activities.FirstScreen.INTENT_KEY_MAIL;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ro.marcu.licenta.R;
import ro.marcu.licenta.cloudData.BpmData;

public class MainScreen extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    private int[] value = new int[250];

    private int heartRate;
    private int heartRate0;
    private int heartRate1;
    private int heartRate2;
    private int heartRate3;
    private int heartRateAvg;
    private long lastBeatTime;
    private int count;

    private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    private boolean validateBPM = false;

    private TextView readyBpm, timerText, textBpm, mainEmail;
    private ImageView bpmScreen, healthScreen;
    private View backgroundBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        mainEmail = findViewById(R.id.main_email);

        if (getIntent() != null) {
            String receiveMail = getIntent().getStringExtra(INTENT_KEY_MAIL);
            mainEmail.setText(receiveMail);
        }

        lastBeatTime = 0;
        heartRate = 70;
        count = 0;

        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("ECGdata/NjdaLPTfQ1NU5c5uWASjK4Zredh1/readings");

        bpmScreen = findViewById(R.id.bpm_bar);
        healthScreen = findViewById(R.id.health_api);

        //startReadingData();
        timerText = findViewById(R.id.text_timer);
        readyBpm = findViewById(R.id.text_ready);
        textBpm = findViewById(R.id.heart_value);
        backgroundBt = findViewById(R.id.background_ready);

        mChart = (LineChart) findViewById(R.id.ecg_chart);

        countTimeBPM();

        bpmScreen.setOnClickListener(view -> goToBPMScreen());

        healthScreen.setOnClickListener(view -> goToHealthScreen());

        readyBpm.setOnClickListener(view -> {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            insertBPMInDatabase(mainEmail.getText().toString().trim(), textBpm.getText().toString().trim(), sdf1.format(timestamp));
        });

        settingsChart();

        feedMultiple();

    }

    public void insertBPMInDatabase(String contactEmail, String bpm, String dateTime) {
        BpmData data = new BpmData(contactEmail, bpm, dateTime);

        Map<String, Object> dataToInsert = new HashMap<>();
        dataToInsert.put("email", data.getEmail());
        dataToInsert.put("bpm", data.getBpm());
        dataToInsert.put("time", data.getDateTime());

        db.collection("BPM_data")
                .add(dataToInsert)
                .addOnSuccessListener(documentReference -> {
                    Log.d("destinationInserted", "success");
                    countTimeBPM();
                    Toast.makeText(this, "Your BPM data was sent with success !", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Your BPM data was not sent !", Toast.LENGTH_SHORT).show();
                });

    }

    private void feedMultiple() {

        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    plotData = true;
                    try {
                        Thread.sleep(1000);
                        startReadingData();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }

    }

    @Override
    protected void onDestroy() {
        thread.interrupt();
        super.onDestroy();
    }


    private void countTimeBPM() {
        long duration = TimeUnit.SECONDS.toMillis(10);
        backgroundBt.setVisibility(View.GONE);
        readyBpm.setVisibility(View.GONE);

        new CountDownTimer(duration, 10) {
            @Override
            public void onTick(long l) {
                String sDuration = String.format(Locale.ENGLISH, "%01d", TimeUnit.MILLISECONDS.toSeconds(l));
                timerText.setText(sDuration + " sec. to the end ...");
            }

            @Override
            public void onFinish() {
                //stabilizeBPM();
                validateBPM = true;
                backgroundBt.setVisibility(View.VISIBLE);
                readyBpm.setVisibility(View.VISIBLE);
                //Toast.makeText(getApplicationContext(), "Ready to save BPM data", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    private void stabilizeBPM() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (lastBeatTime != 0) {
            heartRate = (int) (heartRate + ((60000.0 / (currentTime - lastBeatTime)) / 2)) / 2;
            if (count == 0) {
                textBpm.setText(Integer.toString(heartRate));
                count++;
            }
            if (count == 1) {
                heartRate3 = heartRate;
                count++;
            }
            if (count == 2) {
                heartRate2 = heartRate;
                count++;
            }
            if (count == 3) {
                heartRate1 = heartRate;
                count++;
            }
            if (count == 4) {
                heartRate0 = heartRate;
                count = 1;
                heartRateAvg = ((heartRate0 + heartRate1 + heartRate2 + heartRate3) / 4) + 30;
                textBpm.setText(Integer.toString(heartRateAvg));
            }
        }
        lastBeatTime = currentTime;
    }

    private void startReadingData() {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot key : snapshot.getChildren()) {
                    value[i] = key.getValue(Integer.class);
                    i++;
                }

                if (plotData) {
                    addEntryECG(value);
                    stabilizeBPM();
                    plotData = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }


    private void addEntryECG(int[] aux) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), aux[0]), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(10);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

        }

    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.RED);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }


    private void settingsChart() {
        // enable description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(false);

        // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setEnabled(false);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setAxisLineColor(Color.WHITE);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setAxisMaximum(7000f);
        leftAxis.setAxisMinimum(-5000f);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setAxisMaximum(7000f);


        mChart.getAxisLeft().setDrawGridLines(true);
        mChart.getAxisLeft().setGridColor(Color.YELLOW);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);
    }

    private void goToBPMScreen() {
        Intent intentGoToDashboard = new Intent(this, BPMScreen.class);
        startActivity(intentGoToDashboard);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void goToHealthScreen() {
        Intent intentGoToDashboard = new Intent(this, HealthScreen.class);
        startActivity(intentGoToDashboard);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
    }
}