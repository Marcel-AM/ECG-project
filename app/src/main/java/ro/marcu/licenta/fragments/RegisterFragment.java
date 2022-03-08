package ro.marcu.licenta.fragments;

import static ro.marcu.licenta.fragments.LoginFragment.isValidEmail;

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

public class RegisterFragment extends Fragment {

    private EditText editTextMail, editTextPasswordConfirm, editTextPassword;
    private Button button;

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

        button = view.findViewById(R.id.register_btn);

        button.setOnClickListener(v -> validationInput(editTextMail, editTextPassword, editTextPasswordConfirm));

        return view;

    }

    private void validationInput(EditText editTextMail, EditText editTextPassword, EditText editTextPasswordConfirm) {

        TextView mailWarning = getView().findViewById(R.id.input_email_warning);
        TextView missingInput = getView().findViewById(R.id.password_register_warning);
        TextView differentPasswords = getView().findViewById(R.id.password_confirm_register_warning);

        String mail = editTextMail.getText().toString();
        String password = editTextPassword.getText().toString();
        String passwordConfirm = editTextPasswordConfirm.getText().toString();

        if (isValidEmail(mail) && !password.isEmpty() && !passwordConfirm.isEmpty()) {
            exceptionInvisible(mailWarning, missingInput);

            if (password.equals(passwordConfirm)) {
                differentPasswords.setVisibility(View.INVISIBLE);
                callbackLoginFragment.parseRegisterData(mail, password);
            } else {
                differentPasswords.setVisibility(View.VISIBLE);
            }
        } else {
            exceptionInvisible(mailWarning, missingInput);

            if (password.isEmpty() || passwordConfirm.isEmpty()) {
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

}