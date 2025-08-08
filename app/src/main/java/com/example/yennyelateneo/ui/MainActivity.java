package com.example.yennyelateneo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yennyelateneo.R;
import com.example.yennyelateneo.ui.admin.AdminMainActivity;
import com.example.yennyelateneo.data.model.SessionManager;
import com.example.yennyelateneo.ui.user.BookViewActivity;
import com.example.yennyelateneo.ui.user.FavoriteActivity;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager = SessionManager.getInstance();
    private TextView welcome;
    private Button btnSeeBook, btnSeeFavorite , btnAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnSeeBook = findViewById(R.id.btnSeeBook);
        btnSeeFavorite = findViewById(R.id.btnSeeFavorite);
        btnAdmin =findViewById(R.id.btnAdmin);
        welcome = findViewById(R.id.welcome);

        String name = sessionManager.getUsername();

        if(!name.trim().isEmpty()){
            welcome.setText("Hola, "+name+" Mucho Gusto");
        }else{
            welcome.setText("Hola, Desconocido Mucho Gusto");
        }

        btnSeeBook.setOnClickListener(view -> goToActivity(BookViewActivity.class));
        btnSeeFavorite.setOnClickListener(view -> goToActivity(FavoriteActivity.class));

        if ("admin".equalsIgnoreCase(sessionManager.getRole())) {
            btnAdmin.setVisibility(View.VISIBLE);
            btnAdmin.setOnClickListener(view -> goToActivity(AdminMainActivity.class));
        }

    }

    private void goToActivity(Class<?> actividad) {
        Intent intent = new Intent(this, actividad);
        startActivity(intent);
    }

}