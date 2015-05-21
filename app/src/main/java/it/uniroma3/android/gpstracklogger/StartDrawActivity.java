package it.uniroma3.android.gpstracklogger;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import it.uniroma3.android.gpstracklogger.views.DrawView;


public class StartDrawActivity extends Activity {
    private DrawView drawView;
    ViewGroup root;
    private float lastX, lastY;
    private ScaleGestureDetector mScaleDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        mScaleDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleListener());
        root = (ViewGroup) findViewById(R.id.linearlayout1);
        drawView = new DrawView(this);
        drawView.setBackgroundColor(Color.WHITE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        drawView.setLayoutParams(layoutParams);
        root.addView(drawView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    final float x = event.getX();
                    final float y = event.getY();
                    drawView.setZoomCanvas(x, y);
                }
                drawView.invalidate();
            }
            mScaleDetector.onTouchEvent(event);
        }
        else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    final float x = event.getX();
                    final float y = event.getY();
                    lastX = x;
                    lastY = y;
                }
                break;
                case MotionEvent.ACTION_MOVE: {
                    final float x = event.getX();
                    final float y = event.getY();
                    drawView.translateCanvas(x-lastX, y-lastY);
                    break;
                }
            }
            drawView.invalidate();
        }
        return true;
    }


    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = drawView.getScaleFactor();
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            drawView.setScaleFactor(mScaleFactor);
            drawView.invalidate();
            return true;
        }
    }

}
