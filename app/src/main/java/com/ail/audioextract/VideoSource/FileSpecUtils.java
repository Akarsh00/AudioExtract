package com.ail.audioextract.VideoSource;

import android.media.MediaMetadataRetriever;
import android.net.Uri;

import java.io.File;
import java.net.URLConnection;

public class FileSpecUtils {


    public static BaseFile.FileInfo getInfo(File file, int fileType) {

        BaseFile.FileInfo fileInfo = null;

        try {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(String.valueOf(Uri.parse(file.getPath())));
            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
            String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            fileInfo = new BaseFile.FileInfo(Integer.parseInt(width),Integer.parseInt(height),file.length(),Long.parseLong(durationStr),Long.parseLong(bitrate),fileType);
            retriever.release();
        } catch (Exception e) {
            fileInfo = new BaseFile.FileInfo(0,0,file.length(),0,0,fileType);
        }

        return fileInfo;
    }

    public static BaseFile.FileInfo getInfo(File file, long duration , int fileType) {

        BaseFile.FileInfo fileInfo = null;
        try {
            fileInfo = new BaseFile.FileInfo(0,0,file.length(), duration ,0,fileType);

        } catch (Exception e) {
            fileInfo = new BaseFile.FileInfo(0,0,file.length(),0,0,fileType);
        }

        return fileInfo;
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isImageFileFilterUsingMimeType(String mimeType) {

        return mimeType != null && mimeType.startsWith("image");
    }
}
