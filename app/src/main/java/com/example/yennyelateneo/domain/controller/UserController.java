package com.example.yennyelateneo.domain.controller;

import com.example.yennyelateneo.domain.interfaces.OnLoginResult;
import com.example.yennyelateneo.domain.interfaces.OnRegisterResult;
import com.example.yennyelateneo.data.model.SessionManager;
import com.example.yennyelateneo.data.supabase.SupabaseUserService;

import io.github.jan.supabase.auth.user.UserInfo;
import io.github.jan.supabase.auth.user.UserSession;


public class UserController {

    static SessionManager sessionManager = SessionManager.getInstance();

    public static void login(String email, String password , OnLoginResult callback) {
        new Thread(() -> {

            try {
                boolean success = SupabaseUserService.loginBlocking(email, password);
                saveSessionIfSuccessful(success);
                callback.onSuccess(success);
            }catch (Exception e){
                callback.onError(e);
            }
        }).start();
    }

    public static void register(String email, String password, String username, OnRegisterResult callback) {
        new Thread(() -> {
            try {
                boolean registered = SupabaseUserService.signUpBlocking(email, password, username);
                callback.onSuccess(registered);
            }catch (Exception e){
                callback.onError(e);
            }
        }).start();
    }


    public static void logout() {
        new Thread(() -> {
            boolean success = SupabaseUserService.logoutBlocking();
            if (success) {
                sessionManager.clearSession();
            }
        }).start();
    }


    private static void saveSessionIfSuccessful(boolean success) {
        if (success) {
            UserSession session = SupabaseUserService.getUserSession();
            UserInfo userInfo = SupabaseUserService.getUserInfo();

            if (session != null && userInfo != null) {
                String role = SupabaseUserService.getUserRoleBlocking(userInfo.getId());
                sessionManager.saveSession(userInfo, session,role);
            }
        }
    }

    private static void checkIfAdminAndShowButton (){

    }

}
