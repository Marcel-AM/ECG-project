package ro.marcu.licenta.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

import ro.marcu.licenta.R;
import ro.marcu.licenta.fragments.CallbackLoginFragment;
import ro.marcu.licenta.fragments.LoginFragment;
import ro.marcu.licenta.fragments.NetworkFragment;
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

        checkInternetConnection();
        goToMainScreenExtra();

        mAuth = FirebaseAuth.getInstance();


        editTextMail = findViewById(R.id.input_email);
        editTextPassword = findViewById(R.id.input_password);

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


    @Override
    public void changeToRegisterFragment() {
        registerFragment();
    }

    @Override
    public void changeToLoginFragment() {
        loginFragment();
    }

    @Override
    public void changeToNetworkFragment() {
        networkFragment();
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
                            loginFragment();

                        } else {

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(FirstScreen.this, "User with this email already exist.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(FirstScreen.this, "Register failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
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
