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
    private int dx, dy;
    private ScaleGestureDetector mScaleDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        mScaleDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                drawView.setScaleFactor(detector.getScaleFactor());
                drawView.invalidate();
                return false;
            }
        });
        root = (ViewGroup) findViewById(R.id.linearlayout1);
        drawView = new DrawView(this);
        drawView.setBackgroundColor(Color.WHITE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        drawView.setLayoutParams(layoutParams);
        root.addView(drawView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) drawView.getLayoutParams();
                dx = X - lParams.leftMargin;
                dy = Y - lParams.topMargin;
            }
            break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE: {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) drawView.getLayoutParams();
                layoutParams.leftMargin = X - dx;
                layoutParams.topMargin = Y - dy;
                drawView.setLayoutParams(layoutParams);
                break;
            }
        }
        return true;
    }

}
