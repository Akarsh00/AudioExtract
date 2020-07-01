package com.ail.audioextract.viewmodels

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ail.audioextract.RECENT_FOLDER_NAME
import com.ail.audioextract.VideoSource.VideoFileFilter
import com.ail.audioextract.VideoSource.VideoFolderinfo
import com.ail.audioextract.getCountOfVideo
import com.ail.rocksvideoediting.data.AudioModel
import java.io.File
import java.io.IOException

class AllAudiosViewModel : ViewModel() {


    private val PROJECTION_BUCKET: Array<String> = arrayOf<String>(
            MediaStore.Audio.AudioColumns.BUCKET_ID,
            MediaStore.Audio.AudioColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Audio.AudioColumns.DATE_MODIFIED,
            MediaStore.Audio.AudioColumns.DATA)


    private val selection = MediaStore.Audio.AudioColumns.BUCKET_ID + "=?"

    private val conetentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    private val BUCKET_GROUP_BY = "1) GROUP BY 1,(2"

    private val BUCKET_ORDER_BY = MediaStore.Audio.AudioColumns.BUCKET_DISPLAY_NAME + " ASC" //"MAX(datetaken) DESC";

    fun getAllAudioFromDevice(context: Context): MutableLiveData<MutableList<AudioModel>> {
        var tempAudioList: MutableList<AudioModel> = ArrayList();
        var allAudios: MutableLiveData<MutableList<AudioModel>> =MutableLiveData()

        var uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        var projection = arrayOf(MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.TITLE, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST);
        var c = context.contentResolver.query(uri, projection, MediaStore.Audio.Media.DATA + " like ? ", arrayOf("%utm%"), null);

        if (c != null) {
            while (c.moveToNext()) {

                var path = c.getString(0);   // Retrieve path.
                var name = c.getString(1);   // Retrieve name.
                var album = c.getString(2);  // Retrieve album name.
                var artist = c.getString(3); // Retrieve artist name.

                var audioModel = AudioModel(name, album, artist, path);


                Log.e("Name :" + name, " Album :" + album);
                Log.e("Path :" + path, " Artist :" + artist);

                // Add the model object to the list .
                tempAudioList.add(audioModel);
            }
            c.close();
        }

        // Return the list.
        allAudios.value=tempAudioList
        return allAudios
    }



    fun getInternalStorageDataWithCursor(mContext: Context)/*: MutableLiveData<List<VideoFolderinfo>> */{
        val data: MutableList<VideoFolderinfo> = java.util.ArrayList()
        var cursor: Cursor? = null
        try {
            cursor = mContext.contentResolver.query(conetentUri, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, null)
        } catch (e: SQLiteException) {
        } catch (e: IllegalArgumentException) {
        }
        if (cursor == null) {

//            allVideoBucket.postValue(data)
//            return allVideoBucket

        }
        val idBucketColNum = cursor!!.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.BUCKET_ID)
        val videoFolderName = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.BUCKET_DISPLAY_NAME)
        val videoData = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
        val dateModifiedColNum = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_MODIFIED)


        var recentVideosCount = 0
        var recentFirstImageUrl = ""

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                    fetchVideos(cursor, videoFolderName, idBucketColNum, dateModifiedColNum, videoData, data)
                        cursor.close()
                        data.forEach {
                            if (recentFirstImageUrl == "") {
                                recentFirstImageUrl = it.firstVideoPath
                            }
                            recentVideosCount += it.fileCount.toInt()

                        }

                        val commonFile = VideoFolderinfo()
                        commonFile.folderName = RECENT_FOLDER_NAME
                        commonFile.bucket_id = null
                        commonFile.folderPath = ""
                        commonFile.firstVideoPath = recentFirstImageUrl
                        commonFile.fileCount = recentVideosCount.toString()
                        data.add(0, commonFile)
                    }
        }

        //This is for recent


       data
    }

     fun fetchVideos(cursor: Cursor, videoFolderName: Int, idBucketColNum: Int, dateModifiedColNum: Int, videoData: Int, data: MutableList<VideoFolderinfo>) {
        do {
            val commonFile = VideoFolderinfo()
            commonFile.folderName = cursor.getString(videoFolderName)
            if (!TextUtils.isEmpty(commonFile.folderName) && commonFile.folderName.equals("0", ignoreCase = false)) {
                commonFile.folderName = "Internal storage"
            }
            commonFile.bucket_id = cursor.getString(idBucketColNum) //cursor.getLong(idBucketColNum);
            commonFile.last_modified = cursor.getLong(dateModifiedColNum)
            commonFile.folderPath = cursor.getString(videoData)
            commonFile.firstVideoPath = cursor.getString(videoData)
      //      commonFile.fileCount = cursor.getInt(cursor.getColumnIndex("count(1)")).toString() + ""

            if (commonFile.folderPath != null) {
                val file = File(commonFile.folderPath)
                commonFile.fileSize = file.length()
                try {
                    if (file != null && file.parentFile != null) {
                        commonFile.folderPath = file.parentFile.canonicalPath
                        val files = file.parentFile.listFiles(VideoFileFilter())
                        commonFile.fileCount = "" + getCountOfVideo(files)
                        //commonFile.folderPath = files[0].getPath();
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                }
            }
            if (!commonFile.fileCount.equals("0", ignoreCase = false)) {
                data.add(commonFile)
            }
        } while (cursor.moveToNext())
    }


}

