package ro.marcu.licenta.fragments;

import static ro.marcu.licenta.fragments.LoginFragment.isValidEmail;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import ro.marcu.licenta.R;

public class ForgottenFragment extends Fragment {

    private final String TAG = ForgottenFragment.class.getSimpleName();

    private EditText editTextMail;
    private ImageView returnLogin;
    private Button buttonReset;

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
        View view = inflater.inflate(R.layout.fragment_forgotten, container, false);

        editTextMail = view.findViewById(R.id.input_email);
        returnLogin = view.findViewById(R.id.reset_to_lView);

        buttonReset = view.findViewById(R.id.reset_btn);

        buttonReset.setOnClickListener(view1 -> validationInput(editTextMail));
        returnLogin.setOnClickListener(v -> goToLoginFragment());


        return view;

    }

    private void validationInput(EditText editTextMail) {
        String mail = editTextMail.getText().toString().trim();

        if (mail.isEmpty() || !isValidEmail(mail)) {
            editTextMail.setError("Email required !");
            editTextMail.requestFocus();
        } else {
            callbackLoginFragment.parseResetData(mail);
        }

    }


    private void goToLoginFragment() {
        if (callbackLoginFragment != null) {
            callbackLoginFragment.changeToLoginFragment();
        }
    }

}