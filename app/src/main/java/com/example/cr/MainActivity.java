package com.example.cr;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.cr.ui.CallListContract;
import com.example.cr.ui.RecordingListActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    public final static String TAG = "MainActivity";

    public static final int RECORD_REQUEST_CODE = 123;


    private Toolbar toolbar;

    private final CallReceiver callReceiver = new CallReceiver();


    ToggleButton startandoff;
    TextView listenTextView;




    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToRecordAudio() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
            /* || ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED*/
                ) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.READ_PHONE_STATE//, Manifest.permission.WAKE_LOCK
            }, RECORD_REQUEST_CODE);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.length == 5 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
                // && grantResults[5] == PackageManager.PERMISSION_GRANTED
                    ) {
                Toast.makeText(this, "Record Audio Permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_LONG).show();
                finishAffinity();
            }
        }

    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startandoff = (ToggleButton) findViewById(R.id.toggleButton);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Call Recorder");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));

        setSupportActionBar(toolbar);
        listenTextView = findViewById(R.id.listenTextView);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToRecordAudio();
        }
        startandoff.setChecked(false);
        listenTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (v == listenTextView) {
                    Intent intent = new Intent(getApplicationContext(), RecordingListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });


    }



    public void togglebutton(View view) {

        boolean checked = ((ToggleButton) view).isChecked();


           // if (onRequestPermissionsResult()) {

                if (checked) {

                    callReceiver.register(this);


                } else {

                    callReceiver.unregister(this);

                }
            //} else {
             //   getPermissionToRecord();
           //}


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_list:
                Intent intent = new Intent(this, RecordingListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {

//
    }

    @Override
    protected void onPause() {
        super.onPause();
       /*  callReceiver.unregister(getApplicationContext());*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //callReceiver.unregister(this);

    }
}








