package com.example.yennyelateneo.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yennyelateneo.ui.user.FavoriteActivity;
import com.example.yennyelateneo.R;

public class AdminMainActivity extends AppCompatActivity {

    private Button btnSeeBookAdmin, btnSeeUserAdmin;
    private ImageButton backMainAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);

        btnSeeBookAdmin = findViewById(R.id.btnSeeBookAdmin);
        btnSeeUserAdmin = findViewById(R.id.btnSeeUserAdmin);
        backMainAdmin = findViewById(R.id.btnBackAdmin);

        backMainAdmin.setOnClickListener(v -> finish());
        btnSeeBookAdmin.setOnClickListener(view -> goToActivity(AdminBookViewActivity.class));
        //btnSeeUserAdmin.setOnClickListener(view -> goToActivity(AdminUserActivity.class));

    }

    private void goToActivity(Class<?> actividad) {
        Intent intent = new Intent(this, actividad);
        startActivity(intent);
    }
}