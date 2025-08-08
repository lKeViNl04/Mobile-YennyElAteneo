package com.example.yennyelateneo.domain.interfaces;

import com.example.yennyelateneo.data.model.Book;

import java.util.List;

public interface OnBooksGetResult {
    void onSuccess(List<Book> books);
    void onError(Exception e);
}
