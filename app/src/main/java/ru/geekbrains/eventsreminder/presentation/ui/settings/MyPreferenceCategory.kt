package ru.geekbrains.eventsreminder.presentation.ui.settings

import android.R
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder


class MyPreferenceCategory : PreferenceCategory {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(
        context: Context, attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
    }
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val titleView = holder.findViewById(R.id.title) as TextView
        titleView.setTextColor(context.getColor(ru.geekbrains.eventsreminder.R.color.color_primary_dark))
    }
}