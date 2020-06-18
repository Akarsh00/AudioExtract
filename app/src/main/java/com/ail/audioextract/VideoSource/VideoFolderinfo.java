package com.ail.audioextract.VideoSource;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;

public class VideoFolderinfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public String folderName;

    public String folderPath;

    public String firstVideoPath;

    public  String fileCount;

    public  long fileSize;

    public  long last_modified;

    public  String bucket_id;

    public  String newTag ="";


    public String getCreatedDateFormat() {
        File file = new File(folderPath);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        return format1.format(file.lastModified());
    }

    @Override
    public String toString() {
        return "VideoFolderinfo{" +
                "folderName='" + folderName + '\'' +
                ", folderPath='" + folderPath + '\'' +
                ", firstVideoPath='" + firstVideoPath + '\'' +
                ", fileCount='" + fileCount + '\'' +
                ", fileSize=" + fileSize +
                ", last_modified=" + last_modified +
                ", bucket_id='" + bucket_id + '\'' +
                ", newTag='" + newTag + '\'' +
                '}';
    }
}
