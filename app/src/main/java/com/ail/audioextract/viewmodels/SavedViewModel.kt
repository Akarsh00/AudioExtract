package com.ail.audioextract.viewmodels

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ail.audioextract.VideoSource.FileSpecUtils
import com.ail.audioextract.VideoSource.VideoFileInfo
import java.io.File
import java.util.*

class SavedViewModel(val context: Context) : ViewModel() {

    var listOfVideos: MutableLiveData<List<VideoFileInfo>> = MutableLiveData()


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


    fun getAllVideosFromFolder(folderName: String) {
        val videoItemHashSet: HashSet<String> = HashSet()
        val data: MutableList<VideoFileInfo> = LinkedList()
        val projection = VIDEO_PROJECTION
        val condition = MediaStore.Video.Media.DATA + " like?"
        val selectionArguments = arrayOf("%$folderName%")
        val cursor: Cursor? = context.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, condition, selectionArguments, null)
        if (cursor != null) {

            try {
                cursor.moveToFirst()
            do {
                videoItemHashSet.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)))
                val rowId = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val videoName = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val videoData = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val dateModifiedColNum = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
                val durations = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val bookmark = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BOOKMARK)


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
            } while (cursor.moveToNext())
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
        listOfVideos.postValue(data)
    }


}