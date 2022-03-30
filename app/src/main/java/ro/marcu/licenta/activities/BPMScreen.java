package ro.marcu.licenta.activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import ro.marcu.licenta.R;
import ro.marcu.licenta.cloudData.BpmData;


public class BPMScreen extends AppCompatActivity {

    private static final String TAG = "BPMScreen";
    private static final DecimalFormat df = new DecimalFormat("#.00");
    private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols();


    private static ArrayList<Integer> auxList = new ArrayList<>();
    private static List<BpmData> dataList;
    private static ArrayList<BarEntry> bpmList = new ArrayList<>();
    private static ArrayList<String> timeStampList = new ArrayList<>();


    private FirebaseAuth mAuth;
    private FirebaseFirestore fireStore;

    private String userID;
    private String userEmail;

    private ImageView mainScreen, healthScreen;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpmscreen);

        mAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        barChart = findViewById(R.id.barchart);

        mainScreen = findViewById(R.id.eck_chart);
        healthScreen = findViewById(R.id.health_api);

        mainScreen.setOnClickListener(view -> goToMainScreen());
        healthScreen.setOnClickListener(view -> goToHealthScreen());


        getBPMFromFirestore();


    }

    private void displayBarChart(ArrayList<BarEntry> bpmList) {

        BarDataSet barDataSet = new BarDataSet(bpmList, "BPM");
        barDataSet.setColor(Color.parseColor("#E83A14")); //app_orange
        barDataSet.setValueTextSize(12f);

        BarData barData = new BarData(barDataSet);
        barData.setValueTextColor(Color.parseColor("#E52528")); //app_red
        barData.setBarWidth(0.5f);

        settingBarChart(barData);

    }

    private void settingBarChart(BarData barData) {
        barChart.setFitBars(true);
        barChart.setData(barData);
        //barChart.invalidate();
        //barChart.notifyDataSetChanged();


        // enable description text
        barChart.getDescription().setEnabled(false);


        // enable touch gestures
        barChart.setTouchEnabled(true);

        // enable scaling and dragging
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setDrawGridBackground(false);

        barChart.setMaxVisibleValueCount(100);

        // if disabled, scaling can be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        // set an alternative background color
        barChart.setBackgroundColor(Color.WHITE);


        // get the legend (only possible after setting data)
        Legend l = barChart.getLegend();
        l.setEnabled(false);

        XAxis xl = barChart.getXAxis();
        xl.setTextColor(Color.parseColor("#E83A14")); //app_orange
        xl.setAxisLineColor(Color.WHITE);
        xl.setAvoidFirstLastClipping(true);
        //xl.setAxisMaximum(12.31f);
        xl.setEnabled(true);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setAxisMaximum(120f);
        leftAxis.setAxisMinimum(35f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setAxisMaximum(200f);

        barChart.getAxisLeft().setDrawGridLines(true);
        barChart.getAxisLeft().setGridColor(Color.parseColor("#D9CE3F")); //app_yellow
        barChart.getXAxis().setDrawGridLines(true);
        barChart.getXAxis().setGridColor(Color.parseColor("#D9CE3F")); //app_yellow
        barChart.setDrawBorders(false);
        barChart.animateY(2000);
    }

    private void getBPMFromFirestore() {
        userID = mAuth.getCurrentUser().getUid();
        userEmail = mAuth.getCurrentUser().getEmail();

        Log.d(TAG, "User: " + userID);
        Log.d(TAG, "Email: " + userEmail);

        dataList = new ArrayList<>();

        fireStore.collection("BPM")
                .document(userID)
                .collection(userEmail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {

                            bpmList.clear();
                            timeStampList.clear();

                            int bpmValue = 0;
                            float timeValue = 0;
                            String aux = "";

                            for (QueryDocumentSnapshot documentSnapshot : documentSnapshots) {

                                BpmData data = documentSnapshot.toObject(BpmData.class);
                                data.setDocumentID(documentSnapshot.getId());


                                bpmValue = Integer.parseInt(data.getBpm());

                                aux = data.getTime();
                                timeValue = roundFloat(aux, 2);

                                //timeValue = Float.parseFloat(data.getTime());


                                timeStampList.add(data.getTime());

                                bpmList.add(new BarEntry(timeValue, bpmValue));

                            }

                            displayBarChart(bpmList);

                            Log.d(TAG, "Time format " + timeValue);
                            Log.d(TAG, "onSuccess: " + bpmList);
                            Log.d(TAG, "onSuccess: " + timeStampList);


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private static float roundFloat(String f, int places) {

        BigDecimal bigDecimal = new BigDecimal(f);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.floatValue();
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



