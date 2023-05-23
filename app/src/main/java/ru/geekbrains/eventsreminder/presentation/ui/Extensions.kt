package ru.geekbrains.eventsreminder.presentation.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import android.os.Parcelable

tailrec fun Context.findActivity(): Activity {
    if (this is Activity) {
        return this
    } else {
        if (this is ContextWrapper) {
            return this.baseContext.findActivity()
        }
        throw java.lang.IllegalStateException("Context chain has no activity")
    }
}
/**
 * Синтаксический сахар для извлечения parcelable из bundle
 * */
inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}