package com.example.myapplication.ui.login;

public interface LoginView {
    void showEmailError(boolean show);
    void showPasswordError(boolean show);
    void showCredentialsError(boolean show);
    void setLoginButtonEnabled(boolean enabled);
    void showProgress(boolean show);
    void navigateToTodoList(boolean useRemote);
    void showMessage(String message);
}
