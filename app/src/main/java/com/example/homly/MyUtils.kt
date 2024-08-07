package com.example.homly


import android.content.Context
import android.widget.Toast

object MyUtils {

    // Constants User creation types
    const val USER_TYPE_GOOGLE = "Google"
    const val USER_TYPE_EMAIL = "Email"
    const val USER_TYPE_PHONE = "Phone"

    // A Function to get current timeStamp
    fun toast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // A Function to get current timestamp
    fun timestamp(): Long{
        return System.currentTimeMillis()
    }
}