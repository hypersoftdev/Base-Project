package com.hypersoft.baseproject.core.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withCreated
import androidx.lifecycle.withResumed
import androidx.lifecycle.withStarted
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun LifecycleOwner.launchWhenCreated(callback: () -> Unit) {
    lifecycleScope.launch { lifecycle.withCreated(callback) }
}

fun LifecycleOwner.launchWhenStarted(callback: () -> Unit) {
    lifecycleScope.launch { lifecycle.withStarted(callback) }
}

fun LifecycleOwner.launchWhenResumed(callback: () -> Unit) {
    lifecycleScope.launch { lifecycle.withResumed(callback) }
}

inline fun LifecycleOwner.repeatWhenStarted(lifecycleState: Lifecycle.State = Lifecycle.State.STARTED, crossinline block: suspend () -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(lifecycleState) {
            block()
        }
    }
}

inline fun <T> LifecycleOwner.collectWhenStarted(flow: Flow<T>, lifecycleState: Lifecycle.State = Lifecycle.State.STARTED, crossinline block: suspend (T) -> Unit) {
    repeatWhenStarted(lifecycleState = lifecycleState) { flow.collect { block(it) } }
}