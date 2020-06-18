package com.ail.audioextract.accessmedia


import android.app.Application
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.ail.audioextract.VideoSource.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.*


class BaseMainViewModel(application: Application) : AndroidViewModel(application) {
    var allVideoBucket: MutableLiveData<List<VideoFolderinfo>> = MutableLiveData()
    var listAllVideos: MutableLiveData<List<VideoFileInfo>?>? = MutableLiveData()

    var context: Context = application


    private val PROJECTION_BUCKET: Array<String> = arrayOf<String>(
            MediaStore.Video.VideoColumns.BUCKET_ID,
            MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Video.VideoColumns.DATE_TAKEN,
            MediaStore.Video.VideoColumns.DATE_MODIFIED,
            MediaStore.Video.VideoColumns.DATA,
            "count(1)")


    val selection = MediaStore.Video.VideoColumns.BUCKET_ID + "=?"

    private val VIDEO_PROJECTION = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.BOOKMARK // "0 AS " + MediaStore.Images.ImageColumns.ORIENTATION,
    )


    private val conetentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    private val BUCKET_GROUP_BY = "1) GROUP BY 1,(2"

    private val BUCKET_ORDER_BY = MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME + " ASC" //"MAX(datetaken) DESC";


    public fun getInternalStorageDataWithCursor(mContext: Context): MutableLiveData<List<VideoFolderinfo>> {
        val data: MutableList<VideoFolderinfo> = ArrayList()
        var cursor: Cursor? = null
        try {
            //if (ThemeUtils.isGreaterThanEqualsOreo()){
            cursor = mContext.contentResolver.query(conetentUri, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, BUCKET_ORDER_BY)

            /* }else{
                cursor = mContext.getContentResolver().query(conetentUri, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);
            }*/
        } catch (e: SQLiteException) {
        } catch (e: IllegalArgumentException) {
        }
        if (cursor == null) {

            allVideoBucket?.postValue(data)
            return allVideoBucket

        }
        try {
            val idBucketColNum = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.BUCKET_ID)
            val videoFolderName = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME)
            val videoData = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA)
            val dateModifiedColNum = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_MODIFIED)


            val commonFile = VideoFolderinfo()
            commonFile.folderName = "All Videos"
            commonFile.bucket_id = null //cursor.getLong(idBucketColNum);
            commonFile.folderPath = ""
            commonFile.firstVideoPath = ""
            commonFile.fileCount = ""
            data.add(commonFile)


            if (cursor.moveToFirst()) {
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
                    commonFile.fileCount = cursor.getInt(cursor.getColumnIndex("count(1)")).toString() + ""
                    /* if (lastOpenTime > 0) {
                         if (commonFile.last_modified > lastOpenTime / 1000) {
                             commonFile.newTag = "New"
                         }
                     }*/
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
        } finally {
            cursor.close()
        }
        allVideoBucket?.postValue(data)
//        return data
        return allVideoBucket
    }

    private fun getCountOfVideo(files: Array<File>?): Long {
        var y = 0
        if (files != null) {
            for (x in files.indices) {
                if (files[x].length() > 0) {
                    y = y + 1
                }
            }
        }
        return y.toLong()
    }


    fun queryListVideosFromBucket(mContext: Context, mBucketId: Array<String>?): MutableLiveData<List<VideoFileInfo?>?> {
        var data: MutableLiveData<List<VideoFileInfo?>?> = MutableLiveData()
        CoroutineScope(Dispatchers.IO).launch {
            var x = query(
                    mContext,
                    mBucketId,
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    VIDEO_PROJECTION,
                    MediaStore.Video.Media.DATE_TAKEN,
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DATE_TAKEN,
                    MediaStore.Video.Media.DATE_MODIFIED,
                    MediaStore.Video.Media.MIME_TYPE)
            withContext(Dispatchers.Main) {
                data.postValue(x)
                if (mBucketId == null) {
                    listAllVideos?.postValue(x)
                }


            }
        }
        return data
    }

    private fun query(mContext: Context, mBucketId: Array<String>?, contentUri: Uri, projection: Array<String>, sortByCol: String,
                      idCol: String, fileName: String, dateTakenCol: String, dateModifiedCol: String, mimeTypeCol: String): List<VideoFileInfo> {
        val data: MutableList<VideoFileInfo> = LinkedList()
        val selectionMimeType = MediaStore.Video.Media.MIME_TYPE + " like ?"
        val selectionArgs = arrayOf("%video%")
        val cursor: Cursor
        cursor = (if (mBucketId != null) {
            mContext.contentResolver.query(contentUri, projection, selection, mBucketId, "$idCol DESC")
        } else {
            mContext.contentResolver.query(contentUri, projection, selectionMimeType, selectionArgs, "$idCol DESC")
        })!!
        if (cursor == null) {
            return data
        }
        try {
            val rowId = cursor.getColumnIndexOrThrow(idCol)
            val videoName = cursor.getColumnIndexOrThrow(fileName)
            val videoData = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val dateModifiedColNum = cursor.getColumnIndexOrThrow(dateModifiedCol)
            val durations = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val bookmark = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BOOKMARK)
            while (cursor.moveToNext()) {
                try {
                    val dateModified = cursor.getLong(dateModifiedColNum)
                    val commonFile = VideoFileInfo()
                    commonFile.row_ID = cursor.getLong(rowId)
                    commonFile.file_name = cursor.getString(videoName)
                    commonFile.file_path = cursor.getString(videoData)
                    commonFile.createdTime = dateModified
                    commonFile.lastPlayedDuration = cursor.getLong(bookmark)
                    commonFile.duration = cursor.getLong(durations).toString()
                    commonFile.isDirectory = false
                    commonFile.setFindDuplicate(false)
                    if (commonFile.file_path != null && !commonFile.file_path.equals("")) {
                        commonFile.fileInfo = FileSpecUtils.getInfo(File(commonFile.file_path), cursor.getLong(durations), 1)
                    }
                    val file = File(commonFile.file_path)
                    if (file.length() > 0) {
                        commonFile.increment()
                        data.add(commonFile)
                    }
                } catch (e: java.lang.Exception) {
                }
            }
        } finally {
            cursor.close()
        }
        if (mBucketId == null) {
            val listOfHidenFiles = RootHelper.getHidenVideoFileIfExistWithVideo(context)
            if (listOfHidenFiles != null) {
                data.addAll(listOfHidenFiles)
            }
        }
        return data
    }

}