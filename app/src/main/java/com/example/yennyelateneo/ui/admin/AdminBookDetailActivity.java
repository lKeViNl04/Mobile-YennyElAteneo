package com.example.yennyelateneo.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.yennyelateneo.data.model.Book;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yennyelateneo.R;
import com.example.yennyelateneo.domain.controller.BookController;
import com.example.yennyelateneo.domain.interfaces.OnBookDeleteResult;
import com.example.yennyelateneo.domain.interfaces.OnBookGetResult;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class AdminBookDetailActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> editBookLauncher;

    private TextView txtTitleAdmin, txtAuthorAdmin, txtDescriptionAdmin, txtPriceAdmin;
    private ImageButton backDetailAdmin, btnEditAdmin, btnDeleteAdmin;
    private ImageView imgBookAdmin;
    private NumberFormat format;
    private Bundle bookBundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_book_detail);

        format = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));

        txtTitleAdmin = findViewById(R.id.txtTitleDetailAdmin);
        txtAuthorAdmin = findViewById(R.id.txtAuthorDetailAdmin);
        txtDescriptionAdmin = findViewById(R.id.txtDescriptionDetailAdmin);
        txtPriceAdmin = findViewById(R.id.txtPriceDetailAdmin);
        imgBookAdmin = findViewById(R.id.imgBookDetailAdmin);

        backDetailAdmin = findViewById(R.id.btnBackDetailAdmin);
        btnEditAdmin = findViewById(R.id.btnEditDetailAdmin);
        btnDeleteAdmin = findViewById(R.id.btnDeleteDetailAdmin);

        backDetailAdmin.setOnClickListener(v -> {
            finish();
        });

        editBookLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean updated = result.getData().getBooleanExtra("updated", false);
                        if (updated) {
                            long bookId = getIntent().getLongExtra("id", -1);
                            if (bookId != -1) {
                                refreshBookDetails(bookId); // 🔁 Actualizás la UI
                            }
                            Intent intent = new Intent();
                            intent.putExtra("updated", true);
                            setResult(RESULT_OK, intent);
                        }
                    }
                }
        );


        bookBundle = getIntent().getExtras();

        if(bookBundle != null){
            String title = bookBundle.getString("title","Not title");
            String author = bookBundle.getString("author","Not author");
            String description = bookBundle.getString("description","Not description");
            String image = bookBundle.getString("image","Not image");
            Double price = bookBundle.getDouble("price",0.0);


            txtTitleAdmin.setText(title);
            txtAuthorAdmin.setText(author);
            txtDescriptionAdmin.setText(description);
            txtPriceAdmin.setText(format.format(price));

            if (image != null && !image.isEmpty()) {
                Picasso.get()
                        .load(image)
                        .placeholder(R.drawable.loadinbook)
                        .error(R.drawable.errorimage)
                        .into(imgBookAdmin);
            }

            btnEditAdmin.setOnClickListener(v -> goToActivity(AdminBookEditActivity.class, bookBundle));
            btnDeleteAdmin.setOnClickListener(v -> deleteBookById());
        }
    }

    private void goToActivity(Class<?> actividad,Bundle bundle) {
        Intent intent = new Intent(this, actividad);
        intent.putExtras(bundle);
        editBookLauncher.launch(intent);
    }

    private void deleteBookById(){

        long bookId = getIntent().getLongExtra("id", -1);

        BookController.deleteBookAsync(bookId, new OnBookDeleteResult() {
            @Override
            public void onSuccess(boolean success) {
                runOnUiThread(() -> {
                    if (success){
                        Toast.makeText(AdminBookDetailActivity.this, "Libro Eliminado a Correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("updated", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(AdminBookDetailActivity.this, "No se pudo eliminar el libro. Verifica si existe o si tienes permisos.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(AdminBookDetailActivity.this, "Error Inesperado", Toast.LENGTH_SHORT).show()
                );
            }
        });

    }

    private void refreshBookDetails(long bookId) {
        BookController.getBookByIdAsync(bookId, new OnBookGetResult() {
            @Override
            public void onSuccess(Book book) {
                runOnUiThread(() -> {
                    txtTitleAdmin.setText(book.getTitle());
                    txtAuthorAdmin.setText(book.getAuthor());
                    txtDescriptionAdmin.setText(book.getDescription());
                    txtPriceAdmin.setText(format.format(book.getPrice()));

                    if (book.getImage() != null && !book.getImage().isEmpty()) {
                        Picasso.get()
                                .load(book.getImage())
                                .placeholder(R.drawable.loadinbook)
                                .error(R.drawable.errorimage)
                                .into(imgBookAdmin);
                    }
                    updateBundle(book);
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(AdminBookDetailActivity.this, "Error al obtener el libro", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
    private void updateBundle(Book book) {
        bookBundle.putString("title", book.getTitle());
        bookBundle.putString("author", book.getAuthor());
        bookBundle.putString("description", book.getDescription());
        bookBundle.putString("image", book.getImage());
        bookBundle.putDouble("price", book.getPrice());
    }



}