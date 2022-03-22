package ro.marcu.licenta.fragments;

import static ro.marcu.licenta.fragments.LoginFragment.isValidEmail;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.Slider;

import ro.marcu.licenta.R;

public class RegisterFragment extends Fragment {

    private final String TAG = RegisterFragment.class.getSimpleName();

    private EditText editTextMail, editTextPasswordConfirm, editTextPassword;
    private TextView ageDisplay;
    private Button button;
    private ImageView returnLogin;

    private String genderType = null;

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
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        editTextMail = view.findViewById(R.id.input_email_register);
        editTextPassword = view.findViewById(R.id.input_password_register);
        editTextPasswordConfirm = view.findViewById(R.id.input_reEnter_password_register);

        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        Slider priceSlider = view.findViewById(R.id.slider_age);

        ageDisplay = view.findViewById(R.id.age_display);

        returnLogin = view.findViewById(R.id.register_to_lView);

        button = view.findViewById(R.id.register_btn);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            RadioButton radioButton = view.findViewById(checkedId);

            genderType = radioButton.getText().toString().trim();

        });

        priceSlider.addOnChangeListener((slider, value, fromUser) -> {
            String ageString = String.valueOf(Math.round(value));
            ageDisplay.setText(ageString);
        });

        returnLogin.setOnClickListener(v -> goToLoginFragment());

        button.setOnClickListener(v -> validationInput(editTextMail, editTextPassword, editTextPasswordConfirm));

        return view;

    }

    private void validationInput(EditText editTextMail, EditText editTextPassword, EditText editTextPasswordConfirm) {

        String mail = editTextMail.getText().toString();
        String password = editTextPassword.getText().toString();
        String passwordConfirm = editTextPasswordConfirm.getText().toString();

        if (mail.isEmpty() || !isValidEmail(mail)) {
            editTextMail.setError("Email required !");
            editTextMail.requestFocus();
        } else if (password.isEmpty()) {
            editTextPassword.setError("Password required !");
            editTextPassword.requestFocus();
        } else if (password.length() <= 5) {
            editTextPassword.setError("Min 6 characters !");
            editTextPassword.requestFocus();
        } else if (passwordConfirm.isEmpty()) {
            editTextPasswordConfirm.setError("Password required !");
            editTextPasswordConfirm.requestFocus();
        } else if (passwordConfirm.length() <= 5) {
            editTextPasswordConfirm.setError("Min 6 characters !");
            editTextPasswordConfirm.requestFocus();
        } else if (!password.equals(passwordConfirm)) {
            editTextPasswordConfirm.setError("Passwords are not equal !");
            editTextPasswordConfirm.requestFocus();
        } else {
            callbackLoginFragment.parseRegisterData(mail, password);
        }

    }

    private void goToLoginFragment() {
        if (callbackLoginFragment != null) {
            callbackLoginFragment.changeToLoginFragment();
        }
    }

}