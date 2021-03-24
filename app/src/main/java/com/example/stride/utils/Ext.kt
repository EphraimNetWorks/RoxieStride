package com.example.stride.utils

import android.content.Context
import android.widget.Toast

fun Context.toast(msg:String){
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

data class Optional<T>(val value: T?)

fun <T> T?.asOptional() = Optional(this)