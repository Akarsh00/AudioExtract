package com.ail.audioextract.VideoSource;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public class VideoFileFilter implements FilenameFilter {
    private final  String[] acceptedExtensions = RootHelper.videoacceptedExtensions;

    @Override
    public boolean accept(File dir, String filename) {

        for (int _i = 0; _i < acceptedExtensions.length; _i++) {
            if (filename.endsWith("." + acceptedExtensions[_i])) {
                return true;
            }
        }
        return false;
    }

}
