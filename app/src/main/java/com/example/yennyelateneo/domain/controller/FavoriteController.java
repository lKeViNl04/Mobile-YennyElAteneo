package com.example.yennyelateneo.domain.controller;


import com.example.yennyelateneo.domain.interfaces.OnBooksGetResult;
import com.example.yennyelateneo.domain.interfaces.OnBookFavoriteResult;
import com.example.yennyelateneo.data.model.Book;
import com.example.yennyelateneo.data.model.Favorite;
import com.example.yennyelateneo.data.model.SessionManager;
import com.example.yennyelateneo.data.supabase.SupabaseBookService;
import com.example.yennyelateneo.data.supabase.SupabaseFavoriteService;

import java.util.ArrayList;
import java.util.List;

public class FavoriteController {


    public static void getFavoriteBooksByUserAsync(OnBooksGetResult callback) {
        new Thread(() -> {
            try {

                String userId = SessionManager.getInstance().getUserId();

                if (userId == null || userId.isEmpty()) {
                    callback.onError(new IllegalStateException("El usuario no está logueado."));
                    return;
                }

                List<Favorite> favorites = SupabaseFavoriteService.getFavoritesByUserBlocking(userId);
                if (favorites.isEmpty()) {
                    callback.onSuccess(new ArrayList<>());
                    return;
                }

                List<Long> bookIds = new ArrayList<>();
                for (Favorite f : favorites) {
                    bookIds.add(f.getBook_id());
                }

                List<Book> books = SupabaseBookService.getBooksByIdsBlocking(bookIds);
                callback.onSuccess(books);
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    public static void addFavoriteAsync(String userId, long bookId, OnBookFavoriteResult callback) {
        new Thread(() -> {
            try {
                boolean success = SupabaseFavoriteService.addFavoriteBlocking(userId, bookId);
                callback.onSuccess(success);
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

}
