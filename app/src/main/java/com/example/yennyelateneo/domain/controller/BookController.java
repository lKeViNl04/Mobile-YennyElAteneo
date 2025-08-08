package com.example.yennyelateneo.domain.controller;



import com.example.yennyelateneo.domain.interfaces.OnBookAddResult;
import com.example.yennyelateneo.domain.interfaces.OnBookEditResult;
import com.example.yennyelateneo.domain.interfaces.OnBookGetResult;
import com.example.yennyelateneo.domain.interfaces.OnBooksGetResult;
import com.example.yennyelateneo.data.model.Book;
import com.example.yennyelateneo.data.supabase.SupabaseBookService;
import com.example.yennyelateneo.domain.interfaces.OnBookDeleteResult;

import java.util.List;


public class BookController  {


    public static void getBooksAsync(OnBooksGetResult callback) {
        new Thread(() -> {
            try {
                List<Book> books = SupabaseBookService.getBooksBlocking();
                callback.onSuccess(books);
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    public static void getBookByIdAsync(Long bookId, OnBookGetResult callback) {
        new Thread(() -> {
            try {
                Book book = SupabaseBookService.getBookByIdBlocking(bookId);
                callback.onSuccess(book);
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }



    public static void deleteBookAsync(Long bookId, OnBookDeleteResult callback){
        if (bookId == null) {
            callback.onError(new IllegalArgumentException("ID del libro es null"));
            return;
        }

        new Thread(()->{
            try {
                boolean successDelete = SupabaseBookService.deleteBookByIdBlocking(bookId);
                callback.onSuccess(successDelete);
            }catch (Exception e){
                callback.onError(e);
            }
        }
        ).start();
    }

    public static void addBookAsync(Book book,byte[] imageBytes, String fileName, OnBookAddResult callback){
        new Thread(()->{
            try {
                boolean successAdd = SupabaseBookService.addBookBlocking(book.toKotlinBookk(),imageBytes,fileName);
                callback.onSuccess(successAdd);
            }catch (Exception e){
                callback.onError(e);
            }
        }
        ).start();
    }


    public static void editBookAsync(Book book,byte[] imageBytes, String fileName, OnBookEditResult callback){
        new Thread( ()->{
            try{
                boolean successEdit = SupabaseBookService.updateBookByIdBlocking(book.toKotlinBookk(),imageBytes,fileName);
                callback.onSuccess(successEdit);
            }catch (Exception e){
                callback.onError(e);
            }

        }).start();
    }
}
