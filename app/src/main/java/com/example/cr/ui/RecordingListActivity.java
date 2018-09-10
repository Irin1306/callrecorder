package com.example.cr.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.cr.R;
import com.example.cr.RecordingItem;
import com.example.cr.data.entity.Recording;
import com.example.cr.data.sourse.DataRepository;
import com.example.cr.ui.RecordingAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecordingListActivity extends AppCompatActivity implements CallListContract.View{

    private CallListContract.Presenter mPresenter;

    private CallListContract.View mView = this;


    private Toolbar toolbar;
    private RecyclerView recyclerViewRecordings;

    private List<RecordingItem> recordingItemList = new ArrayList<>();
    private RecordingAdapter recordingAdapter;
    private TextView textViewNoRecordings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recording_list);

        recyclerViewRecordings = findViewById(R.id.recycleViewRecording);
        textViewNoRecordings =findViewById(R.id.textViewNoRecordings);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Recording List");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        setSupportActionBar(toolbar);

        // Now you can use the get methods:
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GridLayoutManager grid =
                new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        recyclerViewRecordings.setLayoutManager(grid);
        recyclerViewRecordings.setHasFixedSize(false);

        recyclerViewRecordings.setAdapter(recordingAdapter);

        //new CallListPresenter(this, getApplicationContext());

    }


    @Override
    protected void onResume() {
        super.onResume();
        //mPresenter.getRecordings();
        fetchRecordings();
    }

    @Override
    protected void onPause() {
        super.onPause();
        recordingItemList.clear();
    }

    private void fetchRecordings() {
        File root = android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/CallRecorder/";
        //File db = getDatabasePath("callrecordingsdb.db");
        //String path = db.getAbsolutePath() + "/audio/";
        Log.d("filename", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();

        //List recList = new ArrayList<>();
        if(files != null && files.length > 0) {   Log.d("filename", "Size: " + files.length);

            for(int i = 0; i < files.length; i++) {

                Log.i ("filename", "FileName: " + files[i].getName());
                String fileName = files[i].getAbsolutePath();
                //String recordingUri = root.getAbsolutePath() + "/CallRecorder/";
                //String recordingPath = path + fileName;

                RecordingItem rec = new RecordingItem( fileName, false);

                recordingItemList.add(rec);

            }
            //recordingItemList = recList;

            textViewNoRecordings.setVisibility(View.GONE);
            recyclerViewRecordings.setVisibility(View.VISIBLE);

            setAdaptertoRecyclerView();

        }else{
            Log.d("filename", "files: " + files);
            textViewNoRecordings.setVisibility(View.VISIBLE);
            recyclerViewRecordings.setVisibility(View.GONE);
        }

    }

    private void setAdaptertoRecyclerView() {
        recordingAdapter = new RecordingAdapter(this,recordingItemList);
        recyclerViewRecordings.setAdapter(recordingAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

   /* @Override
    public void setPresenter(CallListContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void setRecordings(List<Recording> recordings) {

       *//* RecordingAdapter recordingAdapter =
                new RecordingAdapter(mView, mPresenter, recordings, recordingItemList);*//*
        RecordingAdapter recordingAdapter =
                new RecordingAdapter(mView, recordings, recordingItemList);

        GridLayoutManager grid =
                new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        recyclerViewRecordings.setLayoutManager(grid);
        recyclerViewRecordings.setHasFixedSize(false);

        recyclerViewRecordings.setAdapter(recordingAdapter);
    }
*/

}
