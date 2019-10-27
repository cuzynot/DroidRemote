package com.example.droidremoteclient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

public class AnimationView extends View {

    // init vars
    private int width;
    private int height;

    private Paint paint, cornerPaint;
    private final int CORNER_RADIUS = 200;

    private int backgroundColour;
    private int drawColour;

    private int px, py;

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

        cornerPaint = new Paint();
        cornerPaint.setColor(Color.BLACK);
        cornerPaint.setStrokeWidth(CORNER_RADIUS);
        cornerPaint.setStyle(Paint.Style.STROKE);

        // set up paint
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);


        width = Integer.MAX_VALUE;
        height = Integer.MAX_VALUE;

        // get background colour of view
        backgroundColour = Color.rgb(18, 18, 18);
        drawColour = Color.rgb(87, 129, 169);

        px = 0;
        py = 0;
    }

    private void drawCorner(int cx, int cy, int startAngle, int sweepAngle, Canvas canvas){
        RectF oval = new RectF();
        oval.set(cx - CORNER_RADIUS, cy - CORNER_RADIUS, cx + CORNER_RADIUS, cy + CORNER_RADIUS);
        canvas.drawArc(oval, startAngle, sweepAngle, false, cornerPaint);
    }

    // public methods
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // if connected
        if (MainActivity.connected) {

            if (width != Integer.MAX_VALUE && height != Integer.MAX_VALUE) {
                // save rest of canvas first
//                canvas.save();

                // reset canvas
                paint.setColor(backgroundColour);
                canvas.drawRect(0, 0, width, height, paint);

                // draw background circle
//                paint.setColor(drawColour);
//                canvas.drawCircle(width / 2, height / 2, Math.min(width, height) / 2, paint);


                // draw complementary rectangle
                paint.setColor(drawColour);


                int ny = -(int) (height * MainActivity.orientationAngles[1] * 2 / Math.PI);
                int nx = (int) (width * MainActivity.orientationAngles[2] * 2 / Math.PI);

                int y = py;
                int dy = Math.abs(ny - py);
                if (dy > 1){
                    if (ny > py) {
                        y += (int)(Math.log(dy) / Math.log(2));
                    } else {
                        y -= (int)(Math.log(dy) / Math.log(2));
                    }
                }

                int x = px;
                int dx = Math.abs(nx - px);
                if (dx > 1){
                    if (nx > px){
                        x += (int)(Math.log(dx) / Math.log(2));
                    } else {
                        x -= (int)(Math.log(dx) / Math.log(2));
                    }
                }


//                boolean drawOnLeft = true;
//                boolean drawOnBottom = true;

                if (x > 0){
                    canvas.drawRect(width - x, 0, width, height, paint);
                } else if (x < 0){
//                    x += width;
//                    drawOnLeft = !drawOnLeft;
                    canvas.drawRect(0, 0, -x, height, paint);
                }

                if (y > 0){
                    canvas.drawRect(0, height - y, width, height, paint);
                } else if (y < 0){
//                    y += height;
//                    drawOnBottom = !drawOnLeft;
                    canvas.drawRect(0, 0, width, -y, paint);
                }

                px = x;
                py = y;


                // draw text
                paint.setColor(Color.WHITE);
                canvas.drawText(x + " " + y, 200, 200, paint);



                // draw corners
                cornerPaint.setColor(backgroundColour);


                float cx = CORNER_RADIUS / 2;
                float cy = CORNER_RADIUS / 2;

                drawCorner(CORNER_RADIUS / 2, CORNER_RADIUS / 2, 180, 90, canvas); // top left
                drawCorner(width - CORNER_RADIUS / 2, CORNER_RADIUS / 2, 270, 90, canvas); // top right
                drawCorner(width - CORNER_RADIUS / 2, height - CORNER_RADIUS / 2, 0, 90, canvas); // bottom right
                drawCorner(CORNER_RADIUS / 2, height - CORNER_RADIUS / 2, 90, 90, canvas); // bottom left

            } else {
                width = getWidth();
                height = getHeight();
            }
        }

        // redraw canvas
        invalidate();
    }
}
