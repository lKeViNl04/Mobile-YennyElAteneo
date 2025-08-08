package com.example.yennyelateneo.domain.interfaces;

import com.example.yennyelateneo.data.model.Book;

import java.util.List;

public interface OnBookGetResult {
    void onSuccess(Book book);
    void onError(Exception e);
}
