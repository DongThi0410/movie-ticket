package com.example.app02.data

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat.Token
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("user_prefs")

class DataStore(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val ROLE_KEY = stringPreferencesKey("user_role")
    }

    val token: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]
    }

    val role: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[ROLE_KEY]
    }
    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
        val saved = getToken()
        Log.d("DataStore Save"," ${saved}")
    }

    suspend fun getToken(): String? {
        return context.dataStore.data
            .map { prefs -> prefs[TOKEN_KEY] }
            .firstOrNull()
    }

    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }

    suspend fun getUserIdFromToken(): Int? {
        val token = getToken() ?: return null
        val jwt = JWT(token)
        val id = jwt.subject?.toInt()
        Log.d("get Id", "${id}")
        return id
    }
    suspend fun saveRole(role: String){
        context.dataStore.edit { prefs ->
            prefs[ROLE_KEY] = role
        }
        Log.d("DATASTORE SAVE ROLE", role)
    }

    suspend fun getRole():String?{
        return context.dataStore.data
            .map { prefs -> prefs[ROLE_KEY] }
            .firstOrNull()
    }
    suspend fun extractAndSave() {
        val token = getToken() ?: return
        val jwt = JWT(token)
        val rawRole = jwt.getClaim("role").asString() ?: "ROLE_USER"
        val role = rawRole.replace("ROLE_", "", ignoreCase = true).lowercase()

        saveRole(role)
        Log.d("extracted role", role)
    }

}