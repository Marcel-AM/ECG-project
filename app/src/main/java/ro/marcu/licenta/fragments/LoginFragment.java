package ro.marcu.licenta.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.wang.avi.AVLoadingIndicatorView;

import ro.marcu.licenta.R;

public class LoginFragment extends Fragment {

    private EditText editTextMail, editTextPassword;
    private TextView clean, forgotten;
    private Button buttonLogin;
    private ImageView registerView;
    private CallbackLoginFragment callbackLoginFragment;
    private AVLoadingIndicatorView progressBar;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callbackLoginFragment = (CallbackLoginFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "Most implement");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        editTextMail = view.findViewById(R.id.input_email);
        editTextPassword = view.findViewById(R.id.input_password);

        buttonLogin = view.findViewById(R.id.login_btn);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        registerView = view.findViewById(R.id.login_to_rView);

        clean = view.findViewById(R.id.clean_tView);
        forgotten = view.findViewById(R.id.forgotten_tView);

        registerView.setOnClickListener(v -> goToRegisterFragment());
        forgotten.setOnClickListener(view1 -> goToForgottenFragment());

        clean.setOnClickListener(v -> cleanAllInput());

        buttonLogin.setOnClickListener(v -> {
            validationInput(editTextMail, editTextPassword);
        });

        return view;
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void validationInput(EditText editTextMail, EditText editTextPassword) {
        String mail = editTextMail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (mail.isEmpty() || !isValidEmail(mail)) {
            editTextMail.setError("Email required !");
            editTextMail.requestFocus();
        } else if (password.isEmpty()) {
            editTextPassword.setError("Password required !");
            editTextPassword.requestFocus();
        } else if (password.length() <= 5) {
            editTextPassword.setError("Min 6 characters !");
            editTextPassword.requestFocus();
        } else {
            callbackLoginFragment.parseLoginData(mail, password);
            progressBar.setVisibility(View.VISIBLE);
        }
    }


    private void cleanAllInput() {

        editTextMail = getView().findViewById(R.id.input_email);
        editTextPassword = getView().findViewById(R.id.input_password);

        editTextMail.setText(null);
        editTextPassword.setText(null);
    }

    private void goToRegisterFragment() {
        if (callbackLoginFragment != null) {
            callbackLoginFragment.changeToRegisterFragment();
        }
    }

    private void goToForgottenFragment() {
        if (callbackLoginFragment != null) {
            callbackLoginFragment.changeToForgottenFragment();
        }
    }

    public void setCallbackFragment(CallbackLoginFragment callbackLoginFragment) {
        this.callbackLoginFragment = callbackLoginFragment;

    }


}