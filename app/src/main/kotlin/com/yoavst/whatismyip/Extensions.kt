package com.yoavst.whatismyip

import android.view.View

fun View.show() {
    if (!this.isShown)
        this.visibility = View.VISIBLE
}

fun View.hide() {
    if (this.isShown)
        this.visibility = View.GONE
}