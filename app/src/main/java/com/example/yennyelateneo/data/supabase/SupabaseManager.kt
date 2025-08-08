package com.example.yennyelateneo.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage


object SupabaseManager {
    lateinit var supabase: SupabaseClient
    private var initialized = false

    @JvmStatic
    fun initialize(url: String, key: String) {
        if (initialized) return

        supabase = createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
            install(Storage)
            install(Functions)
        }
        initialized = true
    }

}
