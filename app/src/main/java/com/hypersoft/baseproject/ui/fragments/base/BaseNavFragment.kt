package com.hypersoft.baseproject.ui.fragments.base

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

abstract class BaseNavFragment : FragmentGeneral() {

    var callback: OnBackPressedCallback? = null

    /**
     *  @since : Write Code for BackPress Functionality
     */
    override fun onResume() {
        super.onResume()
        callback?.remove()
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
                this.remove()
            }
        }.also {
            (context as FragmentActivity).onBackPressedDispatcher.addCallback(this, it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navIconBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    abstract fun navIconBackPressed()
    abstract fun onBackPressed()

    /**
     *     Used launchWhenCreated, bcz of screen rotation
     *     Used launchWhenResumed, bcz of screen rotation
     * @param fragmentId : Current Fragment's Id (from Nav Graph)
     * @param action : Action / Id of other fragment
     * @param bundle : Pass bundle as a NavArgs to destination.
     */

    protected fun navigateTo(fragmentId: Int, action: Int, bundle: Bundle) {
        lifecycleScope.launchWhenCreated {
            if (isAdded && isCurrentDestination(fragmentId)) {
                findNavController().navigate(action, bundle)
            }
        }
    }

    protected fun navigateTo(fragmentId: Int, action: Int) {
        lifecycleScope.launchWhenCreated {
            if (isAdded && isCurrentDestination(fragmentId)) {
                findNavController().navigate(action)
            }
        }
    }

    protected fun navigateTo(fragmentId: Int, action: NavDirections) {
        lifecycleScope.launchWhenCreated {
            if (isAdded && isCurrentDestination(fragmentId)) {
                findNavController().navigate(action)
            }
        }
    }

    protected fun popFrom(fragmentId: Int) {
        lifecycleScope.launchWhenCreated {
            if (isAdded && isCurrentDestination(fragmentId)) {
                findNavController().popBackStack()
            }
        }
    }

    private fun isCurrentDestination(fragmentId: Int): Boolean {
        return findNavController().currentDestination?.id == fragmentId
    }
}