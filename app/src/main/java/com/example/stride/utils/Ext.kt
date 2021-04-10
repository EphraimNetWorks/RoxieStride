package com.example.stride.utils

import android.app.Activity
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat


fun Context.toast(msg: String){
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

data class Optional<T>(val value: T?)

fun <T> T?.asOptional() = Optional(this)

fun View.hide() { visibility = View.GONE }

fun View.show() { visibility = View.VISIBLE }

fun ActionBar?.setTitleColor(color: Int) {
    val text = SpannableString(this?.title ?: "")
    text.setSpan(ForegroundColorSpan(color), 0, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    this?.title = text
}

fun Activity.dismissKeyboard() {
    val inputMethodManager = getSystemService( Context.INPUT_METHOD_SERVICE ) as InputMethodManager
    if( inputMethodManager.isAcceptingText )
        inputMethodManager.hideSoftInputFromWindow( this.currentFocus?.windowToken,  0)
}