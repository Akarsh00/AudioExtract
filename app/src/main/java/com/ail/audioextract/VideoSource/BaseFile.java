package com.ail.audioextract.VideoSource;

import java.io.Serializable;

public class BaseFile implements Serializable {

    private static final long serialVersionUID = 1L;

    int position;

    FileInfo fileInfo;

    int count;

    public boolean isFindDuplicate;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    public void setFindDuplicate(boolean findDuplicate) {
        isFindDuplicate = findDuplicate;
    }

    public void increment() {
        count++;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public int getCount() {
        return count;
    }

    public static class FileInfo implements Serializable {
        int width, height;
        public long size;
        long duration;
        long bitrate;
        int fileType;

        public FileInfo(int width, int height, long size, long duration, long bitrate, int fileType) {
            this.width = width;
            this.height = height;
            this.size = size;
            this.duration = duration;
            this.fileType = fileType;
            this.bitrate = bitrate;
        }

        @Override
        public boolean equals(Object obj) {
            FileInfo fileInfo = (FileInfo) obj;
            return fileInfo.width == width && fileInfo.height == height && fileInfo.size == size
                    && fileInfo.bitrate == bitrate && fileInfo.duration == duration;
        }

        public int getFileType() {
            return fileType;
        }

        public Long getSize() {
            return size;
        }

        public Long getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            int hashcode = (int) ((37 * size) + ((width + height) ^ 21) + (bitrate + duration) ^ 13);
            return hashcode;
        }

    }
}