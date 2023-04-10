package ru.geekbrains.eventsreminder.presentation.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Здесь показывается интервал для отображения списка событий"
    }
    val text: LiveData<String> = _text
}