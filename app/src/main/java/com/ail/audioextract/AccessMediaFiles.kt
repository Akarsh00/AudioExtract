package com.ail.audioextract

import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.text.format.DateUtils
import android.widget.Toast
import com.ail.audioextract.VideoSource.FileSpecUtils
import com.ail.audioextract.VideoSource.VideoFileInfo
import java.io.File


/**
 * SD CARD QUERIES
 */

fun getAllSdCardTrack_Beans(context: Context): List<Track_Bean>? {
    val Track_Beans: ArrayList<Track_Bean> = ArrayList<Track_Bean>()
    val c: Cursor? = context
            .contentResolver
            .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.ALBUM_ID), "1=1",
                    null, null)
    if (c != null) {
        if (c.moveToFirst()) {
            do {
                val mTcBean = Track_Bean()
                val mTitle: String = c
                        .getString(c
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
//                if (mTitle.trim { it <= ' ' }.endsWith(".mp3") || mTitle.trim { it <= ' ' }.endsWith(".MP3")) {
                mTcBean.mTitle = mTitle
                  /*        val mAlbumName: String = c
                        .getString(c
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
*/
                //Path
                val mPath: String = c
                        .getString(c
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                mTcBean.mPath = mPath

                println("PATH$mPath")
                //Duration
                val mDuration: Long = c
                        .getLong(c
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val mFormattedDuration = DateUtils
                        .formatElapsedTime(mDuration / 1000)
                mTcBean.mDuration = mFormattedDuration

//                    mTcBean.setmDuration(mFormattedDuration)

                if (mTcBean.mPath.contains("Rocks Audio Saved")) {
                    Track_Beans.add(mTcBean)
                }
                /*  } else {
                      println("The Track ext is not Mp3::$mTitle")
                  }*/
            } while (c.moveToNext())
            c.close()
        }
    }
    System.out.println("Size of array::" + Track_Beans.size)
    return Track_Beans
}

data class Track_Bean(var mTitle: String = "", var mPath: String = "", var mDuration: String = "")


fun findFileSizeFromPath(path: String): String {
    var file = File(path)
    if (file.exists()) {

        val fileSizeInBytes = file.length()
        val fileSizeInKB = fileSizeInBytes / 1024
        val fileSizeInMB = fileSizeInKB / 1024
        if (fileSizeInMB > 1) {
            return fileSizeInMB.toString() + " mB"

        } else {
            return fileSizeInKB.toString() + " kB"

        }

    } else {
        return ""
    }
}

