package com.mahipal.phonewallpaper.mvvm.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.request.LoadRequest.Companion.Builder
import coil.transform.RoundedCornersTransformation
import com.mahipal.phonewallpaper.R
import com.mahipal.phonewallpaper.mvvm.model.Photo
import com.mahipal.phonewallpaper.mvvm.view.adapter.viewholder.BaseViewHolder
import com.mahipal.phonewallpaper.mvvm.view.ui.ZoomWallpaperActivity
import kotlinx.android.synthetic.main.layout_wallpaper_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WallpaperListAdapter(private val context: Context, photoList: ArrayList<Photo>) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var list: MutableList<Photo>? = null

    init {
        list = photoList.asIterable().toMutableList()
    }

    private var isLoaderVisible = false

    companion object {
        const val VIEW_TYPE_LOADING = 0
        const val VIEW_TYPE_NORMAL = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            VIEW_TYPE_LOADING ->
                return ProgressbarHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_loading,
                        parent,
                        false
                    )
                )

            VIEW_TYPE_NORMAL ->
                return MyViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_wallpaper_item,
                        parent,
                        false
                    )
                )

            else ->
                return MyViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_wallpaper_item,
                        parent,
                        false
                    )
                )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == list?.size?:0 - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return list?.size?: 0
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bindItem(position)
    }

    inner class MyViewHolder(itemView: View) : BaseViewHolder(itemView) {

        override fun clear() {

        }

        override fun bindItem(position: Int) {
            super.bindItem(position)

            val photoItem = list?.get(position)
            itemView.iv_item_wallpaper.setImageBitmap(null)
            loadUrlIntoImageView(photoItem?.imageSrc?.portrait,itemView.iv_item_wallpaper)

            itemView.setOnClickListener {
                photoItem?.let {
                    ZoomWallpaperActivity.launch(context,it)
                }
            }
        }
    }

    inner class ProgressbarHolder(itemView: View) : BaseViewHolder(itemView) {

        override fun clear() {

        }
    }

    fun loadUrlIntoImageView(url: String?, imageView: ImageView) {
        val imageLoader = Coil.imageLoader(context)
        val request = Builder(context)
            .data(url)
            .target(
                onStart = {
                    imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    imageView.setImageDrawable(context.getDrawable(R.drawable.ic_image_holder))
                },
                onSuccess = {
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    imageView.setImageDrawable(it)
                },
                onError = {
                    imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    imageView.setImageDrawable(context.getDrawable(R.drawable.ic_image_holder))
                }
            )
            .build()

        imageLoader.execute(request)

    }

    fun addItems(imageList: ArrayList<Photo>, startPos: Int, totalCount: Int) {
        list = imageList
        notifyItemRangeInserted(startPos,totalCount)
    }

    fun removeLoading() {
        isLoaderVisible = false
        notifyItemInserted(list?.size?: 0 - 1)
    }

    fun addLoading() {
        isLoaderVisible = true
        notifyItemInserted(list?.size?:0 - 1)
    }

    fun clear() {
        list?.clear()
        notifyDataSetChanged()
    }

}