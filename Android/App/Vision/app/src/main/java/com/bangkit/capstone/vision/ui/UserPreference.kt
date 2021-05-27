package com.bangkit.capstone.vision.ui

import android.content.Context
import com.bangkit.capstone.vision.model.UserModel

internal class UserPreference(context: Context) {

    companion object {
        const val PREFS_NAME = "user_pref"
        const val USERNAME = "username"
    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUser(value: UserModel) {
        val editor = preferences.edit()
        editor.putString(USERNAME, value.username)
        editor.apply()
    }

    fun getUser(): UserModel {
        val model = UserModel()
        model.username = preferences.getString(USERNAME, "")
        return model
    }

    fun removeUser() {
        val editor = preferences.edit()
        editor.remove(USERNAME)
        editor.apply()
    }
}