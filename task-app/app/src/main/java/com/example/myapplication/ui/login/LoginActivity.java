package com.example.myapplication.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.controller.LoginController;
import com.example.myapplication.db.SyncEngine;
import com.example.myapplication.ui.todolist.TodoListActivity;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView emailErrorText, passwordErrorText, pairErrorText;
    private Button loginButton;
    private ProgressBar progressBar;

    private LoginController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        findViews();
        setupInputs();
        sync();
    }

    private void findViews() {
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        emailErrorText = findViewById(R.id.email_error);
        passwordErrorText = findViewById(R.id.password_error);
        pairErrorText = findViewById(R.id.pair_error);
        loginButton = findViewById(R.id.login_id);
        progressBar = findViewById(R.id.login_progress);
    }

    private void setupInputs() {
        emailEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        passwordEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        loginButton.setEnabled(false);
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            controller.attemptLogin(email, password);
        });

        TextWatcher inputWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                controller.onInputChanged(
                        emailEditText.getText().toString(),
                        passwordEditText.getText().toString()
                );
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        emailEditText.addTextChangedListener(inputWatcher);
        passwordEditText.addTextChangedListener(inputWatcher);
    }

    private void sync() {
        controller = new LoginController(this);
        showProgress(true);
        SyncEngine syncEngine = new SyncEngine(this);
        syncEngine.synchronize(new SyncEngine.SyncCallback() {
            @Override
            public void onSyncComplete() {
                showProgress(false);
            }

            @Override
            public void onOfflineMode() {
                showMessage("Server unreachable. Offline mode.");
                navigateToTodoList(false);
            }
        });
    }

    @Override
    public void showEmailError(boolean show) {
        emailErrorText.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showPasswordError(boolean show) {
        passwordErrorText.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showCredentialsError(boolean show) {
        pairErrorText.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setLoginButtonEnabled(boolean enabled) {
        loginButton.setEnabled(enabled);
    }

    @Override
    public void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void navigateToTodoList(boolean useRemote) {
        Intent intent = new Intent(LoginActivity.this, TodoListActivity.class);
        intent.putExtra("USE_REMOTE", useRemote);
        startActivity(intent);
        finish();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}