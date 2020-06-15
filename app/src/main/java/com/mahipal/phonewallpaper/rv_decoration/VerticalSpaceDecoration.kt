package com.mahipal.phonewallpaper.rv_decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalSpaceDecoration(private val verticalSpaceHeight: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = verticalSpaceHeight
        outRect.right = verticalSpaceHeight
        outRect.bottom = verticalSpaceHeight
//        outRect.set(verticalSpaceHeight,verticalSpaceHeight,verticalSpaceHeight,verticalSpaceHeight)

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0 || parent.getChildLayoutPosition(view) == 1 ||
            parent.getChildLayoutPosition(view) == 2
        ) {
            outRect.top = verticalSpaceHeight
        } else {
            outRect.top = verticalSpaceHeight
        }
    }

}