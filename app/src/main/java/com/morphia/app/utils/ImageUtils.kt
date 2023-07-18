package com.morphia.app.utils

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {

    fun saveBitmapToCache(activity: Activity, bitmap: Bitmap): Uri? {

        var bmpUri: Uri? = null
        try {
            val file =

                File(
                    activity.cacheDir,
                    "Img_" + System.currentTimeMillis() + ".jpg"
                )
            if (file.exists()) {
                file.delete()
            }
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
            bmpUri = FileProvider.getUriForFile(
                activity,
                Constant.AUTHORITY,
                file
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }


}