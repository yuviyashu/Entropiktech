package com.example.lap.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;


import com.example.lap.library.DataBase.DataBaseCommunicator;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Lap on 4/1/2016.
 */
public class TouchTrack extends GestureDetector.SimpleOnGestureListener implements
                                GestureDetector.OnDoubleTapListener {


    private Window window;
    private static final String DEBUG_TAG = "Gestures";
    private  SharedPreferences touchTrackDataStorePreference;
    private  SharedPreferences.Editor editor;
    private  Context context;
    private MotionEvent event;
    private SharedPreferences touchTrackDataStorePreference2;
    private static HashMap<String,List<String>> eventMap=new HashMap<String,List<String>>();


    public TouchTrack(Window window, Context context){
        super();
        this.window = window;
        this.context = context;

    }



    @Override
    public boolean onDown(MotionEvent event) {
       Log.d(DEBUG_TAG,"onDown: " + event.toString());
       // Log.d("eventname", event.actionToString(event.getActionMasked()));
        putDataIntoShared1Preference(event,"onDown");
        return true;
    }



    @Override
    public void onShowPress(MotionEvent event) {
        putDataIntoShared1Preference(event,"onShowPress");
       Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        putDataIntoShared1Preference(event,"onSingleTapUp");
        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
        putDataIntoShared1Preference(event,"onScroll");
      Log.d(DEBUG_TAG, "onScroll: " + event1.toString()+ " distanceX->" + distanceX + " distanceY->" + distanceY);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        putDataIntoShared1Preference(event,"onLongPress");
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        putDataIntoShared1Preference(event,"onFling");
     Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString()+" vwlocityX-> "+ velocityX + " velocityY-> "+velocityY);
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        putDataIntoShared1Preference(event,"onSingleTapConfirmed");
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        putDataIntoShared1Preference(event,"onDoubleTap");
        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        putDataIntoShared1Preference(event,"DoubleTapEvent");
        Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }


    /**
     * Put the data in shared preference
     * @param motionEvent
     */
    private void putDataIntoShared1Preference(MotionEvent motionEvent, final String gestureType){

        this.event=motionEvent;
        Thread backend=new Thread(new Runnable() {
            @Override
            public void run() {

                //Fetching gesture event name to use it as a key in hash map
                String eventName=event.actionToString(event.getActionMasked());

                //If same event is already present in event Map
                // then update in the DB and clear
                // the eventMap for that
                // particular key and then insert it
                // and call the same function recursively
                if(eventMap.containsKey(eventName)) {

                    DataBaseCommunicator.setDataSet((eventMap.get(eventName)).toString());
                    eventMap.remove(eventName);
                    Log.e("sp status","Already present update DB " +  eventName);

                }else{

                    Log.e("sp status","Not present put in SF " + eventName);
                    eventMap.put(eventName, getStringDataSet(gestureType+"->"+eventName,event));

                }
            }
        });

        backend.start();

    }

    /**
     * Fetch all the required information like gesture name, event time occurred, x and y position and x2 and y2 (for multiple touch)
     * @param eventName
     * @param event
     * @return
     */
    private List<String> getStringDataSet(String eventName,MotionEvent event) {

        List<String> dataSet=new ArrayList<String>();

        //0. Adding event name
        dataSet.add(eventName);

        //1.Adding device id (if device id is non-zero then  it is physical device)
        dataSet.add(Integer.toString(event.getDeviceId()));

        //2. Adding event time
        dataSet.add(getEventTimeInString());

        //3. Adding X1
        dataSet.add(Float.toString(event.getX()));

        //4. Adding Y1
        dataSet.add(Float.toString(event.getY()));

        //4. Adding X2 and Y2 (This will occur on multiple touch events)
        if(eventName.equals("ACTION_POINTER_UP") || eventName.equals("ACTION_POINTER_DOWN") ){//if the gesture is multiple touch gesture the condition passed

            //5
            dataSet.add(Float.toString(event.getX(1)));
            //6
            dataSet.add(Float.toString(event.getY(1)));
        }else{ //put empty value for x2 and y2 for single touch gestures
            //5
            dataSet.add("-");
            //6
            dataSet.add("-");
        }

        //return data set which contains all the required info
        return dataSet;
    }


    /**
     * For getting current android device time
     * @return datetime string
     */
    private String getEventTimeInString() {

        GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("IST"));
        gregorianCalendar.setGregorianChange(new Date(System.currentTimeMillis()));//Setting to current time
        return gregorianCalendar.getGregorianChange().toString();

    }


}
