package com.example.yennyelateneo.ui.user;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yennyelateneo.R;
import com.example.yennyelateneo.domain.controller.FavoriteController;
import com.example.yennyelateneo.domain.interfaces.OnBookFavoriteResult;
import com.example.yennyelateneo.data.model.SessionManager;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class BookDetailActivity extends AppCompatActivity {

    private TextView txtTitle, txtAuthor, txtDescription, txtPrice,likeIcon, dislikeIcon;
    private ImageView imgBook;
    private ImageButton backDetail, btnFavorite;
    private NumberFormat format;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_detail);
        format = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));

        txtTitle = findViewById(R.id.txtTitleDetail);
        txtAuthor = findViewById(R.id.txtAuthorDetail);
        txtDescription = findViewById(R.id.txtDescriptionDetail);
        txtPrice = findViewById(R.id.txtPriceDetail);
        likeIcon = findViewById(R.id.likeIconDetail);
        dislikeIcon = findViewById(R.id.dislikeIconDetail);
        imgBook = findViewById(R.id.imgBookDetail);
        backDetail = findViewById(R.id.btnBackDetail);

        btnFavorite = findViewById(R.id.btnFavoriteDetail);
        backDetail.setOnClickListener(v -> finish());

        Bundle book = getIntent().getExtras();

        if(book != null){
            String title = book.getString("title","Not title");
            String author = book.getString("author","Not author");
            String description = book.getString("description","Not description");
            String image = book.getString("image","Not image");
            Double price = book.getDouble("price",0.0);
            int like = book.getInt("like",0);
            int dislike = book.getInt("dislike",0);


            txtTitle.setText(title);
            txtAuthor.setText(author);
            txtDescription.setText(description);
            txtPrice.setText(format.format(price));
            likeIcon.setText(String.valueOf(like));
            dislikeIcon.setText(String.valueOf(dislike));

            if (image != null && !image.isEmpty()) {
                Picasso.get()
                        .load(image)
                        .placeholder(R.drawable.loadinbook)
                        .error(R.drawable.errorimage)
                        .into(imgBook);
            }

            btnFavorite.setOnClickListener(v->addFavorites());
        }
    }

    private void addFavorites() {
        String userId = SessionManager.getInstance().getUserId();
        long bookId = getIntent().getLongExtra("id", -1);

        FavoriteController.addFavoriteAsync(userId, bookId, new OnBookFavoriteResult() {
            @Override
            public void onSuccess(boolean success) {
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(BookDetailActivity.this, "Libro agregado a favoritos", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BookDetailActivity.this, "Ya está en favoritos o falló el insert", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(BookDetailActivity.this, "Error al agregar favorito", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

}