package com.example.yennyelateneo.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yennyelateneo.R;
import com.example.yennyelateneo.domain.controller.BookController;
import com.example.yennyelateneo.domain.interfaces.OnBooksGetResult;
import com.example.yennyelateneo.data.model.Book;

import java.util.ArrayList;
import java.util.List;

public class AdminBookViewActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> addBookLauncher;
    private ActivityResultLauncher<Intent> detailBookLauncher;


    private ImageButton backViewBookAdmin;
    private ProgressBar progressBarAdmin;
    private ListView containerBooksViewAdmin;

    private final List<Book> localBookListAdmin = new ArrayList<>();
    private AdminBookAdapter adapterAdmin;
    private Button btnAddBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_book_view);

        backViewBookAdmin = findViewById(R.id.btnBackViewBookAdmin);
        progressBarAdmin = findViewById(R.id.progressBarViewBookAdmin);
        containerBooksViewAdmin = findViewById(R.id.containerBooksViewAdmin);
        btnAddBook = findViewById(R.id.btnAddBook);

        btnAddBook.setOnClickListener(v -> { goToActivity(AdminBookAddActivity.class);});
        backViewBookAdmin.setOnClickListener(v -> finish());

        adapterAdmin = new AdminBookAdapter(this, localBookListAdmin);
        containerBooksViewAdmin.setAdapter(adapterAdmin);

        addBookLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadBooks();
                    }
                }
        );

        detailBookLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean updated = result.getData().getBooleanExtra("updated", false);
                        if (updated) {
                            loadBooks();
                        }
                    }
                }
        );


        adapterAdmin.setOnBookClickListener(book -> {
            Bundle bundle = new Bundle();
            bundle.putLong("id", book.getId());
            bundle.putString("title", book.getTitle());
            bundle.putString("author", book.getAuthor());
            bundle.putString("description", book.getDescription());
            bundle.putString("image", book.getImage());
            bundle.putDouble("price", book.getPrice());

            Intent intent = new Intent(this, AdminBookDetailActivity.class);
            intent.putExtras(bundle);
            detailBookLauncher.launch(intent);
        });

        progressBarAdmin.setVisibility(View.VISIBLE);
        containerBooksViewAdmin.setVisibility(View.GONE);

        loadBooks();
    }

    private void goToActivity(Class<?> actividad) {
        if (actividad == AdminBookAddActivity.class) {
            addBookLauncher.launch(new Intent(this, actividad)); // ✅ Importante
        } else {
            detailBookLauncher.launch(new Intent(this, actividad));
        }
    }


    private void loadBooks() {
        BookController.getBooksAsync(new OnBooksGetResult() {
            @Override
            public void onSuccess(List<Book> books) {
                runOnUiThread(() -> {
                    localBookListAdmin.clear();
                    localBookListAdmin.addAll(books);
                    adapterAdmin.notifyDataSetChanged();
                    progressBarAdmin.setVisibility(View.GONE);
                    containerBooksViewAdmin.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(AdminBookViewActivity.this, "Error al cargar libros", Toast.LENGTH_SHORT).show();
                    progressBarAdmin.setVisibility(View.GONE);
                });
            }
        });
    }

}