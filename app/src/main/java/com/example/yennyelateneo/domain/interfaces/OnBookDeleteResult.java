package com.example.yennyelateneo.domain.interfaces;

public interface OnBookDeleteResult {
    void onSuccess(boolean success);
    void onError(Exception e);
}
