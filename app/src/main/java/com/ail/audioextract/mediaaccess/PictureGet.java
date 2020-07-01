package com.ail.audioextract.mediaaccess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;


import java.util.ArrayList;

import com.ail.audioextract.mediaaccess.mediaHolders.PictureContent;
import com.ail.audioextract.mediaaccess.mediaHolders.PictureFolderContent;

public class PictureGet {

    private static  PictureGet pictureGet;
    private Context pictureContex;
    public static final Uri externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final Uri internalContentUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
    private static Cursor cursor;


    private PictureGet(Context contx){
        //set up picture getting params
        pictureContex = contx.getApplicationContext();
    }

    static PictureGet getInstance(Context contx){
        if(pictureGet == null){
            pictureGet = new PictureGet(contx);
        }
        return pictureGet;
    }

    /**Returns an ArrayList of {@link PictureContent}  */
    public ArrayList<PictureContent> getAllPictureContents(Uri contentLocation){
        ArrayList<PictureContent> images = new ArrayList<>();
        String[] projection = { MediaStore.Images.Media.DATA ,MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATE_ADDED,MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_MODIFIED};
        cursor = pictureContex.getContentResolver().query( contentLocation, projection, null, null,
                "LOWER ("+MediaStore.Images.Media.DATE_TAKEN+") DESC");

        try {
            cursor.moveToFirst();
            do{
                PictureContent pictureContent = new PictureContent();

                pictureContent.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));

                pictureContent.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));

                pictureContent.setPictureSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                pictureContent.setPictureId(id);

                Uri contentUri = Uri.withAppendedPath(contentLocation, String.valueOf(id));
                pictureContent.setAssertFileStringUri(contentUri.toString());

                try{
                    pictureContent.setDate_added(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)));
                }catch (Exception ex){
                    ex.printStackTrace();
                    pictureContent.setDate_added(0000);
                }

                try{
                    pictureContent.setDate_modified(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)));
                }catch (Exception ex){
                    ex.printStackTrace();
                    pictureContent.setDate_added(0000);
                }

                images.add(pictureContent);
            }while(cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return images;
    }

    /**Returns an ArrayList of {@link PictureFolderContent}  */
    public ArrayList<PictureFolderContent> getPictureFolders(){
        ArrayList<PictureFolderContent> picFolders = new ArrayList<>();
        ArrayList<Integer> picPaths = new ArrayList<>();
        @SuppressLint("InlinedApi") String[] projection = {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.DATE_ADDED};
        cursor = pictureContex.getContentResolver().query(externalContentUri, projection, null, null, "LOWER ("+MediaStore.Images.Media.DATE_TAKEN+") DESC");
        try {
            cursor.moveToFirst();
            do{
                PictureFolderContent photoFolder = new PictureFolderContent();
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                int bucket_id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
                //}else {}

                String folderpaths = datapath.substring(0, datapath.lastIndexOf(folder+"/"));
                folderpaths = folderpaths+folder+"/";
                if (!picPaths.contains(bucket_id)) {
                    picPaths.add(bucket_id);

                    photoFolder.setBucket_id(bucket_id);
                    photoFolder.setFolderPath(folderpaths);
                    photoFolder.setFolderName(folder);
                    picFolders.add(photoFolder);
                }
            }while(cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i = 0;i < picFolders.size();i++){
            Log.d("picture folders",picFolders.get(i).getFolderName()+" and path = "+picFolders.get(i).getFolderPath());
        }
        return picFolders;
    }

    /**Returns an ArrayList of {@link PictureContent} in a specific folder*/
    public ArrayList<PictureContent> getAllPictureContentByBucket_id(int bucket_id){
        ArrayList<PictureContent> images = new ArrayList<>();
        String[] projection = { MediaStore.Images.Media.DATA ,MediaStore.Images.Media.DISPLAY_NAME,MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.SIZE,MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_MODIFIED};
        cursor = pictureContex.getContentResolver().query( externalContentUri, projection, MediaStore.Images.Media.BUCKET_ID + " like ? ", new String[] {"%"+bucket_id+"%"},
                "LOWER ("+MediaStore.Images.Media.DATE_TAKEN+") DESC");

        try {
            cursor.moveToFirst();
            do{
                PictureContent pictureContent = new PictureContent();

                pictureContent.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));

                pictureContent.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));

                pictureContent.setPictureSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                pictureContent.setPictureId(id);

                Uri contentUri = Uri.withAppendedPath(externalContentUri, String.valueOf(id));
                pictureContent.setAssertFileStringUri(contentUri.toString());

                try{
                    pictureContent.setDate_added(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)));
                }catch (Exception ex){
                    ex.printStackTrace();
                    pictureContent.setDate_added(0000);
                }

                try{
                    pictureContent.setDate_modified(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)));
                }catch (Exception ex){
                    ex.printStackTrace();
                    pictureContent.setDate_added(0000);
                }
                images.add(pictureContent);
            }while(cursor.moveToNext());
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return images;
    }

}
