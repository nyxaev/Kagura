package com.kagura.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val PITY_COUNT = intPreferencesKey("pity_count")
        val TOTAL_PULLS = intPreferencesKey("total_pulls")
        val STREAK_COUNT = intPreferencesKey("streak_count")
        val LAST_STREAK_DATE = longPreferencesKey("last_streak_date")
    }

    val pityCount: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.PITY_COUNT] ?: 0
    }

    val totalPulls: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.TOTAL_PULLS] ?: 0
    }

    val streakCount: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.STREAK_COUNT] ?: 0
    }

    suspend fun incrementPity(): Int {
        var newPity = 0
        dataStore.edit { prefs ->
            val current = prefs[Keys.PITY_COUNT] ?: 0
            newPity = current + 1
            prefs[Keys.PITY_COUNT] = newPity
        }
        return newPity
    }

    suspend fun resetPity() {
        dataStore.edit { prefs ->
            prefs[Keys.PITY_COUNT] = 0
        }
    }

    suspend fun incrementTotalPulls() {
        dataStore.edit { prefs ->
            val current = prefs[Keys.TOTAL_PULLS] ?: 0
            prefs[Keys.TOTAL_PULLS] = current + 1
        }
    }

    suspend fun checkAndUpdateStreak() {
        dataStore.edit { prefs ->
            val lastDateMs = prefs[Keys.LAST_STREAK_DATE] ?: 0L
            val currentStreak = prefs[Keys.STREAK_COUNT] ?: 0

            val now = Calendar.getInstance()
            val todayStart = now.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val yesterdayStart = todayStart - (24 * 60 * 60 * 1000L)

            if (lastDateMs < yesterdayStart) {
                prefs[Keys.STREAK_COUNT] = 1
                prefs[Keys.LAST_STREAK_DATE] = System.currentTimeMillis()
            } else if (lastDateMs in yesterdayStart until todayStart) {
                prefs[Keys.STREAK_COUNT] = currentStreak + 1
                prefs[Keys.LAST_STREAK_DATE] = System.currentTimeMillis()
            } else if (lastDateMs < todayStart) {
                prefs[Keys.STREAK_COUNT] = if (currentStreak == 0) 1 else currentStreak
                prefs[Keys.LAST_STREAK_DATE] = System.currentTimeMillis()
            }
        }
    }
}
