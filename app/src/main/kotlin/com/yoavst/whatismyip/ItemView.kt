package com.yoavst.whatismyip

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.item.view.*
/**
 * Created by yoavst.
 */
public class ItemView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    override fun onFinishInflate() {
        super.onFinishInflate()
        LayoutInflater.from(getContext()).inflate(R.layout.item, this, true)
    }

    /**
     * Sets the data for the current item view.
     */
    fun setData(itemTitle: String, itemValue: String) {
        title.setText(itemTitle)
        value.setText(itemValue)
    }
}