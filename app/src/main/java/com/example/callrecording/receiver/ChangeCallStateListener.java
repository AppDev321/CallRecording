package com.example.callrecording.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChangeCallStateListener extends BroadcastReceiver {

    private static String lastState = TelephonyManager.EXTRA_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CallObserver", "CallReceiver is starting ....");

        List<String> keyList = new ArrayList<>();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            keyList = new ArrayList<>(bundle.keySet());
            Log.e("CallObserver", "keys : " + keyList);
        }

        if (keyList.contains("incoming_number")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String phoneIncomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            String phoneOutgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            String phoneNumber = phoneOutgoingNumber != null ? phoneOutgoingNumber : (phoneIncomingNumber != null ? phoneIncomingNumber : "");

            if (phoneState != null && phoneNumber != null) {
                if (lastState.equals(phoneState)) {
                    //No change, debounce extras
                    return;
                }
                Log.e("CallObserver", "phoneState = " + phoneState);
                if (TelephonyManager.EXTRA_STATE_RINGING.equals(phoneState)) {
                    isIncoming = true;
                    callStartTime = new Date();
                    //
                    lastState = TelephonyManager.EXTRA_STATE_RINGING;
                    if (phoneNumber != null) {
                        savedNumber = phoneNumber;
                    }


                    onIncomingCallStarted(context, savedNumber, callStartTime);
                } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(phoneState)) {

                    if (lastState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        //
                        lastState = TelephonyManager.EXTRA_STATE_IDLE;
                        onMissedCall(context, savedNumber, callStartTime);
                    } else {
                        if (isIncoming) {
                            //
                            lastState = TelephonyManager.EXTRA_STATE_IDLE;
                            onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                        } else {
                            //
                            lastState = TelephonyManager.EXTRA_STATE_IDLE;
                            Log.d("CallObserver", "onOutgoingCallEnded called !! : ");
                            onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                        }

                    }
                } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(phoneState)) {
                    if (lastState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        isIncoming = true;
                    } else {
                        isIncoming = false;
                    }
                    callStartTime = new Date();
                    savedNumber = phoneNumber;
                    //
                    lastState = TelephonyManager.EXTRA_STATE_OFFHOOK;
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
                }
            }
        }

    }


    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Log.d("CallObserver", "onIncomingCallStarted  :  " + " number is  : " + number);
    }

    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.d("CallObserver", "onOutgoingCallStarted  :  " + " number is  : " + number);
    }

    protected void onIncomingCallEnded(Context context, String number, Date start, Date end) {
    }

    protected void onOutgoingCallEnded(Context context , String number, Date start, Date end) {
    }

    protected void onMissedCall(Context context, String number, Date start) {
    }

}