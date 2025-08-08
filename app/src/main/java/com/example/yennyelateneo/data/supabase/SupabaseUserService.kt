package com.example.yennyelateneo.data.supabase

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

object SupabaseUserService {

    @JvmStatic
    suspend fun signUp(email: String, password: String, username: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                SupabaseManager.supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    data = buildJsonObject {
                        put("username", username)
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    @JvmStatic
    suspend fun login(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        try {
             SupabaseManager.supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val session = getUserSession()
            val user = getUserInfo()

            if (session == null || user == null) {
                return@withContext false
            }

            if (user.emailConfirmedAt == null) {
                return@withContext false
            }

            insertUserIfNeeded(
                user.id,
                user.email.toString(),
                user.userMetadata?.get("username")?.jsonPrimitive?.content.toString()
            )

            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @JvmStatic
    suspend fun logout(): Boolean = withContext(Dispatchers.IO) {
        try {
            SupabaseManager.supabase.auth.signOut()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @JvmStatic
    suspend fun insertUserIfNeeded(userId: String, email: String, username: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val result = SupabaseManager.supabase
                    .from("users")
                    .select {
                        filter {
                            eq("id", userId)
                        }
                    }
                if (result.data.isEmpty()) {
                    return@withContext true
                }
                SupabaseManager.supabase.from("users").insert(
                    buildJsonObject {
                        put("id", userId)
                        put("email", email )
                        put("username", username)
                    }
                )
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    @JvmStatic
    suspend fun getUserRole(userId: String): String? = withContext(Dispatchers.IO) {
        try {
            val result = SupabaseManager.supabase
                .from("users")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }

            if (result.data.isNotEmpty()) {
                val jsonArray = Json.parseToJsonElement(result.data!!).jsonArray
                val user = jsonArray.first().jsonObject
                val role = user["role"]?.jsonPrimitive?.content
                return@withContext role
            } else {
                return@withContext null
            }

        } catch (e: Exception) {
            return@withContext null
        }
    }


    @JvmStatic
    fun getUserSession(): UserSession? {
        return SupabaseManager.supabase.auth.currentSessionOrNull();
    }

    @JvmStatic
    fun getUserInfo(): UserInfo? {
        return SupabaseManager.supabase.auth.currentUserOrNull()
    }

    //
    @JvmStatic
    fun signUpBlocking(email: String, password: String, username: String): Boolean = runBlocking {
        signUp(email, password, username)
    }

    @JvmStatic
    fun loginBlocking(email: String, password: String): Boolean = runBlocking {
        login(email, password)
    }

    @JvmStatic
    fun logoutBlocking(): Boolean = runBlocking {
        logout()
    }

    @JvmStatic
    fun getUserRoleBlocking(userId: String): String? = runBlocking {
        getUserRole(userId)
    }

}
