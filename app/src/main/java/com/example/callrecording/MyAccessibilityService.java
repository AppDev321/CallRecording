package com.example.callrecording;


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;


/**
 * Created by sotsys-014 on 4/10/16.
 */

public class MyAccessibilityService extends AccessibilityService {
    private final AccessibilityServiceInfo info = new AccessibilityServiceInfo();
    private static final String TAG = "MyAccessibilityService";
    private static final String TAGEVENTS = "TAGEVENTS";
    private String currentApplicationPackage = "";
    private WindowManager windowManager;
    private boolean showWindow = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
       /*// Log.d(TAG, "onAccessibilityEvent");
        final String sourcePackageName = (String) accessibilityEvent.getPackageName();
        currntApplicationPackage = sourcePackageName;
       // Log.d(TAG, "sourcePackageName:" + sourcePackageName);
       // Log.d(TAG, "parcelable:" + accessibilityEvent.getText().toString());

        //handlePhoneDialerStatus(sourcePackageName,accessibilityEvent);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

      *//*  if (accessibilityEvent.getEventType() == AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION) {
            Log.d(TAGEVENTS, "CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION");
        }
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.d(TAGEVENTS, "TYPE_WINDOW_STATE_CHANGED");
        }
        if (accessibilityEvent.getEventType() == AccessibilityEvent.CONTENT_CHANGE_TYPE_SUBTREE) {
            Log.d(TAGEVENTS, "CONTENT_CHANGE_TYPE_SUBTREE");
        }
        if (accessibilityEvent.getEventType() == AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT) {
            Log.d(TAGEVENTS, "CONTENT_CHANGE_TYPE_TEXT");
        }
        if (accessibilityEvent.getEventType() == AccessibilityEvent.INVALID_POSITION) {
            Log.d(TAGEVENTS, "INVALID_POSITION");
        }
        if (accessibilityEvent.getEventType() == AccessibilityEvent.CONTENT_CHANGE_TYPE_UNDEFINED) {
            Log.d(TAGEVENTS, "CONTENT_CHANGE_TYPE_UNDEFINED");
        }
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_ANNOUNCEMENT) {
            Log.d(TAGEVENTS, "TYPE_ANNOUNCEMENT");
        }
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT) {
            Log.d(TAGEVENTS, "TYPE_ASSIST_READING_CONTEXT");
        }
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_GESTURE_DETECTION_END) {
            Log.d(TAGEVENTS, "TYPE_GESTURE_DETECTION_END");
        }
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            Log.d(TAGEVENTS, "TYPE_VIEW_CLICKED");
        }
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START) {
            Log.d(TAGEVENTS, "TYPE_TOUCH_EXPLORATION_GESTURE_START");
        }
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_GESTURE_DETECTION_START) {
            Log.d(TAGEVENTS, "TYPE_GESTURE_DETECTION_START");
        }
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED) {
            Log.d(TAGEVENTS, "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED");
        }
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
            Log.d(TAGEVENTS, "TYPE_VIEW_ACCESSIBILITY_FOCUSED");
        }
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
            Log.d(TAGEVENTS, "TYPE_WINDOWS_CHANGED");
        }*//*

        if (accessibilityEvent.getPackageName() == null || !(accessibilityEvent.getPackageName().equals("com.bsb.hike") || !(accessibilityEvent.getPackageName().equals("com.whatsapp") || accessibilityEvent.getPackageName().equals("com.facebook.orca") || accessibilityEvent.getPackageName().equals("com.twitter.android") || accessibilityEvent.getPackageName().equals("com.facebook.katana") || accessibilityEvent.getPackageName().equals("com.facebook.lite"))))
            showWindow = false;

        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
        //    Log.d(TAGEVENTS, "TYPE_VIEW_TEXT_CHANGED");
        *//*    if (windowController == null)
                windowController = new WindowPositionController(windowManager, getApplicationContext());
        *//*    showWindow = true;
          //  windowController.notifyDatasetChanged(accessibilityEvent.getText().toString(), currntApplicationPackage);
        } else if(accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
           // Log.d(TAGEVENTS, "TYPE_WINDOW_STATE_CHANGED:"+accessibilityEvent.getContentDescription());

            if (accessibilityEvent.getPackageName().equals("com.whatsapp") && (accessibilityEvent.getContentDescription() == null || !accessibilityEvent.getContentDescription().equals("Type a message")))
                showWindow = false;
            if (accessibilityEvent.getPackageName().equals("com.facebook.katana") && (accessibilityEvent.getText().toString().equals("[What's on your mind?]") || accessibilityEvent.getText().toString().equals("[Search]")))
                showWindow = false;
            if (accessibilityEvent.getPackageName().equals("com.twitter.android") && (accessibilityEvent.getText().toString().equals("[What\u2019s happening?]") || accessibilityEvent.getText().toString().equals("[Search Twitter]")))
                showWindow = false;
            if (accessibilityEvent.getContentDescription()!=null && (accessibilityEvent.getContentDescription().toString().equals("Textbox in chat thread")))
                showWindow = true;


            //remove window when keyboard closed or user moved from chatting to other things
          //  if (windowController != null && !showWindow)
           //     windowController.onDestroy();
        }*/
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onServiceConnected() {
       /* // Set the type of events that this service wants to listen to.
        //Others won't be passed to this service.
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 100;

        this.setServiceInfo(info);*/
    }


    private void handlePhoneDialerStatus(String packageName,AccessibilityEvent event)
    {
        String phoneDialer = "com.google.android.dialer";
        if (packageName.equals( phoneDialer)) {
            if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
                AccessibilityNodeInfo source = event.getSource();
                findCallStateLabel(source,phoneDialer);
               /*     List<AccessibilityNodeInfo> callState = source.findAccessibilityNodeInfosByViewId(phoneDialer+":id/callStateLabel");
                List<AccessibilityNodeInfo> callTimer = source.findAccessibilityNodeInfosByViewId(phoneDialer+":id/callTimer");
                    if (!callState.isEmpty() && !callTimer.isEmpty()) {
                      String  state =    callState.get(0).getText().toString();
                        String timer = callTimer.get(0).getText().toString();
                        Log.e("Accessbility","Call Time = "+state +" -- "+timer);
                    }*/

            }
        }
    }


        private AccessibilityNodeInfo findCallStateLabel(AccessibilityNodeInfo node,String phoneDialer) {
            if (node == null) {
                return null;
            }

          //  Log.e("Accessibility ",""+node.getText());

            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo child = node.getChild(i);


             /*   if (child != null && "com.google.android.dialer:id/callStateLabel".equals(child.getViewIdResourceName())) {
                    return child;
                } else {
                    AccessibilityNodeInfo result = findCallStateLabel(child);
                    if (result != null) {
                        return result;
                    }
                }*/
            }
            return null;
        }
}