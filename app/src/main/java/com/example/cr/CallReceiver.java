package com.example.cr;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.cr.ui.CallListContract;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.content.Context.TELEPHONY_SERVICE;


public class CallReceiver extends BroadcastReceiver {

    String TAG = MainActivity.TAG;

    boolean isRegistered = false;
    String NEW_OUTGOING_CALL_ACTION = "android.intent.action.NEW_OUTGOING_CALL";
    String PHONE_STATE_ACTION = "android.intent.action.PHONE_STATE";

    private String phone_number;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    @Override
    public void onReceive(Context context, Intent intent) {

        ThePhoneStateListener phoneListener = new ThePhoneStateListener(context);

        TelephonyManager telephony = (TelephonyManager) context
                .getSystemService(TELEPHONY_SERVICE);


        try {
            telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException"
                    + e);
        }

    // String outgoingNumber=intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
    preferences =context.getSharedPreferences("CallReceiver",Context.MODE_PRIVATE);
    editor =preferences.edit();

      /*  if (outgoingNumber != null) {
            editor.putString("phone_number", outgoingNumber);
            phone_number = outgoingNumber;
            Log.d(TAG, " phone_number0"
                    +  phone_number);
        }*/
}



    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(NEW_OUTGOING_CALL_ACTION);
        filter.addAction(PHONE_STATE_ACTION);
       // filter.addCategory(Intent.CATEGORY_DEFAULT);
        context.registerReceiver(this, filter);
        isRegistered = true;
        Log.i(TAG, "callReceiverRegistred" + this);
    }


    public void unregister(Context context) {
        try {
            if (isRegistered) {
                context.unregisterReceiver(this);

            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "callReceiverUnRegistred" + this);
        isRegistered = false;
    }

    class ThePhoneStateListener extends PhoneStateListener {

        private Context context;
        Boolean iscallreceived = false;

        ThePhoneStateListener(Context c) {
            super();
            context = c;
        }

        // public static final int NOTIFICATION_ID_RECEIVED = 0x1221;
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            Log.d(TAG, "PhoneListener::onCallStateChanged state:"
                    + state + " incomingNumber:" + incomingNumber);
            //because unregister doesn't work
            if (isRegistered) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:

                        if (iscallreceived) {

                            Boolean stopped = context.stopService(new Intent(context,
                                    CallRecordingService.class));
                        /*Boolean stopped = mPresenter.stopCallService(context, new Intent(context,
                                        CallRecordingService.class));*/
                            Log.d(TAG, "CALL_STATE_IDLE, stoping recording");
                            Log.i(TAG, "stopService for RecordService returned "
                                    + stopped);
                            editor = preferences.edit();
                            editor.putString("phone_number", null);

                            iscallreceived = false;

                        }

                        break;

                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        iscallreceived = true;

                        Intent callIntent = new Intent(context, CallRecordingService.class);
                        if (incomingNumber != null) {
                            callIntent.putExtra("number", incomingNumber);
                        } else {
                            phone_number = preferences.getString("phone_number", "");
                            callIntent.putExtra("number", phone_number);
                        }

                        Log.d(TAG, "CALL_STATE_OFFHOOK" + "incomingNumber" + incomingNumber);

                        ComponentName name = context.startService(callIntent);
                        //ComponentName name =  mPresenter.startCallServise(context, callIntent);
                        if (null == name) {
                            Log.e(TAG,
                                    "startService for RecordService returned null ComponentName");
                        } else {
                            Log.i(TAG,
                                    "startService returned " + name.flattenToString());
                        }

                        Log.i(TAG, "startService returned ");

                        break;

                    //If phone is ringing, save phone_number. This is done because incomingNumber is not saved on CALL_STATE_OFFHOOK
                    case TelephonyManager.CALL_STATE_RINGING:
                        iscallreceived = true;

                        Log.d(TAG, "CALL_STATE_RINGING");
                        Log.d(TAG,
                                "CallBroadcastReceiver intent has EXTRA_PHONE_NUMBER: "
                                        + incomingNumber);
                        editor = preferences.edit();
                        if (incomingNumber != null) {
                            editor.putString("phone_number", incomingNumber);
                            Log.d(TAG, "CALL_STATE_RINGING " + incomingNumber);

                        }

                        editor.commit();
                        break;
                    //If call is answered, run recording service. Also pass "phone_number" variable with incomingNumber to shared prefs, so service will be able to access that via shared prefs.

                    default:
                        Log.i(TAG, "UNKNOWN_STATE: " + state);
                        break;
                }
            }
        }

    }

}

