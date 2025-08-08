package com.example.yennyelateneo.data.model;

public class Favorite {
    private Long id;
    private String user_id;
    private long book_id;

    public Favorite() {}

    public Favorite(Long id, String user_id, long book_id) {
        this.id = id;
        this.user_id = user_id;
        this.book_id = book_id;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public long getBook_id() { return book_id; }
    public void setBook_id(long book_id) { this.book_id = book_id; }

}

