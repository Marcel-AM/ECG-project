package ro.marcu.licenta.fragments;

public interface CallbackLoginFragment {
    void changeToRegisterFragment();
    void changeToLoginFragment();
    void changeToForgottenFragment();
    void changeToNetworkFragment();
    void parseLoginData(String email, String password);
    void parseResetData(String email);
    void parseRegisterData(String email, String password, String name, String gender, String age);
}
