package com.example.myapplication.controller;

import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import com.example.myapplication.db.LoginImpl;
import com.example.myapplication.model.Users;
import com.example.myapplication.ui.login.LoginView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginController {

    private LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
    }

    public void onInputChanged(String email, String password) {
        view.showCredentialsError(false);
        view.showEmailError(false);
        view.showPasswordError(false);
        view.setLoginButtonEnabled(!email.isEmpty() && !password.isEmpty());
    }

    public void attemptLogin(String email, String password) {
        String cleanEmail = email.trim();
        String cleanPassword = password.trim();

        boolean isEmailValid = isEmailValid(cleanEmail);
        boolean isPasswordValid = isPasswordValid(cleanPassword);

        if (!isEmailValid) {
            view.showEmailError(true);
        }
        if (!isPasswordValid) {
            view.showPasswordError(true);
        }

        if (!isEmailValid || !isPasswordValid) {
            return;
        }

        view.showProgress(true);
        view.setLoginButtonEnabled(false);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        Users userCredentials = new Users();
        userCredentials.setEmail(cleanEmail);
        userCredentials.setPassword(cleanPassword);

        String baseUrl = "http://10.0.2.2:8080/backend-1.0-SNAPSHOT/rest";
        LoginImpl loginImpl = new LoginImpl(baseUrl);

        executorService.submit(() -> {
            try {
                javax.ws.rs.core.Response response = loginImpl.checkUser(userCredentials);
                int status = response.getStatus();
                mainHandler.post(() -> {
                    view.showProgress(false);
                    if (status == javax.ws.rs.core.Response.Status.OK.getStatusCode()) {
                        view.navigateToTodoList(true);
                    } else {
                        view.showCredentialsError(true);
                        view.setLoginButtonEnabled(true);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    view.showProgress(false);
                    view.showMessage("Connection error. Using offline mode.");
                    view.navigateToTodoList(false);
                });
            }
            return null;
        });
        executorService.shutdown();
    }

    private boolean isEmailValid(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.matches("\\d{6}");
    }
}