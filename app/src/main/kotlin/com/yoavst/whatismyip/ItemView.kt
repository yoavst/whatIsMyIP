package com.yoavst.whatismyip

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.item.view.*


/**
 * Created by yoavst.
 */
class ItemView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    override fun onFinishInflate() {
        super.onFinishInflate()
        inflate(context, R.layout.item, this)
    }

    /**
     * Sets the data for the current item view.
     */
    fun setData(itemTitle: String, itemValue: String) {
        title.text = itemTitle
        value.text = itemValue
    }
}