package com.esei.grvidal.nighttime.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.createDataStore
import com.esei.grvidal.nighttime.viewmodels.City
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


private const val APP_PREFERENCES_NAME = "night_time_data_store"
private const val TAG = "DataStoreManager"

data class LoginData(val username: String, val password: String, val accepted: Boolean)

class DataStoreManager private constructor(context: Context) {

    // Reference to dataStore
    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = APP_PREFERENCES_NAME
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
            val checked = preferences[PreferencesKeys.CONFIRMATION_CREDENTIALS] ?: false

            LoginData(login, password, checked)
        }

    val cityPreferences: Flow<City> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val id = preferences[PreferencesKeys.CITY_ID] ?: 1
            val name = preferences[PreferencesKeys.CITY_NAME] ?: "Ourense"

            City(id, name)
        }


    private object PreferencesKeys {
        val LOGIN_CREDENTIALS = stringPreferencesKey("username_login")
        val PASSWORD_CREDENTIALS = stringPreferencesKey("password_login")
        val CONFIRMATION_CREDENTIALS = booleanPreferencesKey("true_credentials")
        val CITY_ID = longPreferencesKey("city_id")
        val CITY_NAME = stringPreferencesKey("city_name")
    }

    /**
     * Async updates credentials in the DataStore, every time data is changed,
     * [PreferencesKeys.CONFIRMATION_CREDENTIALS] is set to false to avoid false logins
     */
    suspend fun updateLoginCredentials(
        login: String? = null,
        password: String? = null,
        isChecked: Boolean? = null
    ) {

        Log.d(TAG, "updateLoginCredentials: new credentials \"$login\", \"$password\"")

        dataStore.edit { preferences ->
            login?.let { preferences[PreferencesKeys.LOGIN_CREDENTIALS] = it }
            password?.let { preferences[PreferencesKeys.PASSWORD_CREDENTIALS] = it }
            isChecked?.let { preferences[PreferencesKeys.CONFIRMATION_CREDENTIALS] = it }
        }
    }

    suspend fun credentialsChecked() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CONFIRMATION_CREDENTIALS] = true
        }
    }

    suspend fun credentialsFailed() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CONFIRMATION_CREDENTIALS] = false
        }
    }

    suspend fun updateCityData(id: Long, name: String) {

        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CITY_ID] = id
            preferences[PreferencesKeys.CITY_NAME] = name
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: DataStoreManager? = null

        fun getInstance(context: Context): DataStoreManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE?.let {
                    return it
                }

                val instance = DataStoreManager(context = context)
                INSTANCE = instance
                instance
            }
        }

    }
}
