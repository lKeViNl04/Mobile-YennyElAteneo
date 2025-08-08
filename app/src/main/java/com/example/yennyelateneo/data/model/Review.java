package com.example.yennyelateneo.data.model;

public class Review {
    private long id;
    private long user_id;
    private long book_id;
    private boolean liked;
    private boolean disliked;

    public Review() {}

    public Review(long id, long user_id, long book_id, boolean liked, boolean disliked) {
        this.id = id;
        this.user_id = user_id;
        this.book_id = book_id;
        this.liked = liked;
        this.disliked = disliked;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUser_id() { return user_id; }
    public void setUser_id(long user_id) { this.user_id = user_id; }

    public long getBook_id() { return book_id; }
    public void setBook_id(long book_id) { this.book_id = book_id; }

    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }

    public boolean isDisliked() { return disliked; }
    public void setDisliked(boolean disliked) { this.disliked = disliked; }
}

