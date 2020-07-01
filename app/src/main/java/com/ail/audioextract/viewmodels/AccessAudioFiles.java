package com.ail.audioextract.viewmodels;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.ail.rocksvideoediting.data.AudioModel;

import java.util.ArrayList;
import java.util.List;

public class AccessAudioFiles {


    public static List<AudioModel> getAllAudioFromDevice(final Context context) {
        final List<AudioModel> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.TITLE, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
        Cursor c = context.getContentResolver().query(uri, projection, /*MediaStore.Audio.Media.DATA + " like ? "*/null, null/*new String[]{"%utm%"}*/, null);

        if (c != null) {
            while (c.moveToNext()) {
                // Create a model object.

                String path = c.getString(0);   // Retrieve path.
                String name = c.getString(1);   // Retrieve name.
                String album = c.getString(2);  // Retrieve album name.
                String artist = c.getString(3); // Retrieve artist name.

                AudioModel audioModel = new AudioModel(name, album, artist, path);
                tempAudioList.add(audioModel);
            }
            c.close();
        }

        // Return the list.
        return tempAudioList;
    }

    public static ArrayList<String> getData(Context context) {
        ArrayList<String> list = new ArrayList<>();
        String[] Projection = new String[]{
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM
                , MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.SIZE};

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(1);
                list.add(path);

            }
        }
        return list;
    }
}

