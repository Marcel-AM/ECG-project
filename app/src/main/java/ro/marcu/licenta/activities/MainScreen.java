package ro.marcu.licenta.activities;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ro.marcu.licenta.R;

public class MainScreen extends AppCompatActivity implements SensorEventListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    private int[] value = new int[250];
    private List<Integer> dataValues = new ArrayList<Integer>();

    private int heartRate;
    private int heartRate0;
    private int heartRate1;
    private int heartRate2;
    private int heartRate3;
    private int heartRateAvg;
    private long lastBeatTime;
    private int count = 0;


    private static final String TAG = "MainActivity";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor sensors;

    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    private boolean validateBPM = false;

    private TextView readyBpm, timerText;
    private View backgroundBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("test/int");

        //value = new int[250];

        if (savedInstanceState != null) {
            heartRate = 70;
            lastBeatTime = 0;
        }

        //int[] aux = new int[250];

        startReadingData();
/*
        for (Integer i: dataValues)
        {
            Log.d(TAG, "Value after pulling " + i);
        }

        Log.d(TAG, "Value after pulling " + dataValues.toString());


        //Log.d(TAG, "Value after pulling " + dataValues.toString());

       // addEntryECG();

        timerText = findViewById(R.id.text_timer);
        readyBpm = findViewById(R.id.text_ready);
        backgroundBt = findViewById(R.id.background_ready);

        countTimeBPM();
/*
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for (int i = 0; i < sensors.size(); i++) {
            Log.d(TAG, "onCreate: Sensor " + i + ": " + sensors.get(i).toString());
        }

        if (mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
*/
        mChart = (LineChart) findViewById(R.id.ecg_chart);

        settingsChart();

        feedMultiple();

    }

    private void countTimeBPM() {
        long duration = TimeUnit.SECONDS.toMillis(5);

        new CountDownTimer(duration, 10) {
            @Override
            public void onTick(long l) {
                String sDuration = String.format(Locale.ENGLISH, "%01d", TimeUnit.MILLISECONDS.toSeconds(l));
                timerText.setText(sDuration + " sec. to the end ...");
            }

            @Override
            public void onFinish() {
                validateBPM = true;
                backgroundBt.setVisibility(View.VISIBLE);
                readyBpm.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Ready to save BPM data", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    private void readEkgDataFromDatabase() {
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("test/int");

    }

    private void startReadingData() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot key : snapshot.getChildren()) {
                    value[i] = key.getValue(Integer.class);
                    //aux.add(key.getValue(Integer.class));
                    i++;
                }
                addEntryECG(value);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private void insertBPMInDatabase() {

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

    /*
        private void addEntry(SensorEvent event) {

            LineData data = mChart.getData();

            if (data != null) {

                ILineDataSet set = data.getDataSetByIndex(0);
                // set.addEntry(...); // can be called as well

                if (set == null) {
                    set = createSet();
                    data.addDataSet(set);
                }

    //            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
                data.addEntry(new Entry(set.getEntryCount(), event.values[0] + 5), 0);
                data.notifyDataChanged();

                // let the chart know it's data has changed
                mChart.notifyDataSetChanged();

                // limit the number of visible entries
                mChart.setVisibleXRangeMaximum(100);
                // mChart.setVisibleYRange(30, AxisDependency.LEFT);

                // move to the latest entry
                mChart.moveViewToX(data.getEntryCount());

            }
        }
    */
    private void addEntryECG(int[] aux) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
            data.addEntry(new Entry(set.getEntryCount(), aux[0]), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(100);
            //mChart.setVisibleYRange(30, YAxis.AxisDependency.LEFT);

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
                        Thread.sleep(1);
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
        //mSensorManager.unregisterListener(this);

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        //if (plotData) {
        //addEntry(event);
        //  plotData = false;
        //}
    }


    @Override
    protected void onResume() {
        super.onResume();
        //mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        // mSensorManager.unregisterListener(MainScreen.this);
        thread.interrupt();
        super.onDestroy();
    }
}