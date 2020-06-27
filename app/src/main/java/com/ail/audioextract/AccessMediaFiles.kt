package com.ail.audioextract

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.text.format.DateUtils
import java.io.File


/**
 * SD CARD QUERIES
 */

fun getAllSdCardTrackBeans(context: Context): List<AudioTrackBean>? {
    val Track_Beans: ArrayList<AudioTrackBean> = ArrayList()
    val c: Cursor? = context
            .contentResolver
            .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.ALBUM_ID),
                    MediaStore.Audio.Media.DATA + " like ? ", arrayOf("%$SAVED_EDITED_MEDIA%"), null)

    if (c != null) {
        if (c.moveToFirst()) {
            do {
                val mTcBean = AudioTrackBean()
                val mTitle: String = c
                        .getString(c
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                mTcBean.mTitle = mTitle
                val mPath: String = c
                        .getString(c
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                mTcBean.mPath = mPath
                val mDuration: Long = c
                        .getLong(c
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val mFormattedDuration = DateUtils
                        .formatElapsedTime(mDuration / 1000)
                mTcBean.mDuration = mFormattedDuration
                Track_Beans.add(mTcBean)
            } while (c.moveToNext())
            c.close()
        }
    }



    return Track_Beans
}

data class AudioTrackBean(var mTitle: String = "", var mPath: String = "", var mDuration: String = "")


fun findFileSizeFromPath(path: String): String {
    val file = File(path)
    return if (file.exists()) {
        val fileSizeInBytes = file.length()
        val fileSizeInKB = fileSizeInBytes / 1024
        val fileSizeInMB = fileSizeInKB / 1024
        if (fileSizeInMB > 1) {
            "$fileSizeInMB mB"
        } else {
            "$fileSizeInKB kB"
        }

    } else {
        ""
    }
}

