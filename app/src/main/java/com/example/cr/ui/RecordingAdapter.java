package com.example.cr.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.cr.MainActivity;
import com.example.cr.R;
import com.example.cr.RecordingItem;
import com.example.cr.data.entity.Recording;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;



public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.ViewHolder> {

    String TAG = MainActivity.TAG;
    private Context mContext;
    private CallListContract.View mView;
    private CallListContract.Presenter mPresenter;

    private List<Recording> mRecordingList;
    private List<RecordingItem> mRecordingItemList;

    private MediaPlayer mMediaPlayer;
    private boolean isPlaying = false;

    public RecordingAdapter( Context context,
            //CallListContract.View view,
                          //  CallListContract.Presenter presenter,
                          //  List<Recording> recordingList,
                            List<RecordingItem> recordingItemList) {
        //mView = view;
        //mPresenter = presenter;
        //mRecordingList = recordingList;

        //mView.setAdapter(this);
       // mPresenter.setAdapter(this);

        mContext = context;
        mRecordingItemList = recordingItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recording_item_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        setUpData(holder,position);


    }

    private void setUpData(ViewHolder holder, int position) {

        RecordingItem rec = mRecordingItemList.get(position);
        holder.textViewName.setText(rec.getName());

        if (rec.isPlaying()) {
            holder.imageViewPlay.setImageResource(R.drawable.ic_pause_black_24dp);
            //TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
            holder.seekBar.setVisibility(View.VISIBLE);
            holder.seekUpdation(holder);
        } else {
            holder.imageViewPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            // TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
            holder.seekBar.setVisibility(View.GONE);
        }

        holder.manageSeekBar(holder);

        holder.imageViewPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecordingItem rec = mRecordingItemList.get(holder.getAdapterPosition());
                int position = holder.getAdapterPosition();

                Log.d("filename", "Click" + "getPath" + rec.getPath() + "getAbsolutePath"
                        + rec.getAbsolutePath() + "getName" + rec.getName() + "getUri()" +
                        "isPlaying" + rec.isPlaying());

                // Recording recording = recordingArrayList.get(position);
                //Recording recording = recordingArrayList.get(holder.getAdapterPosition());
                // recordingUri = recording.getUri();

                if (isPlaying) {
                    holder.stopPlaying();
                    rec.setPlaying(false);

                    Log.d("filename", "Stop" + rec.getName());

                } else {
                    holder.markAllPaused(position);
                    holder.startPlaying(rec, position);
                    rec.setPlaying(true);

                    Log.d("filename", "isPlaying" + rec.getName());

                }
            }
        });


        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                holder.deleteRec(rec, position);
               // notifyDataSetChanged();
                // notifyItemRemoved(holder.getAdapterPosition());
                //String name = rec.getAbsolutePath();

                // mPresenter.findByNameAndDelete(name);
                //mPresenter.deleteAll();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecordingItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewPlay, imageViewDelete;
        SeekBar seekBar;
        TextView textViewName;
        // private String recordingUri;
        private int lastProgress = 0;
        private Handler mHandler = new Handler();
        ViewHolder holder;


        public ViewHolder(View itemView) {
            super(itemView);

            imageViewPlay = itemView.findViewById(R.id.imageViewPlay);
            seekBar = itemView.findViewById(R.id.seekBar);
            textViewName = itemView.findViewById(R.id.recordingName);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
        }


        public void manageSeekBar(ViewHolder holder) {
            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (mMediaPlayer != null && fromUser) {
                        mMediaPlayer.seekTo(progress);
                        //notifyDataSetChanged();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        private void markAllPaused(int position) {
            for (int i = 0; i < mRecordingItemList.size(); i++) {
                //if (i != position) {
                mRecordingItemList.get(i).setPlaying(false);
                //mRecordingItemList.set(i, mRecordingItemList.get(i));
               // }
            }
            notifyDataSetChanged();
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                seekUpdation(holder);
            }
        };



        private void seekUpdation(ViewHolder holder) {
            this.holder = holder;
            if (mMediaPlayer != null) {
                int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                holder.seekBar.setMax(mMediaPlayer.getDuration());
                holder.seekBar.setProgress(mCurrentPosition);
                lastProgress = mCurrentPosition;

            }
            mHandler.postDelayed(runnable, 100);
        }

        private void stopPlaying() {
            if (mMediaPlayer != null) {
                try{
                    //mMediaPlayer.stop();
                    mMediaPlayer.release();
                }catch (Exception e){
                    e.printStackTrace();
                }

                mMediaPlayer = null;
                imageViewPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                isPlaying = false;
            }

        //showing the play button
            imageViewPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }

        private void startPlaying(final RecordingItem audio, final int position) {

            mMediaPlayer = new MediaPlayer();
            Log.e(TAG, "setDataSource" + audio.getAbsolutePath());

            try {
                mMediaPlayer.setDataSource(audio.getAbsolutePath());
                mMediaPlayer.prepare();
                mMediaPlayer.start();

            } catch (IOException e) {
                Log.e(TAG, "prepare() failed");
            }

            //making the imageview pause button
            imageViewPlay.setImageResource(R.drawable.ic_pause_black_24dp);

            //seekBar.setProgress(lastProgress);
            //mMediaPlayer.seekTo(lastProgress);
            seekBar.setMax(mMediaPlayer.getDuration());
            isPlaying = true;
            //seekUpdation();


            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mMediaPlayer != null) {
                       //mMediaPlayer.release();
                        mMediaPlayer = null;
                        imageViewPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        audio.setPlaying(false);
                        notifyItemChanged(position);
                    }
                }
            });
           /* mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mMediaPlayer != null) {
                        seekBar.setProgress(lastProgress);
                        mMediaPlayer.seekTo(lastProgress);
                        seekBar.setMax(mMediaPlayer.getDuration());
                        mMediaPlayer.start();
                        audio.setPlaying(true);
                        // seekUpdation();
                    }
                }
            });*/


        }

        private void deleteRec(RecordingItem rec, int position) {
            if (mRecordingItemList.size() == 0) {
                return;
            }
            if (rec.delete()) {

                mRecordingItemList.remove(position);
                //notifyDataSetChanged();
                notifyItemRemoved(position);
                Log.e(TAG, "recording deleted");
            } else {
                Log.e(TAG, "recording was not found" + rec.getName());
            }


        }
    }

}


 /*   private void initViews() {

        *//** setting up the toolbar  **//*
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Voice Recorder");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        setSupportActionBar(toolbar);

        linearLayoutRecorder = (LinearLayout) findViewById(R.id.linearLayoutRecorder);
        chronometer = (Chronometer) findViewById(R.id.chronometerTimer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        imageViewRecord = (ImageView) findViewById(R.id.imageViewRecord);
        imageViewStop = (ImageView) findViewById(R.id.imageViewStop);
        imageViewPlay = (ImageView) findViewById(R.id.imageViewPlay);
        linearLayoutPlay = (LinearLayout) findViewById(R.id.linearLayoutPlay);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        imageViewRecord.setOnClickListener(this);
        imageViewStop.setOnClickListener(this);
        imageViewPlay.setOnClickListener(this);

    }

    private void prepareforRecording() {
        TransitionManager.beginDelayedTransition(linearLayoutRecorder);
        imageViewRecord.setVisibility(View.GONE);
        imageViewStop.setVisibility(View.VISIBLE);
        linearLayoutPlay.setVisibility(View.GONE);
    }


    private void startRecording() {
        //we use the MediaRecorder class to record
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        *//**In the lines below, we create a directory VoiceRecorderSimplifiedCoding/Audios in the phone storage
         * and the audios are being stored in the Audios folder **//*
        File root = android.os.Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + "/VoiceRecorderSimplifiedCoding/Audios");
        if (!file.exists()) {
            file.mkdirs();
        }

        fileName =  root.getAbsolutePath() + "/VoiceRecorderSimplifiedCoding/Audios/" +
                String.valueOf(System.currentTimeMillis() + ".mp3");
        Log.d("filename",fileName);
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastProgress = 0;
        seekBar.setProgress(0);
        stopPlaying();
        //starting the chronometer
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }


    private void stopPlaying() {
        try{
            mPlayer.release();
        }catch (Exception e){
            e.printStackTrace();
        }
        mPlayer = null;
        //showing the play button
        imageViewPlay.setImageResource(R.drawable.ic_play);
        chronometer.stop();
    }

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private String fileName = null;
    private int lastProgress = 0;
    private Handler mHandler = new Handler();
    private boolean isPlaying = false;

    private void prepareforStop() {
        TransitionManager.beginDelayedTransition(linearLayoutRecorder);
        imageViewRecord.setVisibility(View.VISIBLE);
        imageViewStop.setVisibility(View.GONE);
        linearLayoutPlay.setVisibility(View.VISIBLE);
    }

    private void stopRecording() {

        try{
            mRecorder.stop();
            mRecorder.release();
        }catch (Exception e){
            e.printStackTrace();
        }
        mRecorder = null;
        //starting the chronometer
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        //showing the play button
        Toast.makeText(this, "Recording saved successfully.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if( view == imageViewRecord ){
            prepareforRecording();
            startRecording();
        }else if( view == imageViewStop ){
            prepareforStop();
            stopRecording();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
//fileName is global string. it contains the Uri to the recently recorded audio.
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }
        //making the imageview pause button
        imageViewPlay.setImageResource(R.drawable.ic_pause);

        seekBar.setProgress(lastProgress);
        mPlayer.seekTo(lastProgress);
        seekBar.setMax(mPlayer.getDuration());
        seekUpdation();
        chronometer.start();

        *//** once the audio is complete, timer is stopped here**//*
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imageViewPlay.setImageResource(R.drawable.ic_play);
                isPlaying = false;
                chronometer.stop();
            }
        });

        *//** moving the track as per the seekBar's position**//*
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if( mPlayer!=null && fromUser ){
                    //here the track's progress is being changed as per the progress bar
                    mPlayer.seekTo(progress);
                    //timer is being updated as per the progress of the seekbar
                    chronometer.setBase(SystemClock.elapsedRealtime() - mPlayer.getCurrentPosition());
                    lastProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    private void seekUpdation() {
        if(mPlayer != null){
            int mCurrentPosition = mPlayer.getCurrentPosition() ;
            seekBar.setProgress(mCurrentPosition);
            lastProgress = mCurrentPosition;
        }
        mHandler.postDelayed(runnable, 100);
    }

    @Override
    public void onClick(View view) {
        if( view == imageViewRecord ){
            prepareforRecording();
            startRecording();
        }else if( view == imageViewStop ){
            prepareforStop();
            stopRecording();
        }else if( view == imageViewPlay ){
            if( !isPlaying && fileName != null ){
                isPlaying = true;
                startPlaying();
            }else{
                isPlaying = false;
                stopPlaying();
            }
        }

    }*/

