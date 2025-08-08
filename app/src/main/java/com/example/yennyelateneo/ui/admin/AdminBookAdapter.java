package com.example.yennyelateneo.ui.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yennyelateneo.R;
import com.example.yennyelateneo.data.model.Book;
import com.example.yennyelateneo.domain.interfaces.OnBookClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdminBookAdapter extends ArrayAdapter<Book>{
    private Context mContext;
    private List<Book> mBooks;

    private ImageView imgBook;
    private TextView txtTitle;
    private TextView txtAuthor;
    private TextView txtDescripcion;
    private TextView txtPrice;
    private OnBookClickListener listener;


    public AdminBookAdapter(Context context, List<Book> books){
        super(context, 0 , books);
        mContext = context;
        mBooks = books;
    }
    public void setOnBookClickListener(OnBookClickListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){

        Book book = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_book_view, parent, false);
        }

        imgBook = convertView.findViewById(R.id.imgBookAdmin);
        txtTitle = convertView.findViewById(R.id.txtTitleAdmin);
        txtAuthor = convertView.findViewById(R.id.txtAuthoAdmin);
        txtPrice = convertView.findViewById(R.id.txtPriceAdmin);
        txtDescripcion = convertView.findViewById(R.id.txtDescriptionAdmin);


        String imageUrl = book.getImage();

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.loadinbook)
                .error(R.drawable.errorimage)
                .into(imgBook);
        txtTitle.setText(book.getTitle());
        txtAuthor.setText(book.getAuthor());
        txtPrice.setText(String.valueOf(book.getPrice()));
        txtDescripcion.setText(book.getDescription());


        convertView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(book);
            }
        });


        return convertView;


    }
}