package it.uniroma3.android.gpstracklogger.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.view.View;
import android.graphics.Point;

import it.uniroma3.android.gpstracklogger.application.AppSettings;
import it.uniroma3.android.gpstracklogger.application.Converter;
import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.application.Utilities;
import it.uniroma3.android.gpstracklogger.model.TrackPoint;

/**
 * Created by Fabio on 21/05/2015.
 */
public class ScaleView extends View {
    float mXOffset = 10;
    float mYOffset = 10;
    float mLineWidth = 3;
    int mTextSize = 25;

    boolean mIsLatitudeBar = AppSettings.isLatitudeBar();
    boolean mIsLongitudeBar = AppSettings.isLongitudeBar();

    float mXdpi;
    float mYdpi;
    private float yMetersPerInch;
    private float xMetersPerInch;

    public ScaleView(Context context) {
        super(context);
        mXdpi = context.getResources().getDisplayMetrics().xdpi;
        mYdpi = context.getResources().getDisplayMetrics().ydpi;
    }

    public float getYMetersPerInch() {
        return this.yMetersPerInch;
    }

    public float getXMetersPerInch() {
        return this.xMetersPerInch;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScaleBarPicture(canvas);
    }

    private void drawScaleBarPicture(Canvas canvas) {
        // We want the scale bar to be as long as the closest round-number miles/kilometers
        // to 1-inch at the latitude at the current center of the screen.

        final Paint barPaint = new Paint();
        barPaint.setColor(Color.BLACK);
        barPaint.setAntiAlias(true);
        barPaint.setStrokeWidth(mLineWidth);

        final Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(mTextSize);

        drawXMetric(canvas, textPaint, barPaint);

        drawYMetric(canvas, textPaint, barPaint);
    }

    private void drawXMetric(Canvas canvas, Paint textPaint, Paint barPaint) {
        Converter converter = Session.getConverter();

        if (converter != null) {
            TrackPoint p1 = converter.getTrackPoint(new Point((int) ((getWidth() / 2) - (mXdpi / 2)), getHeight() / 2));
            TrackPoint p2 = converter.getTrackPoint(new Point((int) ((getWidth() / 2) + (mXdpi / 2)), getHeight() / 2));

            xMetersPerInch = p1.distanceTo(p2);

            if (mIsLongitudeBar) {
                String xMsg = scaleBarLengthText(xMetersPerInch);
                Rect xTextRect = new Rect();
                textPaint.getTextBounds(xMsg, 0, xMsg.length(), xTextRect);

                int textSpacing = (int) (xTextRect.height() / 5.0);

                canvas.drawRect(mXOffset, mYOffset, mXOffset + mXdpi, mYOffset + mLineWidth, barPaint);
                canvas.drawRect(mXOffset + mXdpi, mYOffset, mXOffset + mXdpi + mLineWidth, mYOffset +
                        xTextRect.height() + mLineWidth + textSpacing, barPaint);

                if (!mIsLatitudeBar) {
                    canvas.drawRect(mXOffset, mYOffset, mXOffset + mLineWidth, mYOffset +
                            xTextRect.height() + mLineWidth + textSpacing, barPaint);
                }
                canvas.drawText(xMsg, (mXOffset + mXdpi / 2 - xTextRect.width() / 2),
                        (mYOffset + xTextRect.height() + mLineWidth + textSpacing), textPaint);
            }
        }
    }

    private void drawYMetric(Canvas canvas, Paint textPaint, Paint barPaint) {
        Converter converter = Session.getConverter();

        if (converter != null) {

            TrackPoint p1 = converter.getTrackPoint(new Point(getWidth() / 2,
                    (int) ((getHeight() / 2) - (mYdpi / 2))));
            TrackPoint p2 = converter.getTrackPoint(new Point(getWidth() / 2,
                    (int) ((getHeight() / 2) + (mYdpi / 2))));

            yMetersPerInch = p1.distanceTo(p2);

            if (mIsLatitudeBar) {
                String yMsg = scaleBarLengthText(yMetersPerInch);
                Rect yTextRect = new Rect();
                textPaint.getTextBounds(yMsg, 0, yMsg.length(), yTextRect);

                int textSpacing = (int) (yTextRect.height() / 5.0);

                canvas.drawRect(mXOffset, mYOffset, mXOffset + mLineWidth, mYOffset + mYdpi, barPaint);
                canvas.drawRect(mXOffset, mYOffset + mYdpi, mXOffset + yTextRect.height() +
                        mLineWidth + textSpacing, mYOffset + mYdpi + mLineWidth, barPaint);

                if (!mIsLongitudeBar) {
                    canvas.drawRect(mXOffset, mYOffset, mXOffset + yTextRect.height() +
                            mLineWidth + textSpacing, mYOffset + mLineWidth, barPaint);
                }

                float x = mXOffset + yTextRect.height() + mLineWidth + textSpacing;
                float y = mYOffset + mYdpi / 2 + yTextRect.width() / 2;

                canvas.rotate(-90, x, y);
                canvas.drawText(yMsg, x, y + textSpacing, textPaint);
            }
        }
    }

    private String scaleBarLengthText(float meters) {
        if ((meters - (int) meters) > 0.5)
            meters++;
        return Utilities.getFormattedDistance((int)meters, true);
    }

}
