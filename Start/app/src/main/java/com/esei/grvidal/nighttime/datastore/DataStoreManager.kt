package com.esei.grvidal.nighttime.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import java.io.IOException

/* https://developer.android.com/codelabs/android-preferences-datastore#2 */

private const val APP_PREFERENCES_NAME = "night_time_data_store"

data class LoginData(val username: String, val password: String)

class DataStoreManager private constructor(context: Context) {

    // Reference to dataStore
    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = "settings"
    )

    // Returns data via flow
    val userPreferences: Flow<LoginData> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val login = preferences[PreferencesKeys.LOGIN_CREDENTIALS] ?: ""
            val password = preferences[PreferencesKeys.PASSWORD_CREDENTIALS] ?: ""

            LoginData(login, password)
        }


    private object PreferencesKeys {
        val LOGIN_CREDENTIALS = stringPreferencesKey("username_login")
        val PASSWORD_CREDENTIALS = stringPreferencesKey("password_login")
    }

    // Async update DataStore of the credentials
    suspend fun updateLoginCredentials(login : String, password: String){

        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LOGIN_CREDENTIALS] = login
            preferences[PreferencesKeys.PASSWORD_CREDENTIALS] = password
        }
    }

    companion object{
        @Volatile
        private var INSTANCE : DataStoreManager? = null

        fun getInstance(context: Context): DataStoreManager{
            return INSTANCE ?: synchronized(this){
                INSTANCE?.let{
                    return it
                }

                val instance = DataStoreManager(context = context)
                INSTANCE = instance
                instance
            }
        }

    }
}
