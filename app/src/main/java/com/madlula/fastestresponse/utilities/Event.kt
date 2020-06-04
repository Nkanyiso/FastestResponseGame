package com.madlula.fastestresponse.utilities

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import java.util.concurrent.atomic.AtomicBoolean


class Event<T>(private val data: T) {
    private val handled = AtomicBoolean(false)

    private fun ifNotHandled(block: (data: T) -> Unit) {
        if (!handled.getAndSet(true)) {
            block(data)
        }
    }

    fun peek() = data

    class Observer<T>(private val onChanged: (data: T) -> Unit)
        : androidx.lifecycle.Observer<Event<T>> {

        override fun onChanged(t: Event<T>?) {
            t?.ifNotHandled(onChanged)
        }
    }
}

@MainThread
fun <T> LiveData<Event<T>>.observeEvent(owner: LifecycleOwner, onChanged: (data: T) -> Unit) {
    observe(owner, Event.Observer { onChanged(it) })
}