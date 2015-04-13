package com.H2TFC.H2T_DMS_MANAGER.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by c4sau on 30/03/2015.
 */
public class CircleView extends View {

    private static final String TAG = "CircleView";
    private static final double MOVE_SENSITIVITY = 1.25;
    private Paint circlePaint;
    private boolean isPinchMode;
    private float lastCircleX;
    private float lastCircleY;
    public Circle circle;
    private boolean isDoneResizing = true;

    public CircleView(Context context) {
        super(context);
        setCirclePaint(0x220000ff);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCirclePaint(0x220000ff);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCirclePaint(0x220000ff);
    }

    private void setCirclePaint(int color) {
        circle = new Circle();
        circlePaint = new Paint();
        circlePaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(circle.centerX, circle.centerY, circle.radius, circlePaint);
    }
    public float offsetX;
    public float offsetY;

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        int historySize;
        double lastDistance;
        double oneBeforeLastDistance;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                offsetX = circle.centerX - event.getRawX();
                offsetY = circle.centerY - event.getRawY();
                lastCircleX = circle.centerX ;
                lastCircleY = circle.centerY;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                isPinchMode = true;
                isDoneResizing = false;
                break;

            case MotionEvent.ACTION_MOVE:
                circle.centerX = lastCircleX;
                circle.centerY = lastCircleY;

                if (getTouchedCircle((int) event.getX(), (int) event.getY()) && !isPinchMode && isDoneResizing) {

                    historySize = event.getHistorySize();
                    if (historySize > 0) {

                        oneBeforeLastDistance = Math.sqrt((event.getX() - event.getHistoricalX(0, historySize - 1)) *
                                (event.getX() - event.getHistoricalX(0, historySize - 1)) +
                                (event.getY() - event.getHistoricalY(0, historySize - 1)) *
                                        (event.getY() - event.getHistoricalY(0, historySize - 1)));


                        if (oneBeforeLastDistance > MOVE_SENSITIVITY) {
                            //circle.centerX = (int) event.getX();
                            //circle.centerY = (int) event.getY();
                            circle.centerX = event.getRawX() + offsetX;
                            circle.centerY = event.getRawY() + offsetY;
                            lastCircleX = circle.centerX;
                            lastCircleY = circle.centerY;

                        }
                    }
                }
                if (isPinchMode) {
                    circle.centerX = lastCircleX;
                    circle.centerY = lastCircleY;

                    historySize = event.getHistorySize();
                    if (historySize > 0) {

                        lastDistance = Math.sqrt((event.getX(0) - event.getX(1)) * (event.getX(0) - event.getX(1)) +
                                (event.getY(0) - event.getY(1)) * (event.getY(0) - event.getY(1)));

                        oneBeforeLastDistance = Math.sqrt((event.getHistoricalX(0, historySize - 1) - event.getHistoricalX(1, historySize - 1)) *
                                (event.getHistoricalX(0, historySize - 1) - event.getHistoricalX(1, historySize - 1)) +
                                (event.getHistoricalY(0, historySize - 1) - event.getHistoricalY(1, historySize - 1)) *
                                        (event.getHistoricalY(0, historySize - 1) - event.getHistoricalY(1, historySize - 1)));


                        if (lastDistance < oneBeforeLastDistance) {
                            circle.radius -= Math.abs(lastDistance - oneBeforeLastDistance);
                        } else {
                            circle.radius += Math.abs(lastDistance - oneBeforeLastDistance);
                        }
                    }
                }
                lastCircleX = circle.centerX;
                lastCircleY = circle.centerY;
                invalidate();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                circle.centerX = lastCircleX;
                circle.centerY = lastCircleY;
                isPinchMode = false;
                break;

            case MotionEvent.ACTION_UP:
                circle.centerX = lastCircleX;
                circle.centerY = lastCircleY;
                isPinchMode = false;
                isDoneResizing = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_HOVER_MOVE:
                break;



            default:
                super.onTouchEvent(event);
                break;

        }
        return true;
    }

    private Boolean getTouchedCircle(final int xTouch, final int yTouch) {
        if ((circle.centerX - xTouch) * (circle.centerX - xTouch) +
                (circle.centerY - yTouch) * (circle.centerY - yTouch)   <= circle.radius * circle.radius) {
            return true;
        } else {
            return false;
        }

    }

    static class Circle {
        float radius;
        float centerX;
        float centerY;

        Circle() {
            this.radius = 150;
            this.centerX = 378;
            this.centerY = 478;
        }
    }



}