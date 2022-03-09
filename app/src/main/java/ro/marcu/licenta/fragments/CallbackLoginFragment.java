package ro.marcu.licenta.fragments;

public interface CallbackLoginFragment {
    void changeToRegisterFragment();
    void changeToLoginFragment();
    void changeToNetworkFragment();
    void parseLoginData(String username, String password);
    void parseRegisterData(String username, String password);
}
