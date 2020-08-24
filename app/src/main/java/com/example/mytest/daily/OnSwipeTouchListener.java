package com.example.mytest.daily;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    GestureListener listener;



    public OnSwipeTouchListener(Context context) {
       // Log.d("스와이프터치리스너", "OnSwipeTouchListener: ");
        gestureDetector = new GestureDetector(context, new GestureListener());
        listener = new GestureListener();
    }

    public void onSwipeLeft() {
        //Log.d("스와이프터치리스너", "onSwipeLeft: ");
    }

    public void onSwipeRight() {
        //Log.d("스와이프터치리스너", "onSwipeRight: ");
    }

    boolean isMoving = false;

    public boolean onTouch(View v, MotionEvent event) {
        //Log.d("스와이프터치리스너", "onTouch: ");

            switch (event.getAction()) {

                case MotionEvent.ACTION_MOVE:
                    //Log.d("스와이프터치리스너", "ACTION_MOVE: ");
                    isMoving = true;


                    float eventNum = event.getOrientation();
                    float xNum = event.getX();
                    float yNum = event.getY();


//                    Log.d("스와이프터치리스너", "event.getOrientation(): "+eventNum);
//                    Log.d("스와이프터치리스너", "getX"+xNum+"getY"+yNum);
                    //this.onSwipeLeft();

                    // implement your move codes
                    break;

                case MotionEvent.ACTION_UP:
                   // Log.d("스와이프터치리스너", "ACTION_UP: ");
                    isMoving = false;
                    this.onClick(v);
                    break;

                default:
                    break;
            }
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            //Log.d("스와이프터치리스너", "onDown: ");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0) {
                    //Log.d("스와이프터치리스너", "onFling: 오른쪽");
                    onSwipeRight();
                }else {
                    //Log.d("스와이프터치리스너", "onFling: 왼쪽");
                    onSwipeLeft();
                    return true;
                }
            }
            return false;
        }
    }

    public void onClick(View v) {
        //Log.d("스와이프터치리스너", "onClick: ");
    }
}