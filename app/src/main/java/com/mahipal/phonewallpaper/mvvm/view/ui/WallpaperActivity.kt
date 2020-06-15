package com.mahipal.phonewallpaper.mvvm.view.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mahipal.phonewallpaper.R
import com.mahipal.phonewallpaper.mvvm.model.BaseResponse
import com.mahipal.phonewallpaper.mvvm.model.Photo
import com.mahipal.phonewallpaper.mvvm.view.adapter.WallpaperListAdapter
import com.mahipal.phonewallpaper.mvvm.view.listeners.PaginationListener
import com.mahipal.phonewallpaper.mvvm.viewModel.PhotoViewModel
import com.mahipal.phonewallpaper.rv_decoration.GridAutofitLayoutManager
import com.mahipal.phonewallpaper.rv_decoration.VerticalSpaceDecoration
import com.mahipal.phonewallpaper.utils.showMessage
import kotlinx.android.synthetic.main.activity_wallpaper.*
import android.app.Activity
import android.view.inputmethod.InputMethodManager

class WallpaperActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private var imageList = ArrayList<Photo>()
    private var photoViewModel: PhotoViewModel? = null
    private lateinit var wallpaperAdapter: WallpaperListAdapter
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var totalPage = 15
    private var isLoading = false
    var itemCount = 0
    private var queryText: String? = "People"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper)
//        window.setTitle(resources.getString(R.string.wallpaper_activity_name))

        photoViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(PhotoViewModel::class.java)

        initViewsAndListeners()
        loadImageList(currentPage, queryText)
        observeImageList()
    }

    private fun observeImageList() {
        photoViewModel?.mutablePhotoList?.observe(this,
            Observer<BaseResponse> { response ->

                runOnUiThread {
                    activity_progressBar.visibility = View.GONE
                    if (response.totalResults > 0) {

                        itemCount = if (response.photoList?.size ?: 0 > 0) {
                            imageList.addAll(response.photoList)
                            response.photoList.size
                        } else {
                            return@runOnUiThread
                        }

                        if (currentPage != PaginationListener.PAGE_START) wallpaperAdapter.removeLoading()

                        wallpaperAdapter.addItems(imageList, imageList.size, itemCount)
                        swipe_refresh.isRefreshing = false

                        if (currentPage < totalPage) {
                            wallpaperAdapter.addLoading()
                        } else {
                            isLastPage = true
                        }
                        isLoading = false
                    } else {
                        showMessage(this, "No Data found", 4)
                    }
                }
            })
    }

    private fun loadImageList(pageNo: Int, queryText: String?) {
        hideSoftKeyboard(search_view)
        photoViewModel?.searchImages(this, pageNo, queryText)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query?.length?:0 > 0) {
            queryText = query
            itemCount = 0
            currentPage = PaginationListener.PAGE_START
            isLastPage = false
            wallpaperAdapter.clear()
            loadImageList(currentPage, queryText)
        }
        return true
    }

    override fun onRefresh() {
        itemCount = 0
        currentPage = PaginationListener.PAGE_START
        isLastPage = false
        wallpaperAdapter.clear()
        loadImageList(currentPage, queryText)
    }

    private fun initViewsAndListeners() {
        swipe_refresh.setOnRefreshListener(this)
        search_view.setOnQueryTextListener(this)

        rv_wallpaper_list.setHasFixedSize(true)

        val columnWidth = resources.getDimension(R.dimen.column_width)
        val layoutManager = GridAutofitLayoutManager(this, columnWidth.toInt())
        rv_wallpaper_list.layoutManager = layoutManager
        val dimen = resources.getDimensionPixelSize(R.dimen.four_dp)
        rv_wallpaper_list.addItemDecoration(VerticalSpaceDecoration(dimen))

        activity_progressBar.visibility = View.VISIBLE

        wallpaperAdapter =
            WallpaperListAdapter(this, imageList)
        rv_wallpaper_list.adapter = wallpaperAdapter

        rv_wallpaper_list.addOnScrollListener(object : PaginationListener(layoutManager) {

            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                loadImageList(currentPage, queryText)
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

        })
    }

    fun hideSoftKeyboard(view: View) {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
