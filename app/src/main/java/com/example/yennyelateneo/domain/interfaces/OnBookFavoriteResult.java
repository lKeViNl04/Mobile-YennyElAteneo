package com.example.yennyelateneo.domain.interfaces;

public interface OnBookFavoriteResult {
    void onSuccess(boolean success);
    void onError(Exception e);
}