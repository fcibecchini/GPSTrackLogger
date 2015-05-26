package it.uniroma3.android.gpstracklogger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ZoomControls;

import it.uniroma3.android.gpstracklogger.views.DrawView;
import it.uniroma3.android.gpstracklogger.views.ScaleView;


public class StartDrawActivity extends Activity {
    private DrawView drawView;
    private ScaleView scaleView;
    ViewGroup root, scaleRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        root = (ViewGroup) findViewById(R.id.drawlayout);
        scaleRoot = (ViewGroup) findViewById(R.id.drawscale);
        drawView = new DrawView(this);
        scaleView = new ScaleView(this);
        root.addView(drawView);
        scaleRoot.addView(scaleView);
        showScala();
        Button fit = (Button) findViewById(R.id.fit);
        ZoomControls zoom = (ZoomControls) findViewById(R.id.zoomControls);
        fit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fit();
            }
        });
        zoom.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomIn();
            }
        });
        zoom.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomOut();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        drawView.invalidate();
    }

    private void fit() {
        drawView.fit();
        drawView.invalidate();
        showScala();
    }

    private void zoomIn() {
        if (scaleView.getXMetersPerInch()>10 &&
                scaleView.getYMetersPerInch()>10)
            drawView.setScala(2);
        showScala();
        drawView.invalidate();
    }

    private void zoomOut() {
        if (scaleView.getXMetersPerInch()<100000 &&
                scaleView.getYMetersPerInch()<100000)
            drawView.setScala(0.5);
        showScala();
        drawView.invalidate();
    }

    private void showScala() {
        scaleView.invalidate();
    }

}
