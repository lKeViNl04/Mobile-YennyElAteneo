package com.example.yennyelateneo.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yennyelateneo.BuildConfig;
import com.example.yennyelateneo.ui.MainActivity;
import com.example.yennyelateneo.R;
import com.example.yennyelateneo.domain.controller.UserController;
import com.example.yennyelateneo.domain.interfaces.OnLoginResult;
import com.example.yennyelateneo.data.supabase.SupabaseManager;


public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btnLogin, btnRegisterView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        SupabaseManager.initialize(BuildConfig.SUPABASE_URL,BuildConfig.SUPABASE_KEY);

        email = findViewById(R.id.emailUserLogin);
        password = findViewById(R.id.passwordUserLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegisterView = findViewById(R.id.btnRegisterViewLogin);

        btnLogin.setOnClickListener(v -> tryLogin());
        btnRegisterView.setOnClickListener(v -> goRegister());
    }

    private void tryLogin() {
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        if (!emailStr.isEmpty() && !passwordStr.isEmpty()) {
            UserController.login(emailStr, passwordStr, new OnLoginResult() {
                @Override
                public void onSuccess(boolean result) {
                    runOnUiThread(() -> {
                        if (result) {
                            Toast.makeText(LoginActivity.this, "Iniciando session", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this , MainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Login bloqueado: email no confirmado.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "Error al Iniciar session", Toast.LENGTH_SHORT).show()
                    );
                }
            });
        } else {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void goRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}