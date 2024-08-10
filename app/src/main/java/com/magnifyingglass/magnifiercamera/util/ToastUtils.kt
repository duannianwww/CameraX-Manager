package com.magnifyingglass.magnifiercamera.util

import android.app.Activity
import android.widget.Toast

object ToastUtils {
    fun showShort(activity: Activity, text: String) {
        activity.runOnUiThread {
            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
        }
    }

    fun showShort(activity: Activity, id: Int) {
        activity.runOnUiThread {
            Toast.makeText(activity, activity.getString(id), Toast.LENGTH_SHORT).show()
        }
    }
}