package com.example.yennyelateneo.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yennyelateneo.R;
import com.example.yennyelateneo.data.model.Book;
import com.squareup.picasso.Picasso;

import java.util.List;


public class BookAdapter extends ArrayAdapter<Book> {

        private Context mContext;
        private List<Book> mBooks;

        public BookAdapter(Context context, List<Book> books){
            super(context, 0 , books);
            mContext = context;
            mBooks = books;
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent){

            Book book = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_view, parent, false);
            }

            ImageView imgLibro = convertView.findViewById(R.id.imgBook);
            TextView txtTitulo = convertView.findViewById(R.id.txtTitle);

            String imageUrl = book.getImage();

            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.loadinbook)
                    .error(R.drawable.errorimage)
                    .into(imgLibro);
            txtTitulo.setText(book.getTitle());

            imgLibro.setOnClickListener(v -> {

                Bundle bundle = new Bundle();
                bundle.putLong("id", book.getId());
                bundle.putString("title", book.getTitle());
                bundle.putString("author", book.getAuthor());
                bundle.putString("description", book.getDescription());
                bundle.putString("image", book.getImage());
                bundle.putDouble("price",book.getPrice());

                Intent intent = new Intent(mContext, BookDetailActivity.class);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            });


            return convertView;


        }



}
