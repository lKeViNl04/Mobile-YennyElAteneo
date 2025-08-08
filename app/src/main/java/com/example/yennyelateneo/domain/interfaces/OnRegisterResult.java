package com.example.yennyelateneo.domain.interfaces;

public interface OnRegisterResult {
    void onSuccess(boolean registered);
    void onError(Exception e);
}

