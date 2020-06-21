package com.vianh.blogtruyen

import androidx.lifecycle.Observer

open class Event<out T>(private val content: T) {
    var hasBeenHandle = false

    fun getContentIfNotHandled(): T? {
        return if(hasBeenHandle) {
            null
        } else {
            hasBeenHandle = true
            content
        }
    }

    fun peekContent(): T = content


    class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
        override fun onChanged(event: Event<T>?) {
            event?.getContentIfNotHandled()?.let {
                onEventUnhandledContent(it)
            }
        }
    }
}