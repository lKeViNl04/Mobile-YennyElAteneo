package com.example.yennyelateneo.data.model;

import io.github.jan.supabase.auth.user.UserInfo;
import io.github.jan.supabase.auth.user.UserSession;

public class SessionManager {

    private static SessionManager instance;
    private String userId;
    private String email;
    private String token;
    private String username;
    private String role;

    private SessionManager() { }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Getters
    public String getUserId() { return this.userId; }
    public String getEmail() { return this.email; }
    public String getToken() { return this.token; }
    public String getUsername() { return this.username; }
    public String getRole(){return this.role;}

    // Guardar sesión
    public void saveSession(UserInfo userInfo, UserSession session, String role) {
        this.userId = userInfo != null ? userInfo.getId() : null;
        this.email = userInfo != null ? userInfo.getEmail() : null;
        this.token = session != null ? session.getAccessToken() : null;
        this.role = role;

        if (userInfo != null && userInfo.getUserMetadata() != null) {
            Object usernameValue = userInfo.getUserMetadata().get("username");
            username = usernameValue != null ? usernameValue.toString() : null;
        } else {
            username = null;
        }
    }

    // Limpiar sesión
    public void clearSession() {
        userId = null;
        email = null;
        token = null;
        username = null;
        role=null;
    }

}
