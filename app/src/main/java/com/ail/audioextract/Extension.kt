package idv.luchafang.videotrimmerexample

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.widget.TextView
import androidx.core.net.toUri
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


fun File.getVideoFileDuration(context: Context): Long =
        MediaPlayer.create(context, Uri.parse(absolutePath))
                .duration.toLong()


fun TextView.setTimeVideo(position: Int) {
    val seconds = "" /*sec*/
    this.text = java.lang.String.format("%s %s", position.getTimeStringFromInt(), seconds)
}

fun TextView.setTime(position: Int) {
    val seconds = "sec"
    this.text = java.lang.String.format("%s %s", position.getTimeStringFromInt(), seconds)
}

private fun Context.dpToPx(dp: Float): Float = dp * this.resources.displayMetrics.density


fun Int.getTimeStringFromInt(): String {
    val totalSeconds = this / 1000
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60 % 60
    val hours = totalSeconds / 3600
    val mFormatter = Formatter()
//    return if (hours > 0) {
    return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
    /*  } else {
          mFormatter.format("%02d:%02d", minutes, seconds).toString()
      }*/
}

fun getVideoFilePath(): String? {
    return getAndroidMoviesFolder()?.absolutePath + "/" + SimpleDateFormat("yyyyMMdd-HHmmss")
            .format(Date()) + ".mp4"
}

fun getAndroidMoviesFolder(): File? {
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
}

fun String.uriExtractorFromPath() = Uri.Builder().path(this).build()


suspend fun File.getMediaDuration(context: Context): String {

    val duration =
            MediaPlayer.create(context, this.absolutePath.toUri())
                    .duration.toLong()
    var x = duration
    var lengthOfVideo = String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(x),
            TimeUnit.MILLISECONDS.toSeconds(x) -
                    TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(
                                    x
                            )
                    ))

    return lengthOfVideo
}

fun Int.getFile_duration_inDetail(): String? {
    var duration: Long = this.toLong()
    if (duration < 1) {
        return ""
    }
    duration = (duration / 1000.0f).toLong() //seconds
    val sec = duration % 60
    val minutes = duration % 3600 / 60
    val hours = duration % 86400 / 3600
    if (hours > 0) {

        // return hours +" hrs "+minutes+ " min "+sec +" sec";
        if (sec < 10) {
            return if (minutes < 10) {
                "$hours:0$minutes:0$sec"
            } else {
                "$hours:$minutes:0$sec"
            }
        } else if (sec > 9) {
            return if (minutes < 10) {
                "$hours:0$minutes:$sec"
            } else {
                "$hours:$minutes:$sec"
            }
        }
        return "$hours:$minutes:$sec"
    } else if (minutes > 0) {
        // return minutes+ " min "+sec +" sec";
        return if (sec < 10) {
            "$minutes:0$sec"
        } else "$minutes:$sec"
    } else if (sec > 0) {
        // return sec +" sec";
        return if (sec < 10) {
            "0:0$sec"
        } else "0:$sec"
    }
    return ""
}

fun playAudioIntent(context: Context, item: String) {
    val name: String = item
    val a = Uri.parse(name)

    val viewMediaIntent = Intent()
    viewMediaIntent.action = Intent.ACTION_VIEW
    viewMediaIntent.setDataAndType(a, "audio/*")
    viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    context.startActivity(Intent.createChooser(viewMediaIntent, "Open with..."))
}

fun shareAudioIntent(context: Context, item: String) {
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(item)))
    intent.type = "audio/*"
    val builder = VmPolicy.Builder()
    StrictMode.setVmPolicy(builder.build())
    context.startActivity(Intent.createChooser(intent, "Share Audio ..."))
}

 fun setRingtone(context: Context, path: String?) {
     var ringFile=File(path)
     val values = ContentValues()
     values.put(MediaStore.MediaColumns.DATA, ringFile.absolutePath)
     values.put(MediaStore.MediaColumns.TITLE, "ring")
     values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
     values.put(MediaStore.MediaColumns.SIZE, ringFile.length())
     values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
     values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true)
     values.put(MediaStore.Audio.Media.IS_ALARM, true)
     values.put(MediaStore.Audio.Media.IS_MUSIC, false)

     val uri = MediaStore.Audio.Media.getContentUriForPath(ringFile.absolutePath)
     val newUri: Uri? = context.contentResolver.insert(uri, values)

     try {
         RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri)
     } catch (t: Throwable) {
     }

 }

fun getCountOfVideo(files: Array<File>?): Long {
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
