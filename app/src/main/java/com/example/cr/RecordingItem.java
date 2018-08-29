package com.example.cr;

import android.support.annotation.NonNull;

import java.io.File;

public class RecordingItem extends File{


    boolean isPlaying;

    public RecordingItem(@NonNull String pathname, Boolean isPlaying) {
        super(pathname);
    }


    public void setPlaying(Boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }
}
