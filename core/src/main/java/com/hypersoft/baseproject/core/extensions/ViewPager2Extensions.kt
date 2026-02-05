package com.hypersoft.baseproject.core.extensions

import androidx.viewpager2.widget.ViewPager2

/**
 * Moves to the next page in ViewPager2
 * @return true if there are more pages, false if already on the last page
 */
fun ViewPager2.nextPage(): Boolean {
    val currentItem = currentItem
    val itemCount = adapter?.itemCount ?: 0

    return if (currentItem < itemCount - 1) {
        setCurrentItem(currentItem + 1, true)
        true
    } else {
        false
    }
}