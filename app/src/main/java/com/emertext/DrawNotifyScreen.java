package com.emertext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
// Activity to display a body sketch that enables user to point out area of injury
public class DrawNotifyScreen extends AppCompatActivity {
    // contained locally to the class
    private RelativeLayout rl;

    Bitmap b;
    DisplayMetrics displayMetrics;

    @Override
    // creates the bitmap and add view to the screen
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_notify_screen);
        rl = (RelativeLayout) findViewById(R.id.imageHolder);
        //adding a new view with our canvas and paint
        rl.addView(new MyView(this));
        b=BitmapFactory.decodeResource(getResources(), R.drawable.my_img);
        // 0, 0 represents the matrix position of the image
        displayMetrics = new DisplayMetrics();

    }

    // Function to go back to text speech screen on click of button
    public void simonGoBack(View v){
        Intent i = new Intent(DrawNotifyScreen.this,TextSpeech.class);
        startActivity(i);
    }

    // defining class to manipulate view with canvas and paint objects
    class MyView extends View{

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

        // Drawing the touches on screen and resizing image in case screen size/ orientation changes
        @Override
        protected void onDraw(Canvas canvas) {

            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int h = rl.getHeight();
            int w = rl.getWidth();

            @SuppressLint("DrawAllocation")
            Bitmap resized = Bitmap.createScaledBitmap(b,w,h,true);
            canvas.drawBitmap(resized, 0, 0, paint);
            canvas.drawCircle(x, y, 30, paint);
        }

        // getting the x,y co-ordinates of screen where the user touches
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
