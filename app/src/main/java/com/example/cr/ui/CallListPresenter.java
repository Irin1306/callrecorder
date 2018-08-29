package com.example.cr.ui;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.example.cr.data.AppDataInjector;
import com.example.cr.data.entity.Recording;
import com.example.cr.data.sourse.DataRepository;
import com.example.cr.data.sourse.DataSource;
import com.example.cr.data.sourse.local.LocalDatabase;
import com.example.cr.ui.CallListContract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

public class CallListPresenter implements CallListContract.Presenter {
    Context context;

    private CallListContract.View mView;

    private DataRepository mData;

    public CallListPresenter(CallListContract.View view, Context context) {
        context = context;
        mView = view;
       // mView.setPresenter(this);

        mData = AppDataInjector.provideDataRepository(context);
    }


    @Override
    public void saveRecording(final Recording recording) {
        mData.saveRecording(recording, new DataSource.SaveCallback() {
            @Override
            public void onSaved() {
               // Toast.makeText(context, "recording is saved in db" + recording, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void getRecordings() {
        mData.getAllRecordings(new DataSource.GetRecordingsCallback() {
            @Override
            public void onRecordingsLoaded(List<Recording> recordings) {
                //mView.setRecordings(recordings);
            }
        });
    }

    @Override
    public void findByNameAndDelete(String name) {
        mData.getRecordingByName(name, new DataSource.RecordingLoadedCallback() {
            @Override
            public void onRecordingLoaded(Recording recording) {
                if (recording != null) {
                    mData.deleteRecording(recording, new DataSource.DeleteCallback() {
                        @Override
                        public void onDeleted() {
                            //Toast.makeText(context, "recording is deleted from db" + recording, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }



    @Override
    public void makeSearch(String searchString) {

    }

    @Override
    public void deleteAll() {
        mData.deleteAllRecordings(new DataSource.DeleteCallback() {
            @Override
            public void onDeleted() {
                //
            }
        });
    }


}
