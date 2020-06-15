package com.mahipal.phonewallpaper.mvvm.view.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {

    private var currentPosition = 0

    open fun bindItem(position: Int) {
        currentPosition = position
        clear()
    }

    fun getCurrentPosition(): Int {
        return currentPosition
    }

    protected abstract fun clear()
}