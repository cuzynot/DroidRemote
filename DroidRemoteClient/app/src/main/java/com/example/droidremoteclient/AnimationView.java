package com.example.droidremoteclient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

public class AnimationView extends View {

    // init vars
    private int width;
    private int height;
    private Paint paint;
    private int backgroundColour;
    private int drawColour;
    //    private int prevAngle;

    // constructors
    public AnimationView(Context context) {
        super(context);
        init();
    }

    public AnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // private methods
    private void init(){

        paint = new Paint();

        // set up paint
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);


        width = Integer.MAX_VALUE;
        height = Integer.MAX_VALUE;

        // get background colour of view
        backgroundColour = Color.rgb(18, 18, 18);
        drawColour = Color.rgb(87, 129, 169);
    }

    // public methods
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // if connected
        if (MainActivity.connected) {

            if (width != Integer.MAX_VALUE && height != Integer.MAX_VALUE) {
                int y = -(int) (height * MainActivity.orientationAngles[1] * 2 / Math.PI);
                int x = (int) (width * MainActivity.orientationAngles[2] * 2 / Math.PI);

                boolean drawOnLeft = true;
//                boolean onTop = false;

                if (x < 0){
                    x += width;
                    drawOnLeft = !drawOnLeft;
                }
                if (y < 0){
                    y += height;
                    drawOnLeft = !drawOnLeft;
//                    onTop = true;
                }

                // determine angle
                double slope = 0;
                if (width / 2 - x != 0) { // if slope is not undefined
                    slope = (y - height / 2.0) / (width / 2.0 - x);
                }
                int angleDegrees = (int)((Math.PI / 2.0 - Math.atan(slope)) * 180 / Math.PI);


                // save rest of canvas first
                canvas.save();

                // draw background circle
                paint.setColor(drawColour);
//                canvas.drawCircle(width / 2, height / 2, Math.min(width, height) / 2, paint);

                // draw complementary rectangle
                paint.setColor(Color.WHITE);
//                canvas.rotate(angleDegrees, width / 2, height / 2);


//                if (slope != 0) {
//                    if (angleDegrees > 0) {
//                        canvas.drawRect(x, height / 2, width, height, paint);
//                        canvas.drawRect(x, height / 2, width, 0, paint);
//                    } else {
//                        canvas.drawRect(x, height / 2, 0, height, paint);
//                        canvas.drawRect(x, height / 2, 0, 0, paint);
//                    }
//                }
                canvas.drawLine(x, height / 2, width / 2, y, paint);

                canvas.restore();

                canvas.drawText(x + " " + y, 200, 200, paint);
            } else {
                width = getWidth();
                height = getHeight();
            }
        }

        // delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // redraw canvas
        invalidate();
    }
}
