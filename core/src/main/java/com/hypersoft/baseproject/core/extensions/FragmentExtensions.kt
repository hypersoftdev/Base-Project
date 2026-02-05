package com.hypersoft.baseproject.core.extensions

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/* ---------------------------------------------- BackPress ---------------------------------------------- */

fun Fragment.onBackPressedDispatcher(callback: () -> Unit) {
    (activity as? AppCompatActivity)?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            callback.invoke()
        }
    })
}

/* ---------------------------------------------- Collectors ---------------------------------------------- */

inline fun Fragment.repeatWhen(lifecycleState: Lifecycle.State = Lifecycle.State.STARTED, crossinline block: suspend () -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
            block()
        }
    }
}

inline fun <T> Fragment.collectWhenCreated(flow: Flow<T>, crossinline block: suspend (T) -> Unit) {
    repeatWhen(lifecycleState = Lifecycle.State.CREATED) { flow.collect { block(it) } }
}

inline fun <T> Fragment.collectWhenStarted(flow: Flow<T>, crossinline block: suspend (T) -> Unit) {
    repeatWhen(lifecycleState = Lifecycle.State.STARTED) { flow.collect { block(it) } }
}

/* ----------------------------------------- Navigation's -----------------------------------------*/

/**
 *     Used launchWhenCreated, bcz of screen rotation
 *     Used launchWhenResumed, bcz of screen rotation
 * @param fragmentId : Current Fragment's Id (from Nav Graph)
 * @param action : Action / Id of other fragment
 * @param bundle : Pass bundle as a NavArgs to destination.
 */

fun Fragment.navigateTo(fragmentId: Int, action: Int, bundle: Bundle) {
    if (isAdded && isCurrentDestination(fragmentId)) {
        findNavController().navigate(action, bundle)
    }
}

fun Fragment.navigateTo(fragmentId: Int, action: Int) {
    if (isAdded && isCurrentDestination(fragmentId)) {
        findNavController().navigate(action)
    }
}

fun Fragment.navigateTo(fragmentId: Int, action: NavDirections) {
    if (isAdded && isCurrentDestination(fragmentId)) {
        findNavController().navigate(action)
    }
}

fun Fragment.popFrom(fragmentId: Int) {
    if (isAdded && isCurrentDestination(fragmentId)) {
        findNavController().popBackStack()
    }
}

fun Fragment.popFrom(fragmentId: Int, destinationFragmentId: Int, inclusive: Boolean = false) {
    if (isAdded && isCurrentDestination(fragmentId)) {
        findNavController().popBackStack(destinationFragmentId, inclusive)
    }
}

fun Fragment.isCurrentDestination(fragmentId: Int): Boolean {
    return findNavController().currentDestination?.id == fragmentId
}