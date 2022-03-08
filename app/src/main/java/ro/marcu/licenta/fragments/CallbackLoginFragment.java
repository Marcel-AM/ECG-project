package ro.marcu.licenta.fragments;

public interface CallbackLoginFragment {
    void changeFragment();
    void parseLoginData(String username, String password);
    void parseRegisterData(String username, String password);
}
