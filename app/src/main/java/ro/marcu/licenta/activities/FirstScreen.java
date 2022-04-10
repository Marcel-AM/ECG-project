package ro.marcu.licenta.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.Map;

import ro.marcu.licenta.R;
import ro.marcu.licenta.cloudData.UserData;
import ro.marcu.licenta.fragments.CallbackLoginFragment;
import ro.marcu.licenta.fragments.ForgottenFragment;
import ro.marcu.licenta.fragments.LoginFragment;
import ro.marcu.licenta.fragments.NetworkFragment;
import ro.marcu.licenta.fragments.RegisterFragment;
import ro.marcu.licenta.languages.AppCompat;

public class FirstScreen extends AppCompat implements CallbackLoginFragment {

    public static final String SHARED_PREF_EMAIL = "email_shared_pref";
    public static final String SHARED_PREF_PASSWORD = "password_shared_pref";

    private FirebaseAuth mAuth;
    private FirebaseFirestore fireStore;

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private String userID, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        checkInternetConnection();
        //goToMainScreenExtra();

        mAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();


    }

    public void loginFragment() {
        LoginFragment fragment = new LoginFragment();
        fragment.setCallbackFragment(this);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_register_to_login, R.anim.exit_register_to_login,
                R.anim.enter_login_to_register, R.anim.exit_login_to_register);
        fragmentTransaction.add(R.id.thePlaceholder, fragment);
        fragmentTransaction.commit();
    }

    public void networkFragment() {
        NetworkFragment fragment = new NetworkFragment();
        fragment.setCallbackFragment(this);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_register_to_login, R.anim.exit_register_to_login,
                R.anim.enter_login_to_register, R.anim.exit_login_to_register);
        fragmentTransaction.add(R.id.thePlaceholder, fragment);
        fragmentTransaction.commit();
    }


    public void registerFragment() {
        fragment = new RegisterFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_login_to_register, R.anim.exit_login_to_register,
                R.anim.enter_register_to_login, R.anim.exit_register_to_login);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.thePlaceholder, fragment);
        fragmentTransaction.commit();
    }

    public void resetFragment() {
        fragment = new ForgottenFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right,
                R.anim.slide_in_left, R.anim.slide_out_left);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.thePlaceholder, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void changeToRegisterFragment() {
        registerFragment();
    }

    @Override
    public void changeToLoginFragment() {
        loginFragment();
    }

    @Override
    public void changeToForgottenFragment() {
        resetFragment();
    }

    @Override
    public void changeToNetworkFragment() {
        networkFragment();
    }

    @Override
    public void parseLoginData(String email, String password) {
        loginUser(email, password);
    }

    @Override
    public void parseResetData(String email) {
        resetPassword(email);
    }

    @Override
    public void parseRegisterData(String email, String password, String name, String gender, String age) {
        createUser(email, password, name, gender, age);
    }


    private void saveUser(String email, String password) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SHARED_PREF_EMAIL, email);
        editor.putString(SHARED_PREF_PASSWORD, password);
        editor.apply();
    }

    private void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(FirstScreen.this, R.string.success_reset_mail_sent,
                            Toast.LENGTH_SHORT).show();
                    loginFragment();
                } else {
                    Toast.makeText(FirstScreen.this, R.string.failure_reset_mail_sent,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveUser(email, password);
                            goToMainScreen(email);
                        } else {
                            Toast.makeText(FirstScreen.this, R.string.login_failure,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void createUser(String email, String password, String name, String gender, String age) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(FirstScreen.this, R.string.register_success,
                                    Toast.LENGTH_SHORT).show();

                            userID = mAuth.getCurrentUser().getUid();
                            userEmail = mAuth.getCurrentUser().getEmail();

                            insertUserInDatabase(name, email, gender, age);
                            loginFragment();

                        } else {

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(FirstScreen.this, R.string.register_same_user,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(FirstScreen.this, R.string.register_failed,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void insertUserInDatabase(String name, String email, String gender, String age) {
        UserData data = new UserData(name, email, gender, age);

        Map<String, Object> dataToInsert = new HashMap<>();
        dataToInsert.put("name", data.getName());
        dataToInsert.put("email", data.getEmail());
        dataToInsert.put("gender", data.getGender());
        dataToInsert.put("age", data.getAge());

        fireStore.collection("Users")
                .document(userID)
                .collection(userEmail)
                .add(dataToInsert)
                .addOnSuccessListener(documentReference -> {
                    Log.d("destinationInserted", "success");
                    //Toast.makeText(this, "Your User data was sent with success !", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, R.string.inserted_failed, Toast.LENGTH_SHORT).show();
                });

    }

    private void goToMainScreenExtra() {
        Intent intentGoToDashboard = new Intent(this, MainScreen.class);
        startActivity(intentGoToDashboard);
    }

    private void goToMainScreen(String mailExtra) {
        Intent intentGoToDashboard = new Intent(this, MainScreen.class);
        startActivity(intentGoToDashboard);
    }

    private void checkInternetConnection() {
        if (!isConnected(FirstScreen.this)) {
            networkFragment();
        } else {
            loginFragment();
        }
    }

    private boolean isConnected(FirstScreen firstScreen) {

        ConnectivityManager connectivityManager = (ConnectivityManager) firstScreen.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }


    @Override
    public void onBackPressed() {
    }
}
