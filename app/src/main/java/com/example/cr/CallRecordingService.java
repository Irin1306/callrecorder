package com.example.cr;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.util.SortedList;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cr.data.AppDataInjector;
import com.example.cr.data.entity.Recording;
import com.example.cr.data.sourse.DataRepository;
import com.example.cr.data.sourse.DataSource;
import com.example.cr.data.sourse.local.LocalDatabase;
import com.example.cr.ui.CallListContract;
import com.example.cr.ui.CallListPresenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;



public class CallRecordingService extends Service implements Runnable {

    private CallListContract.Presenter mPresenter;

    private static boolean isRecording = false;
    private static String fileName = null;
    private static long fileDate;
    private final Handler mHandler = new Handler();
    String TAG = MainActivity.TAG;
    //MediaRecorder recorder;
    Thread mThread;
    SharedPreferences preferences;
    private DataRepository mData;
    private List<Recording> mRecordingList;
    private volatile String phoneNumber;

    //private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
   // private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    //private static final String AUDIO_RECORDER_FOLDER = "CallRecorder";

    private MediaRecorder recorder = null;
   // private int currentFormat = 0;
  /*  private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4,
            MediaRecorder.OutputFormat.THREE_GPP };
    private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4,
            AUDIO_RECORDER_FILE_EXT_3GP };*/


    private final BroadcastReceiver callStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyPhoneStateListener phoneListener = new MyPhoneStateListener(
                    context);
            TelephonyManager telephony = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(phoneListener,
                    PhoneStateListener.LISTEN_CALL_STATE);

        }

        class MyPhoneStateListener extends PhoneStateListener {
            private Context context;

            MyPhoneStateListener(Context c) {
                super();
                context = c;
            }

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (isRecording) {

                            stopRecording();
                        }
                        break;
                }
            }

        }
    };
    private String INCOMING_CALL_ACTION = "android.intent.action.PHONE_STATE";

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        // super.onCreate();
        Log.d(TAG, "onCreate");
        Toast.makeText(getApplicationContext(), "CallRecordingService onCreate", Toast.LENGTH_SHORT).show();

        mData = AppDataInjector.provideDataRepository(getApplicationContext());

        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter.addAction(INCOMING_CALL_ACTION);
        registerReceiver(callStateReceiver, intentToReceiveFilter, null,
                mHandler);


        mThread = new Thread(this);

        mThread.start();



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        // super.onStart(intent, startId);
        Log.d(TAG, "onStartCommand");
        //Toast.makeText(getApplicationContext(), "CallRecordingService onStartCommand", Toast.LENGTH_SHORT).show();

        if (intent.hasExtra("number")) {
            phoneNumber = intent.getStringExtra("number");
        }
        preferences = getApplicationContext().getSharedPreferences("CallReceiver", Context.MODE_PRIVATE);

       // Log.d(TAG, "phoneNumber" + phoneNumber);
       // Toast.makeText(getApplicationContext(), "phoneNumber" + phoneNumber, Toast.LENGTH_SHORT).show();

        return START_STICKY;

    }

   /* private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
    }
*/
    /*private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(getApplicationContext(),
                    "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Toast.makeText(getApplicationContext(),
                    "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
                    .show();
        }
    };
*/
    @Override
    public synchronized void run() {
        Looper.myLooper();
        Looper.prepare();


        while (phoneNumber == null || phoneNumber.equals("")) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Proceed when condition holds
        if (phoneNumber != null && !phoneNumber.equals("")) {

            File root = android.os.Environment.getExternalStorageDirectory();
           // File db = getDatabasePath("crdatabase.db");
           // Log.i(TAG, "getDatabasePath(\"crdatabase.db\")" + db);
            File file = new File(root.getAbsolutePath() + "/CallRecorder/");

            if (!file.exists()) {
                file.mkdirs();
            }

                    //execute what ever you want to do
                    //Date date = new Date();
                    //CharSequence sdf = android.text.format.DateFormat.format("hh:mm:ss", date.getTime());
                    // CharSequence sdf = android.text.format.DateFormat.format("hh:mm:ss", new java.util.Date());
                    //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;

                    Log.i(TAG, "phoneNumber before" + phoneNumber);

                    fileName = root.getAbsolutePath() + "/CallRecorder/" + phoneNumber + "+" +
                            //fileName = db.getAbsolutePath() + "/audio/" + phoneNumber + "/" +
       //                     String.valueOf(System.currentTimeMillis()) + file_exts[currentFormat];
                            String.valueOf(System.currentTimeMillis()) + ".3gp";
                    // dateFormat.format(date) + ".3gp";
                    //sdf + ".3gp";*/
                    //Log.i(TAG, "fileName before" + fileName);
                    //Toast.makeText(getApplicationContext(), "fileName " + fileName, Toast.LENGTH_SHORT).show();
                    fileDate = System.currentTimeMillis();

                    recorder = new MediaRecorder();
                        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                       // recorder.setOutputFormat(output_formats[currentFormat]);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorder.setOutputFile(fileName);
                       // recorder.setOnErrorListener(errorListener);
                       // recorder.setOnInfoListener(infoListener);

                        try {
                            recorder.prepare();
                            recorder.start();
                            isRecording = true;
                        } catch (IllegalStateException e) {
                            Log.e("REDORDING :: ",e.getMessage());
                            e.printStackTrace();
                        } catch (IOException e) {
                            Log.e("REDORDING :: ",e.getMessage());
                            e.printStackTrace();
                        }

                    Log.i(TAG, "fileName prepared" + fileName);
                    Log.i(TAG, "Call Recording started");
            //Toast.makeText(getApplicationContext(), "CR started" + fileName, Toast.LENGTH_LONG).show();
        }

    }

    void stopRecording() {
        // Then clean up with when it hangs up:
      //  try{
            if (recorder != null) {
                recorder.stop();
                //recorder.reset();
                recorder.release();
                recorder = null;
                isRecording = false;
                phoneNumber = null;
            }
      //  }catch(RuntimeException stopException){
     //       Log.i(TAG, "RuntimeException" + stopException);
      //  }


           // Toast.makeText(getApplicationContext(), "CR stopped" + fileName, Toast.LENGTH_LONG).show();


    }
    /*Recording recording = new Recording(fileName, phoneNumber );
            Log.i(TAG, "recording ----" + recording.getName());
    saveRecording(recording);*/
    public void saveRecording(Recording recording) {
        mData.saveRecording(recording, new DataSource.SaveCallback() {
            @Override
            public void onSaved() {
                Toast.makeText(getApplicationContext(),
                        "recording is saved", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onDestroy() {

        unregisterReceiver(callStateReceiver);
       /* if (isRecording) {
            stopRecording();
            Log.d(TAG, "recorder.stop, recorder.release, recording = false");
        }*/
       /* if (new File(fileName).exists()) {
            Recording recording = new Recording(fileName, phoneNumber, fileDate);
            Log.i(TAG, "recording ----" + recording.getName());
            saveRecording(recording);
            Toast.makeText(getApplicationContext(), "Server: recording is saved in db" + recording, Toast.LENGTH_SHORT).show();
        }*/


        super.onDestroy();
        Log.d(TAG, "onDestroy");
       // Toast.makeText(getApplicationContext(), "CallRecordingService onDestroy", Toast.LENGTH_SHORT).show();
    }


    /*private void copyFile() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                //String currentDBPath =
                       // getDatabasePath("crdatabase.db").getAbsolutePath();
                //String backupDBPath = "crdatabase.db";
                //previous wrong  code
                // **File currentDB = new File(data,currentDBPath);**
                // correct code
               // File currentDB = new File(currentDBPath);

                File currentDB = getDatabasePath("crdatabase.db");
                String backupDBPath = LocalDatabase.getInstance(getApplicationContext()).getOpenHelper().getWritableDatabase().getPath();
                Log.i(TAG, "backupDBPath ----" + backupDBPath);
                File backupDB = new File(sd, backupDBPath);


                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}



   /* public static int getAudioSource(String str) {
        if (str.equals("MIC")) {
            return MediaRecorder.AudioSource.MIC;
        }
        else if (str.equals("VOICE_COMMUNICATION")) {
            return MediaRecorder.AudioSource.VOICE_COMMUNICATION;
        }
        else if (str.equals("VOICE_CALL")) {
            return MediaRecorder.AudioSource.VOICE_CALL;
        }
        else if (str.equals("VOICE_DOWNLINK")) {
            return MediaRecorder.AudioSource.VOICE_DOWNLINK;
        }
        else if (str.equals("VOICE_UPLINK")) {
            return MediaRecorder.AudioSource.VOICE_UPLINK;
        }
        else if (str.equals("VOICE_RECOGNITION")) {
            return MediaRecorder.AudioSource.VOICE_RECOGNITION;
        }
        else if (str.equals("CAMCORDER")) {
            return MediaRecorder.AudioSource.CAMCORDER;
        }
        else {
            return MediaRecorder.AudioSource.DEFAULT;
        }
    }

    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
String sampleRate =  audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
String sampleBufferSize = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
int bufferSize = AudioRecord.getMinBufferSize(Integer.parseInt(sampleRate), AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

    */