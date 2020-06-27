package com.ail.audioextract

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.StrictMode
import android.provider.MediaStore
import java.io.File


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
    val builder = StrictMode.VmPolicy.Builder()
    StrictMode.setVmPolicy(builder.build())
    context.startActivity(Intent.createChooser(intent, "Share Audio ..."))
}

fun setRingtone(context: Context, path: String?) {
    var ringFile = File(path)
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
                y += 1
            }
        }
    }
    return y.toLong()
}
