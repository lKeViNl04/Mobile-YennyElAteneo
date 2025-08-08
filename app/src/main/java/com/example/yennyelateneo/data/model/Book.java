package com.example.yennyelateneo.data.model;

public class Book {

    private Long id;
    private String title;
    private String author;
    private String description;
    private double price;
    private String image;


    public Book(Long id, String title, String author, String description, double price, String image)
    {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public Book(String title, String author, String description, double price, String image)
    {
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.image = image;
    }


    public Long getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public double getPrice() {
        return price;
    }


    public Bookk toKotlinBookk() {
        return new Bookk(id, title, author, description, price, image);
    }
}
