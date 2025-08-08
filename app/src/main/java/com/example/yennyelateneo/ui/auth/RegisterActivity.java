package com.example.yennyelateneo.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.example.yennyelateneo.R;
import com.example.yennyelateneo.domain.controller.UserController;
import com.example.yennyelateneo.domain.interfaces.OnRegisterResult;


public class RegisterActivity extends AppCompatActivity {

    private EditText email, password, username;
    private Button btRegister;
    private ImageButton backRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.emailUserRegister);
        password = findViewById(R.id.passwordUserRegister);
        username = findViewById(R.id.nameUserRegister);

        backRegister = findViewById(R.id.btnBackRegister);
        btRegister = findViewById(R.id.btnRegister);

        backRegister.setOnClickListener(v ->finish());
        btRegister.setOnClickListener(v ->registerUser());
    }

    private void registerUser() {
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();
        String usernameStr = username.getText().toString().trim();

        if (!emailStr.isEmpty() && !passwordStr.isEmpty() && !usernameStr.isEmpty()) {
            UserController.register(emailStr, passwordStr, usernameStr, new OnRegisterResult() {
                @Override
                public void onSuccess(boolean registered) {
                    runOnUiThread(() -> {
                        if (registered) {
                            Toast.makeText(RegisterActivity.this, "Registrado Correctamente, Confirme el email", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this , LoginActivity.class));
                        } else {
                            Toast.makeText(RegisterActivity.this, "Ya está registrado", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(RegisterActivity.this, "Error al Registrarse", Toast.LENGTH_SHORT).show()
                    );
                }
            });
        } else {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
        }
    }
}