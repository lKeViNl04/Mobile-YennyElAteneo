package com.example.yennyelateneo.data.supabase

import com.example.yennyelateneo.data.model.Favorite
import com.example.yennyelateneo.data.model.Favoritee
import com.example.yennyelateneo.data.model.toJavaFavorite
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object SupabaseFavoriteService {

    @JvmStatic
    suspend fun getFavoritesByUser(userId: String): List<Favorite> = withContext(Dispatchers.IO) {
        SupabaseManager.supabase.from("favorites")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<Favoritee>()
            .map{ it.toJavaFavorite()}
    }


    @JvmStatic
    suspend fun addFavorite(userId: String, bookId: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            SupabaseManager.supabase
                .from("favorites")
                .insert(
                    buildJsonObject {
                        put("user_id", userId)
                        put("book_id", bookId)
                    }
                )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }





    /*
    suspend fun addFavorite(userId: Long, bookId: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            SupabaseManager.supabase.from("favorites").insert(
                mapOf("user_id" to userId, "book_id" to bookId)
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeFavorite(userId: Long, bookId: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            SupabaseManager.supabase.from("favorites").delete {
                filter {
                    eq("user_id", userId)
                    eq("book_id", bookId)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
*/
    @JvmStatic
    fun getFavoritesByUserBlocking(userId: String): List<Favorite> = runBlocking {
            getFavoritesByUser(userId)
    }

    @JvmStatic
    fun addFavoriteBlocking(userId: String, bookId: Long): Boolean = runBlocking {
        addFavorite(userId, bookId)
    }


}
