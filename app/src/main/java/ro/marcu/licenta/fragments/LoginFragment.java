package ro.marcu.licenta.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import ro.marcu.licenta.R;

public class LoginFragment extends Fragment {

    private EditText editTextMail, editTextPassword;
    private TextView register, clean;
    private Button buttonLogin;
    private CallbackLoginFragment callbackLoginFragment;

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

        buttonLogin = view.findViewById(R.id.splash_screen_btn);

        register = view.findViewById(R.id.sign_up_tView);
        clean = view.findViewById(R.id.clean_tView);

        register.setOnClickListener(v -> goToRegisterFragment());
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

        TextView mailWarning = getView().findViewById(R.id.mail_warning);
        TextView missingInput = getView().findViewById(R.id.password_warning);

        String mail = editTextMail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (isValidEmail(mail) && !password.isEmpty()) {
            exceptionInvisible(mailWarning, missingInput);
            callbackLoginFragment.parseLoginData(mail, password);
        } else {
            exceptionInvisible(mailWarning, missingInput);

            if (password.isEmpty()) {
                missingInput.setVisibility(View.VISIBLE);
            }
            if (!isValidEmail(mail)) {
                mailWarning.setVisibility(View.VISIBLE);
            }
        }

    }

    private void exceptionInvisible(TextView mailWarning, TextView missingInput) {

        mailWarning.setVisibility(View.INVISIBLE);
        missingInput.setVisibility(View.INVISIBLE);

    }

    private void cleanAllInput() {

        editTextMail = getView().findViewById(R.id.input_email);
        editTextPassword = getView().findViewById(R.id.input_password);

        editTextMail.setText(null);
        editTextPassword.setText(null);
    }

    private void goToRegisterFragment() {
        if (callbackLoginFragment != null) {
            callbackLoginFragment.changeFragment();
        }
    }

    public void setCallbackFragment(CallbackLoginFragment callbackLoginFragment) {
        this.callbackLoginFragment = callbackLoginFragment;

    }


}