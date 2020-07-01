package com.ail.audioextract.mediaaccess.mediaHolders;

import java.util.ArrayList;

public class PictureFolderContent {

    private String folderpath;
    private String folderName;
    private ArrayList<PictureContent> photos;
    private int bucket_id;

    public PictureFolderContent(){
        photos = new ArrayList<>();
    }

    public PictureFolderContent(String path, String folderName) {
        this.folderpath = path;
        this.folderName = folderName;
        photos = new ArrayList<>();
    }

    public String getFolderPath() {
        return folderpath;
    }

    public void setFolderPath(String path) {
        this.folderpath = path;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public ArrayList<PictureContent> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<PictureContent> photos) {
        this.photos = photos;
    }

    public int getBucket_id() {
        return bucket_id;
    }

    public void setBucket_id(int bucket_id) {
        this.bucket_id = bucket_id;
    }
}
