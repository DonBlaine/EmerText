package com.example.don.emertext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class DrawNotifyScreen extends AppCompatActivity {

    // contained locally to the class
    private RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_notify_screen);
        rl = (RelativeLayout) findViewById(R.id.activity_draw_notify_screen);
        //adding a new view with our canvas and paint
        rl.addView(new MyView(this));
    }

    class MyView extends View {

        //variables defined for storing touch points
        float x,y;
        // creating a paint object to enable painting on the canvas
        Paint paint = new Paint();
        public MyView(Context context) {
            super(context);
            // color of paint object
            paint.setColor(Color.RED);
            // width of brush
            paint.setStrokeWidth(10);
            //style of paint
            paint.setStyle(Paint.Style.STROKE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Bitmap b= BitmapFactory.decodeResource(getResources(), R.drawable.my_img);
            // 0, 0 represents the matrix position of the image
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

            int h = displaymetrics.heightPixels;
            int w = displaymetrics.widthPixels;

            h = h - 400;

            Bitmap resized = Bitmap.createScaledBitmap(b,w,h,true);
            canvas.drawBitmap(resized, 0, 0, paint);
            canvas.drawCircle(x, y, 30, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                // action down since that is what the system action code is on first touch
                case MotionEvent.ACTION_DOWN:
                    // getting the x y coordinates returned by the system.
                    x = event.getX();
                    y = event.getY();

            }
            invalidate();
            return true;

        }

    }

}
