package ro.marcu.licenta.activities;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ro.marcu.licenta.R;
import ro.marcu.licenta.cloudData.AdviceData;
import ro.marcu.licenta.cloudData.BpmData;
import ro.marcu.licenta.cloudData.UserData;


public class BPMScreen extends AppCompatActivity {

    private static final String TAG = "BPMScreen";

    private static ArrayList<BarEntry> bpmList = new ArrayList<>();
    private static ArrayList<String> timeStampList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore fireStore;

    private String userID, userEmail, userName, userAge, userGen;
    private String poorBPM, normalBPM, excellentBPM;
    private String auxAge;


    private ImageView mainScreen, healthScreen;
    private TextView nameTxt, emailTxt, ageTxt, genderTxt;
    private TextView poorTxt, normalTxt, excellentTxt;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpmscreen);

        mAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        userID = mAuth.getCurrentUser().getUid();
        userEmail = mAuth.getCurrentUser().getEmail();

        barChart = findViewById(R.id.barchart);

        nameTxt = findViewById(R.id.user_name);
        emailTxt = findViewById(R.id.user_email);
        ageTxt = findViewById(R.id.user_age);
        genderTxt = findViewById(R.id.user_gender);

        poorTxt = findViewById(R.id.poor_bpm_data);
        normalTxt = findViewById(R.id.normal_bpm_data);
        excellentTxt = findViewById(R.id.excellent_bpm_data);

        mainScreen = findViewById(R.id.eck_chart);
        healthScreen = findViewById(R.id.health_api);

        mainScreen.setOnClickListener(view -> goToMainScreen());
        healthScreen.setOnClickListener(view -> goToHealthScreen());


        getBPMFromFirestore();

        getUserDataFromFirebase();


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

        Log.d(TAG, "User: " + userID);
        Log.d(TAG, "Email: " + userEmail);

        fireStore.collection("BPM")
                .document(userID)
                .collection(userEmail)
                .get()
                .addOnSuccessListener(documentSnapshots -> {
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
                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(),
                "Error getting data!!!", Toast.LENGTH_LONG).show());

    }

    private static float roundFloat(String f, int places) {

        BigDecimal bigDecimal = new BigDecimal(f);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.floatValue();
    }

    private void getUserDataFromFirebase() {

        CollectionReference collectionReference = fireStore.collection("Users")
                .document(userID)
                .collection(userEmail);

        collectionReference.get()
                .addOnSuccessListener(documentSnapshots -> {
                    if (documentSnapshots.isEmpty()) {
                        Log.d(TAG, "onSuccess: LIST EMPTY");
                        return;
                    } else {

                        for (QueryDocumentSnapshot documentSnapshot : documentSnapshots) {

                            UserData data = documentSnapshot.toObject(UserData.class);

                            userName = data.getName();
                            userAge = data.getAge();
                            userGen = data.getGender();


                        }

                        String[] arr = userEmail.split("@");
                        emailTxt.setText(arr[0]);

                        nameTxt.setText(userName);
                        ageTxt.setText(userAge);
                        genderTxt.setText(userGen);

                        getAdviceDataFromFirebase();

                        Log.d(TAG, "User name " + userName);
                        Log.d(TAG, "User age " + userAge);
                        Log.d(TAG, "User gender " + userGen);


                    }
                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(),
                "Error getting data!!!", Toast.LENGTH_LONG).show());

    }

    private void getAdviceDataFromFirebase() {

        ageConversion(userAge);

        DocumentReference docRef = fireStore.collection("Recommendation")
                .document(userGen)
                .collection("age")
                .document(auxAge);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot == null) {
                    Log.d(TAG, "onSuccess: EMPTY");
                    return;
                } else {

                    AdviceData adviceData = documentSnapshot.toObject(AdviceData.class);

                    poorBPM = adviceData.getPoor();
                    normalBPM = adviceData.getNormal();
                    excellentBPM = adviceData.getExcelent();

                    poorTxt.setText(poorBPM);
                    normalTxt.setText(normalBPM);
                    excellentTxt.setText(excellentBPM);

                }

                Log.d(TAG, "User poorBPM " + poorBPM);
                Log.d(TAG, "User normalBPM " + normalBPM);
                Log.d(TAG, "User excellentBPM " + excellentBPM);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void ageConversion(String age) {

        List<String> list1 = Arrays.asList("18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29");
        List<String> list2 = Arrays.asList("30", "31", "32", "33", "34", "35", "36", "37", "38", "39");
        List<String> list3 = Arrays.asList("40", "41", "42", "43", "44", "45", "46", "47", "48", "49");
        List<String> list4 = Arrays.asList("50", "51", "52", "53", "54", "55", "56", "57", "58", "59");
        List<String> list5 = Arrays.asList("60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70");

        if (list1.contains(age)) {
            auxAge = "25";
        } else if (list2.contains(age)) {
            auxAge = "35";
        } else if (list3.contains(age)) {
            auxAge = "45";
        } else if (list4.contains(age)) {
            auxAge = "55";
        } else if (list5.contains(age)) {
            auxAge = "65";
        } else {
            Log.d(TAG, "Error at conversion ");
        }


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



