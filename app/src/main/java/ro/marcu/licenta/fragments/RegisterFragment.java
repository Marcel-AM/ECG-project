package ro.marcu.licenta.fragments;

import static ro.marcu.licenta.fragments.LoginFragment.isValidEmail;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.slider.Slider;

import ro.marcu.licenta.R;

public class RegisterFragment extends Fragment {

    private final String TAG = RegisterFragment.class.getSimpleName();

    private EditText editTextName, editTextMail, editTextPasswordConfirm, editTextPassword;
    private TextView ageDisplay, validationAge;
    private Button button;
    private ImageView returnLogin;
    private RadioGroup radioGroup;
    private Slider ageSlider;

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

        editTextName = view.findViewById(R.id.input_name_register);
        editTextMail = view.findViewById(R.id.input_email_register);
        editTextPassword = view.findViewById(R.id.input_password_register);
        editTextPasswordConfirm = view.findViewById(R.id.input_reEnter_password_register);

        radioGroup = view.findViewById(R.id.radioGroup);
        ageSlider = view.findViewById(R.id.slider_age);

        ageDisplay = view.findViewById(R.id.age_display);
        validationAge = view.findViewById(R.id.title_gender);

        returnLogin = view.findViewById(R.id.register_to_lView);

        button = view.findViewById(R.id.register_btn);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            RadioButton radioButton = view.findViewById(checkedId);

            genderType = radioButton.getText().toString().trim();

        });

        ageSlider.addOnChangeListener((slider, value, fromUser) -> {
            String ageString = String.valueOf(Math.round(value));
            ageDisplay.setTextColor(getResources().getColor(R.color.app_orange));
            ageDisplay.setText(ageString);
        });

        returnLogin.setOnClickListener(v -> goToLoginFragment());

        button.setOnClickListener(v -> validationInput(editTextName, editTextMail, editTextPassword, editTextPasswordConfirm,
                genderType, ageDisplay));

        return view;

    }

    private void validationInput(EditText editTextName, EditText editTextMail, EditText editTextPassword, EditText editTextPasswordConfirm,
                                 String gender, TextView ageDisplay) {

        String name = editTextName.getText().toString();
        String mail = editTextMail.getText().toString();
        String password = editTextPassword.getText().toString();
        String passwordConfirm = editTextPasswordConfirm.getText().toString();
        String age = ageDisplay.getText().toString();

        if (name.isEmpty()) {
            editTextName.setError("Name required !");
            editTextName.requestFocus();
        } else if (mail.isEmpty() || !isValidEmail(mail)) {
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
        } else if (gender == null) {
            validationAge.setError("Choose you gender !");
            radioGroup.requestFocus();
        } else if (age.equals("years")) {
            ageDisplay.setError("Choose you age !");
            ageSlider.requestFocus();
        } else {
            callbackLoginFragment.parseRegisterData(mail, password, name, gender, age);
        }

    }

    private void goToLoginFragment() {
        if (callbackLoginFragment != null) {
            callbackLoginFragment.changeToLoginFragment();
        }
    }

}