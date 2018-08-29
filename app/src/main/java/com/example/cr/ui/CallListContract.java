package com.example.cr.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.example.cr.data.entity.Recording;

import java.util.List;
import java.util.logging.Filter;

public interface CallListContract {

    interface View {

       // void setPresenter(Presenter presenter);


      //  void setRecordings(List<Recording> Recordings);


    }



    interface Presenter {
        void saveRecording(Recording recording);

        void getRecordings();

        void findByNameAndDelete(String name);

        void makeSearch(String searchString);

        void deleteAll();
    }



}
