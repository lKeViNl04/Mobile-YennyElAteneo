package com.example.yennyelateneo.ui.admin;

public class BookValidationResult {
    public final String fileName;
    public final byte[] imageBytes;
    public final double price;

    public BookValidationResult(String fileName, byte[] imageBytes, double price) {
        this.fileName = fileName;
        this.imageBytes = imageBytes;
        this.price = price;
    }
}
