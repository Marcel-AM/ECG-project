package ro.marcu.licenta.activities;

import static ro.marcu.licenta.activities.MainScreen.INTENT_KEY_MAIL_MAIN;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import ro.marcu.licenta.R;
import ro.marcu.licenta.cloudData.BpmData;

public class BPMScreen extends AppCompatActivity {

    private static final String TAG = "BPMScreen";

    private static final String KEY_BPM = "bpm";
    private static final String KEY_TIME_STAMP = "time";

    private static ArrayList<BpmData> mArrayList = new ArrayList<>();
    private static List<BpmData> dataList;


    private FirebaseAuth mAuth;
    private FirebaseFirestore fireStore;

    private String userID;

    private TextView mainEmail, testText;
    private ImageView mainScreen, healthScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpmscreen);

        mainEmail = findViewById(R.id.main_email);
        testText = findViewById(R.id.test_text);

        if (getIntent() != null) {
            String receiveMail = getIntent().getStringExtra(INTENT_KEY_MAIL_MAIN);
            mainEmail.setText(receiveMail);
        }

        mAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        mainScreen = findViewById(R.id.eck_chart);
        healthScreen = findViewById(R.id.health_api);

        mainScreen.setOnClickListener(view -> goToMainScreen());
        healthScreen.setOnClickListener(view -> goToHealthScreen());

        getBPMFromFirestore();
    }

    private void getBPMFromFirestore() {
        userID = mAuth.getCurrentUser().getUid();
        String contactEmail = mainEmail.getText().toString().trim();

        Log.d(TAG, "User: " + userID);
        Log.d(TAG, "Email: " + contactEmail);

        dataList = new ArrayList<>();

        fireStore.collection("BPM")
                .document(userID)
                .collection(contactEmail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {

                            String aux = "";
                            String bpm = "";
                            String timeStamp = "";

                            for (QueryDocumentSnapshot documentSnapshot : documentSnapshots) {

                                BpmData data = documentSnapshot.toObject(BpmData.class);
                                data.setDocumentID(documentSnapshot.getId());

                                String documentID = data.getDocumentID();
                                aux += " ID " + documentID;

                                bpm += " bpm " + data.getBpm();
                                timeStamp += " timeStamp " + data.getTime();
                            }

                            //testText.setText(aux);
                            Log.d(TAG, "onSuccess: " + aux);
                            Log.d(TAG, "onSuccess: " + bpm);
                            Log.d(TAG, "onSuccess: " + timeStamp);

                            /*
                            List<DocumentSnapshot> list = documentSnapshots.getDocuments();

                            for(DocumentSnapshot d : list){
                                BpmData data = d.toObject(BpmData.class);
                                dataList.add(data);
                            }

                            Log.d(TAG, "onSuccess: " + dataList);
                            /*

                            List<BpmData> types = documentSnapshots.toObjects(BpmData.class);

                            // Add all to your list
                            mArrayList.addAll(types);
                            Log.d(TAG, "onSuccess: " + mArrayList);

                        */
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
            }
        });
/*
        CollectionReference coffeeRef = fireStore.collection("BPM");
        coffeeRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        String coffeeName = document.getString("coffeeName");
                        Log.d(TAG,"Test" + coffeeName);
                    }
                }
            }
        });
*/

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