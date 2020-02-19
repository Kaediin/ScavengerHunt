package com.example.mobileappdevelopment.DataUtils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

object LocUtils {
    fun resizeMapIcons(context: Context, iconName: String?): Bitmap {
        val imageBitmap = BitmapFactory.decodeResource(context.resources, context.resources.getIdentifier(iconName, "drawable", context.packageName))
        return Bitmap.createScaledBitmap(imageBitmap, 65, 110, false)
    }
}