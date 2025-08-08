package com.example.yennyelateneo.ui.user;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yennyelateneo.R;
import com.example.yennyelateneo.domain.controller.FavoriteController;
import com.example.yennyelateneo.domain.interfaces.OnBooksGetResult;
import com.example.yennyelateneo.data.model.Book;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private TextView NoFavorites;
    private ListView containerBooksFav;
    private BookAdapter adapter;
    private ImageButton backFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite);

        NoFavorites = findViewById(R.id.textIconNoFav);
        backFav = findViewById(R.id.btnBackFav);
        containerBooksFav = findViewById(R.id.containerBooksFav);

        backFav.setOnClickListener(v -> finish());

        loadFavorites();
    }

    private void loadFavorites() {
        FavoriteController.getFavoriteBooksByUserAsync(new OnBooksGetResult() {
            @Override
            public void onSuccess(List<Book> books) {
                runOnUiThread(() -> {
                    if (books != null && !books.isEmpty()) {
                        NoFavorites.setVisibility(View.GONE);
                        adapter = new BookAdapter(FavoriteActivity.this, books);
                        containerBooksFav.setAdapter(adapter);
                    } else {
                        NoFavorites.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    NoFavorites.setVisibility(View.VISIBLE);
                    Toast.makeText(FavoriteActivity.this, "Error al cargar favoritos", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}