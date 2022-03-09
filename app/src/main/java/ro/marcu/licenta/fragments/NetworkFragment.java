package ro.marcu.licenta.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import ro.marcu.licenta.R;
import ro.marcu.licenta.activities.FirstScreen;

public class NetworkFragment extends Fragment {

    private Button tryAgain;
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
        View view = inflater.inflate(R.layout.fragment_network, container, false);

        tryAgain = view.findViewById(R.id.network_btn);

        tryAgain.setOnClickListener(view1 -> checkConnection());

        return view;
    }

    private void goToRegisterFragment() {
        if (callbackLoginFragment != null) {
            callbackLoginFragment.changeToNetworkFragment();
        }
    }

    public void setCallbackFragment(CallbackLoginFragment callbackLoginFragment) {
        this.callbackLoginFragment = callbackLoginFragment;

    }

    private void checkConnection(){
        startActivity(new Intent(getContext(), FirstScreen.class));
    }


}