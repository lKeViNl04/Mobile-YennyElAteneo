package com.example.yennyelateneo.domain.interfaces;

public interface OnLoginResult {
    void onSuccess(boolean result);
    void onError(Exception e);
}
