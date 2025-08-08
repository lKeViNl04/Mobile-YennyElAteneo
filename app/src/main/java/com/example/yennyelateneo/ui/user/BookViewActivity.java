package com.example.yennyelateneo.ui.user;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yennyelateneo.R;
import com.example.yennyelateneo.domain.controller.BookController;
import com.example.yennyelateneo.domain.interfaces.OnBooksGetResult;
import com.example.yennyelateneo.data.model.Book;

import java.util.ArrayList;
import java.util.List;


public class BookViewActivity extends AppCompatActivity {

    private ImageButton backViewBook;
    private ProgressBar progressBar;
    private ListView containerBooksView;

    private final List<Book> localBookList = new ArrayList<>();
    private BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_view);

        backViewBook = findViewById(R.id.btnBackViewBook);
        progressBar = findViewById(R.id.progressBarViewBook);
        containerBooksView = findViewById(R.id.containerBooksView);

        backViewBook.setOnClickListener(v -> finish());

        adapter = new BookAdapter(this, localBookList);
        containerBooksView.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);
        containerBooksView.setVisibility(View.GONE);

        loadBooks();

    }

    private void loadBooks() {
        BookController.getBooksAsync(new OnBooksGetResult() {
            @Override
            public void onSuccess(List<Book> books) {
                runOnUiThread(() -> {
                    localBookList.clear();
                    localBookList.addAll(books);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    containerBooksView.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(BookViewActivity.this, "Error al cargar libros", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }
}