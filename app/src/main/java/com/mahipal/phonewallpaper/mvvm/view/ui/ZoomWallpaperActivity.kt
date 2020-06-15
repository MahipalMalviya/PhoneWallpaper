package com.mahipal.phonewallpaper.mvvm.view.ui

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.Coil
import coil.api.load
import coil.request.GetRequest
import coil.request.LoadRequest
import com.mahipal.phonewallpaper.R
import com.mahipal.phonewallpaper.mvvm.model.Photo
import kotlinx.android.synthetic.main.activity_zoom_wallpaper.*
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class ZoomWallpaperActivity: AppCompatActivity(), View.OnClickListener {

    private var photo: Photo? = null
    private var rotateFab = false

    companion object {
        private val PERMISSION_LIST = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

        private const val REQUEST_CODE_PERMISSION = 101
        private const val PHOTO_DETAILS = "Photo_Details"
        const val APP_FOLDER_NAME = "/storage/emulated/0/PhoneWallpapers/Download/"

        fun launch(context:Context,photoDetails:Photo) {
            val intent = Intent(context,ZoomWallpaperActivity::class.java)
            intent.putExtra(PHOTO_DETAILS,photoDetails)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(R.layout.activity_zoom_wallpaper)

        photo = intent.getSerializableExtra(PHOTO_DETAILS) as Photo

        initViews()
    }

    private fun initViews() {
        rl_set_wallpaper.setOnClickListener(this)
        rl_preview_wallpaper.setOnClickListener(this)
        rl_expand_button.setOnClickListener(this)
        rl_set_lock_screen.setOnClickListener(this)

        photo?.let { photo ->
            GlobalScope.launch(Dispatchers.Main) {
                val drawable = photo.imageSrc?.large2x?.let {
                    getImageFromUrl(it)
                }

                iv_zoom_wallpaper.setImageDrawable(drawable)

                drawable?.toBitmap()?.let {
                    Palette.from(it)
                        .generate { palette ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                palette?.getLightVibrantColor(getColor(R.color.black_transparent))?.let { it1 ->
                                    cl_root.setBackgroundColor(
                                        it1
                                    )
                                }
                            }
                        }
                }
            }
//            setImageBitmap(iv_zoom_wallpaper,photo.imageSrc?.large2x)
        }
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.rl_set_wallpaper -> {

                if (!isAppPermissionGranted()) {
                    setImageWallpaper(false)
                } else {
                    isAppPermissionGranted()
                }
            }

            R.id.rl_set_lock_screen -> {
                if (!isAppPermissionGranted()) {
                    setImageWallpaper(true)
                } else {
                    isAppPermissionGranted()
                }
            }

            R.id.rl_preview_wallpaper -> {
                val dialog = Dialog(this,R.style.WallpaperTheme)
                dialog.setContentView(R.layout.dialog_zoom_image)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.statusBarColor = resources.getColor(android.R.color.transparent,theme)
                }
                window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

                val imageView = dialog.findViewById(R.id.iv_zoom_wallpaper) as ImageView

                GlobalScope.launch(Dispatchers.Main) {
                    val drawable = photo?.imageSrc?.large2x?.let {
                        getImageFromUrl(it)
                    }
                    try {
                        imageView.setImageDrawable(drawable)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                dialog.show()
            }

            R.id.rl_expand_button -> {
                rotateFab = rotateFab(view,!rotateFab)
                if (rotateFab) {
                    extraFabVisible(rotateFab)
                } else {
                    extraFabVisible(rotateFab)
                }
            }
        }
    }

    private fun extraFabVisible(isVisible:Boolean) {
        if (isVisible) {
            rl_set_wallpaper.visibility = View.VISIBLE
            rl_set_lock_screen.visibility = View.VISIBLE
            tv_set_wallpaper.visibility = View.VISIBLE
            tv_set_lock_screen.visibility = View.VISIBLE
        } else {
            rl_set_wallpaper.visibility = View.GONE
            rl_set_lock_screen.visibility = View.GONE
            tv_set_wallpaper.visibility = View.GONE
            tv_set_lock_screen.visibility = View.GONE
        }
    }

    private fun rotateFab(view:View,rotate:Boolean): Boolean {
        val rotation = if (rotate)
            135f
        else
            0f
        view.animate().setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                }
            })
            .rotation(rotation)
        return rotate
    }

    private fun setImageWallpaper(isLockScreen:Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
            val drawable = photo?.imageSrc?.large2x?.let {
                getImageFromUrl(it)
            }

            try {
                if (isLockScreen && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    getWallPaperManager().setBitmap(drawable?.toBitmap(),null,true,WallpaperManager.FLAG_LOCK)
                } else {
                    val imagePath = saveToInternalStorage(drawable)
                    val uri = getImageContentUri(imagePath)

                    val intent = getWallPaperManager().getCropAndSetWallpaperIntent(uri)
                    startActivity(intent)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun getImageContentUri(imagePath: String): Uri? {
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media._ID),
            MediaStore.Images.Media.DATA + "=? ", arrayOf(imagePath),null)

        cursor?.let { c ->
            if (c.moveToFirst()) {
                val id = c.getInt(c.getColumnIndex(MediaStore.MediaColumns._ID))
                val baseUri = Uri.parse("content://media/external/images/media")
                c.close()
                return Uri.withAppendedPath(baseUri, "" + id)
            }
            c.close()
        }.run {
            if (File(imagePath).exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA,imagePath)
                return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
            }
        }

        return null
    }

    private fun isAppPermissionGranted(): Boolean {
        val listPermissionNeeded = ArrayList<String>()
        for (permission in PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(permission)
            }
        }

        //ask for non-permission granted
        if (listPermissionNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this,
                listPermissionNeeded.toTypedArray(), REQUEST_CODE_PERMISSION)
        }

        return listPermissionNeeded.size > 0
    }

    private fun saveToInternalStorage(drawable: Drawable?): String {
        val directory = File(APP_FOLDER_NAME)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory.absolutePath,"${photo?.id}.jpeg")

        var fos:FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            drawable?.toBitmap()?.compress(Bitmap.CompressFormat.JPEG,100,fos)
        } catch (ex:Exception) {
            ex.printStackTrace()
        } finally {
            try {
                fos?.flush()
                fos?.close()
            }catch (ie:IOException) {
                ie.printStackTrace()
            }
        }
        return file.absolutePath
    }

    private fun getWallPaperManager(): WallpaperManager {
        return WallpaperManager.getInstance(this)
    }

    private suspend fun getImageFromUrl(url:String):Drawable? {
        val imageLoader = Coil.imageLoader(this)
        val request = GetRequest.Builder(this)
            .data(url)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .build()

        return imageLoader.execute(request).drawable
    }

    private fun setImageBitmap(imageView: ImageView,url:String?) {
        val imageLoader = Coil.imageLoader(this)
        val request = LoadRequest.Builder(this)
            .data(url)
            .target(
                onStart = {
                    imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    imageView.setImageDrawable(resources.getDrawable(R.drawable.ic_image_holder,theme))
                },
                onSuccess = {
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    imageView.setImageDrawable(it)
                },
                onError = {
                    imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    imageView.setImageDrawable(resources.getDrawable(R.drawable.ic_image_holder,theme))
                }
            )
            .build()
        imageLoader.execute(request)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSION) {

            var deniedCount = 0
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    deniedCount++
                }
            }

            if (deniedCount > 0) {
                isAppPermissionGranted()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}