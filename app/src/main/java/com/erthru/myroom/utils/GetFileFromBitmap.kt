package com.erthru.myroom.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class GetFileFromBitmap{

    fun getFileFromBitmap(bitmap: Bitmap,context: Context) : File? {
        val cache = context.externalCacheDir
        val shareFile = File(cache, "myFile.jpg")
        Log.d("share file type is", shareFile.absolutePath)
        try {
            val out = FileOutputStream(shareFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            return shareFile
        } catch (e: Exception) {

        }
        return null
    }

}