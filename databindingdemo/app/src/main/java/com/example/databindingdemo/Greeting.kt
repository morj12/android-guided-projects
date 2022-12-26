package com.example.databindingdemo

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

/**
 * Private fields and public properties with annotations and notifications
 */

data class Greeting (
    private var _senderName: String,
    private var _greetingText: String
) : BaseObservable() {

    var senderName: String
    @Bindable get() = _senderName
    set(value) {
        _senderName = value
        notifyPropertyChanged(BR.senderName)
    }

    var greetingText: String
        @Bindable get() = _greetingText
        set(value) {
            _greetingText = value
            notifyPropertyChanged(BR.greetingText)
        }
}
