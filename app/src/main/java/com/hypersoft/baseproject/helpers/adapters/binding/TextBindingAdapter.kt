package com.hypersoft.baseproject.helpers.adapters.binding

import android.graphics.Paint
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.hypersoft.baseproject.R

/**
 * @param: dTextColor -> Set attribute color for this
 *  Syntax:
 *      xml     ->   app:dTextColor="@{item.isSelected}"
 */
@BindingAdapter("dTextColor")
fun TextView.setDTextColor(isItemSelected:Boolean) {
//    if (isItemSelected){
//        val typedArray = context.theme.obtainStyledAttributes(intArrayOf(R.attr.colorFromAttribute))
//        val textColor = typedArray.getColor(0, 0)
//        typedArray.recycle()
//        this.setTextColor(textColor)
//    }else{
//        this.setTextColor(ContextCompat.getColor(context, R.color.normalColor))
//    }



    /**
     * @param: strikeThrough -> to draw a line on text
     *  Syntax:
     *      xml     ->   android:text="This is my Text"
     *      xml     ->   app:strikeThroughText="This is my Text"
     */

    @BindingAdapter("strikeThroughText")
    fun TextView.strikeThroughText(strikeThrough: Boolean = true) {
        if (strikeThrough) {
            this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            this.paintFlags = this.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }
}
