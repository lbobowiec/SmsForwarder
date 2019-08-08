package com.lubosoft.smsforwarder.utilities

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardUtils {

    fun hideKeyboardFrom(context: Context, view: View?) {
        view?.let {
            val inputMethodManager =  context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun hideKeyboard(activity: Activity) {
        hideKeyboardFrom(activity, activity.currentFocus)
    }

}