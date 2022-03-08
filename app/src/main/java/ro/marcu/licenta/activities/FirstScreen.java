package ro.marcu.licenta.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ro.marcu.licenta.R;
import ro.marcu.licenta.fragments.CallbackLoginFragment;
import ro.marcu.licenta.fragments.LoginFragment;
import ro.marcu.licenta.fragments.RegisterFragment;

public class FirstScreen extends AppCompatActivity implements CallbackLoginFragment {

    public static final String INTENT_KEY_MAIL = "intent_key_mail";
    public static final String SHARED_PREF_USERNAME = "username_shared_pref";
    public static final String SHARED_PREF_PASSWORD = "password_shared_pref";

    private FirebaseAuth mAuth;

    private EditText editTextMail, editTextPassword;

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);


        //goToMainScreenExtra();

        mAuth = FirebaseAuth.getInstance();

        addFragment();

        editTextMail = findViewById(R.id.input_email);
        editTextPassword = findViewById(R.id.input_password);

    }

    public void addFragment() {
        LoginFragment fragment = new LoginFragment();
        fragment.setCallbackFragment(this);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.thePlaceholder, fragment);
        fragmentTransaction.commit();
    }

    public void replaceFragment() {
        fragment = new RegisterFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.thePlaceholder, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void changeFragment() {
        replaceFragment();
    }

    @Override
    public void parseLoginData(String username, String password) {
        loginUser(username, password);
    }

    @Override
    public void parseRegisterData(String username, String password) {
        createUser(username, password);
    }


    private void saveUser(String username, String password) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SHARED_PREF_USERNAME, username);
        editor.putString(SHARED_PREF_PASSWORD, password);
        editor.apply();
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
                            Toast.makeText(FirstScreen.this, "Wrong Credential OR Bad ethernet connection",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(FirstScreen.this, "User Created.",
                                    Toast.LENGTH_SHORT).show();
                            addFragment();

                        } else {
                            Toast.makeText(FirstScreen.this, "Register failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void goToMainScreenExtra() {
        Intent intentGoToDashboard = new Intent(this, MainScreen.class);
        startActivity(intentGoToDashboard);
    }

    private void goToMainScreen(String mailExtra) {
        Intent intentGoToDashboard = new Intent(this, MainScreen.class);
        intentGoToDashboard.putExtra(INTENT_KEY_MAIL, mailExtra);

        startActivity(intentGoToDashboard);
    }
    @Override
    public void onBackPressed() {
        Intent intentRefresh = new Intent(this, FirstScreen.class);
        startActivity(intentRefresh);
    }
}
