package com.ail.audioextract.VideoSource;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RootHelper {
    public final static String[] videoacceptedExtensions = {"mp4","mp4v", "avi", "asf","avchd","dav", "arf", "ts", "mov", "qt","trc", "dv4", "dv4"
            , "mpg","mpeg", "mpeg4","webm","ogv","vp9","vob", "3gp", "riff", "m2ts", "m3u", "avc", "mkv", "wav", "flv", "wmv", "divx","swf"};


    public final static String[] imageacceptedExtensions = {"jpg","jpeg", "png"};

    public static List<VideoFileInfo> getVideoFilesListFromFolder(String path, int fileType, boolean includeSubDirectory, boolean showHidden, boolean getDuplicate) {
        File f = new File(path);
        List<VideoFileInfo> files = new LinkedList<>();
        try {
            if (f.exists()) {
                for (File file : f.listFiles()) {

                    if (!file.getName().startsWith(".") && checkFileisVideo(file)) {

                        VideoFileInfo commonFile = new VideoFileInfo();
                        commonFile.file_name = file.getName();
                        commonFile.file_path = file.getPath();
                        commonFile.createdTime  = file.lastModified();
                        commonFile.isDirectory = file.isDirectory();
                        commonFile.setFindDuplicate(getDuplicate);
                        commonFile.setFileInfo(FileSpecUtils.getInfo(file, fileType));
                        commonFile.increment();
                        if (!file.isHidden() || showHidden) {
                            files.add(commonFile);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    private static boolean checkFileisVideo(File file) {
        String[] acceptedExtensions = ConstantFileFilters.videoacceptedExtensions;
        for (int _i = 0; _i < acceptedExtensions.length; _i++) {
            if (file.getName().endsWith("." + acceptedExtensions[_i])) {
                return true;
            }
        }
        return false;
    }



    public static List<VideoFileInfo> getVideoFilesList(String path, int fileType, boolean includeSubDirectory, boolean showHidden, boolean getDuplicate) {
        if (path==null || "".equals(path.trim())){
            return null;
        }
        File f = new File(path);
        List<VideoFileInfo> files = new LinkedList<>();
        try {
            if (f!=null && f.exists()) {
                File [] filesArray = f.listFiles();
                if (filesArray == null){
                    return files;
                }
                for (File file : f.listFiles()) {

                    if (file!=null && file.isDirectory() && !file.isHidden() && includeSubDirectory) {

                        int k = file.getAbsolutePath().lastIndexOf("/Android/data");
                        if (k !=-1) {
                            continue;
                        }
                        if (file.getName().startsWith("com.")){
                            continue;
                        }

                        files.addAll(getVideoFilesList(file.getPath(), fileType, includeSubDirectory, showHidden, getDuplicate));
                    } else {


                        if (null != file && !file.getName().startsWith(".") && checkFileisVideo(file)) {

                            VideoFileInfo commonFile = new VideoFileInfo();
                            commonFile.file_name = file.getName();
                            commonFile.file_path = file.getPath();
                            commonFile.createdTime  = file.lastModified();
                            commonFile.isDirectory = file.isDirectory();
                            commonFile.setFindDuplicate(getDuplicate);
                            commonFile.setFileInfo(FileSpecUtils.getInfo(file, fileType));
                            commonFile.increment();
                            if (!file.isHidden() || showHidden) {
                                if (file.length()>0) {
                                    files.add(commonFile);
                                }
                            }
                        }


                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }



    public static ArrayList<VideoFolderinfo> getHidenVideoFolderIfExistWithVideo(Context context) {
        final String FILE_TYPE_NO_MEDIA = ".nomedia";

        ArrayList<VideoFolderinfo> videoFolderinfoArrayList = new ArrayList<>();
        // Scan all no Media files
        String nonMediaCondition = MediaStore.Files.FileColumns.MEDIA_TYPE
                + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;

        // Files with name contain .nomedia
        String where = nonMediaCondition + " AND "
                + MediaStore.Files.FileColumns.TITLE + " LIKE ?";

        String[] params = new String[] { "%" + FILE_TYPE_NO_MEDIA + "%" };

        // make query for non media files with file title contain ".nomedia" as
        // text on External Media URI
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                new String[] { MediaStore.Files.FileColumns.DATA }, where,
                params, null);

        while (cursor.moveToNext()) {
            String hiddenFilePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            if (hiddenFilePath != null) {
                File file = new File(hiddenFilePath);
                if (file.exists()){
                    File parentFile = file.getParentFile();
                    if (parentFile.isDirectory()) {
                        File [] videoFileArray =    parentFile.listFiles(new VideoFileFilter());
                        if (videoFileArray != null && videoFileArray.length>0) {
                            VideoFolderinfo videoFolderinfo = new VideoFolderinfo();
                            videoFolderinfo.fileCount = "" + videoFileArray.length;
                            videoFolderinfo.folderPath = parentFile.getAbsolutePath();
                            videoFolderinfo.fileSize = parentFile.length();
                            videoFolderinfo.folderName = parentFile.getName();
                            videoFolderinfo.last_modified = parentFile.lastModified();
                            videoFolderinfoArrayList.add( videoFolderinfo);
                        }
                    }
                }
            }
        }
        return  videoFolderinfoArrayList;
    }



    public static List<VideoFileInfo>  getHidenVideoFileIfExistWithVideo(Context context) {
        final String FILE_TYPE_NO_MEDIA = ".nomedia";

        LinkedList<VideoFileInfo> videoFolderinfoArrayList = new LinkedList<>();
        // Scan all no Media files
        String nonMediaCondition = MediaStore.Files.FileColumns.MEDIA_TYPE
                + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;

        // Files with name contain .nomedia
        String where = nonMediaCondition + " AND "
                + MediaStore.Files.FileColumns.TITLE + " LIKE ?";

        String[] params = new String[] { "%" + FILE_TYPE_NO_MEDIA + "%" };

        // make query for non media files with file title contain ".nomedia" as
        // text on External Media URI
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                new String[] { MediaStore.Files.FileColumns.DATA }, where,
                params, null);

        while (cursor.moveToNext()) {
            String hiddenFilePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            if (hiddenFilePath != null) {
                File hidenFile = new File(hiddenFilePath);
                if (hidenFile.exists()){
                    File parentFile = hidenFile.getParentFile();
                    if (parentFile.isDirectory()) {
                        File[] videoFileArray = parentFile.listFiles(new VideoFileFilter());
                        if (videoFileArray != null && videoFileArray.length > 0) {
                            for (File file : videoFileArray) {
                                VideoFileInfo commonFile = new VideoFileInfo();
                                commonFile.file_name = file.getName();
                                commonFile.file_path = file.getPath();
                                commonFile.createdTime = file.lastModified();
                                commonFile.isDirectory = file.isDirectory();
                                commonFile.setFindDuplicate(false);
                                commonFile.setFileInfo(FileSpecUtils.getInfo(file, 0));
                                commonFile.increment();
                                videoFolderinfoArrayList.add(commonFile);
                            }
                        }
                    }
                }
            }
        }
        return  videoFolderinfoArrayList;
    }




    public static List<VideoFolderinfo> getVideoFoldersList(Context context, String path) {
        if (path==null || "".equals(path.trim())){
            return null;
        }

        File f = new File(path);
        List<VideoFolderinfo> files = new LinkedList<>();

        try {
            if (f!=null && f.exists()) {
                File [] filesArray = f.listFiles();
                if (filesArray == null){
                    return files;
                }

                for (File file : f.listFiles()) {

                    if (file!=null && file.isDirectory() && !file.isHidden()) {
                        Log.d("Folders", file.getName());

                        int k = file.getAbsolutePath().lastIndexOf("/Android/data");
                        if (k !=-1) {
                            continue;
                        }
                        if (file.getName().startsWith("com.")){
                            continue;
                        }

                        if (null != file && !file.getName().startsWith(".") && file.list(new VideoFileFilter()).length > 0) {
                            Log.d("Folders  videos", file.getName());
                            File[] vfileArray = file.listFiles(new VideoFileFilter());
                            VideoFolderinfo commonFile = new VideoFolderinfo();
                            commonFile.fileCount = ""+ vfileArray.length;
                            String[] filesVideo = f.list(new VideoFileFilter());
                            commonFile.folderName =    file.getName();
                            commonFile.folderPath  =   file.getPath();
                            commonFile.firstVideoPath = ""+vfileArray[0].getPath();
                            commonFile.last_modified = file.lastModified();
                            commonFile.fileSize =      file.length();
//                            if ( commonFile.last_modified > lastOpenTime){
//                                commonFile.newTag ="New";
//                            }

                            files.add(commonFile);
                        }


                        files.addAll(getVideoFoldersList(context, file.getPath()));
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }



    public  static String getExternalSDcardPath(Context mContext){
        try {
            String strSDCardPath = System.getenv("SECONDARY_STORAGE");

            if ((strSDCardPath == null) || (strSDCardPath.length() == 0)) {
                strSDCardPath = System.getenv("EXTERNAL_SDCARD_STORAGE");
            }

            // For Marshmallow, use getExternalCacheDirs() instead of System.getenv("SECONDARY_STORAGE")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                File[] externalCacheDirs = mContext.getExternalCacheDirs();
                for (File file : externalCacheDirs) {
                    if (Environment.isExternalStorageRemovable(file)) {
                        // Path is in format /storage.../Android....
                        // Get everything before /Android
                        strSDCardPath = file.getPath().split("/Android")[0];
                        return strSDCardPath;
                    }
                }
            }

            //If may get a full path that is not the right one, even if we don't have the SD Card there.
            //We just need the "/mnt/extSdCard/" i.e and check if it's writable
            if (strSDCardPath != null) {
                if (strSDCardPath.contains(":")) {
                    strSDCardPath = strSDCardPath.substring(0, strSDCardPath.indexOf(":"));
                }
                File externalFilePath = new File(strSDCardPath);

                if (externalFilePath.exists() && externalFilePath.canWrite()) {
                    //do what you need here
                    return externalFilePath.getPath();
                }

            }
        }catch (Exception e){

        }
        return null;
    }


}
