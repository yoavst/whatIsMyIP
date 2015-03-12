package com.yoavst.whatismyip

import android.widget.LinearLayout
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import butterknife.bindView
import android.widget.TextView

/**
 * Created by yoavst.
 */
public class ItemView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    val titleView: TextView by bindView(R.id.title)
    val valueView: TextView by bindView(R.id.value)

    override fun onFinishInflate() {
        super.onFinishInflate()
        LayoutInflater.from(getContext()).inflate(R.layout.item, this, true)
    }

    fun setData(title: String, value: String) {
        titleView.setText(title)
        valueView.setText(value)
    }
}