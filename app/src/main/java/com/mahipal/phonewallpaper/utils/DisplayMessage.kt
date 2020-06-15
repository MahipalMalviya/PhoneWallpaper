package com.mahipal.phonewallpaper.utils

import android.app.Activity
import android.content.Context
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

fun showMessage(context:Context,message:String,margin:Int) {
    val snackBar = Snackbar.make((context as Activity).findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
    val params = snackBar.view.layoutParams as FrameLayout.LayoutParams
    params.setMargins(params.leftMargin + margin,params.topMargin,params.rightMargin + margin, params.bottomMargin + margin)
    snackBar.view.layoutParams = params
    snackBar.animationMode = Snackbar.ANIMATION_MODE_FADE
    snackBar.show()
}