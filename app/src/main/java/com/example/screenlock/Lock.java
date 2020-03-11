package com.example.screenlock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TODO: document your custom view class.
 */
public class Lock extends View {
    private int primaryColor;
    private Paint paint;
    private List<Float> X,Y;
    private int centersTouched;
    private Pair<Float,Float> current;
    private int choice;
    private boolean touched[][]=new boolean[3][3];
    private String pattern,password;
    public Lock(Context context) {
        super(context);
        init();
    }

    public Lock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Lock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // Load attributes
        primaryColor=Color.DKGRAY;
        choice=0;
        paint=new Paint();
        X=new ArrayList<>();
        Y=new ArrayList<>();
        centersTouched=0;
        current=new Pair<>(0f,0f);
        password="03678";//hard-coded pattern for password
        pattern="";
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                touched[i][j]=false;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                paint.setColor(primaryColor);
                canvas.drawCircle(100f+i*200f,100f+j*200f,40f,paint);
                paint.setColor(Color.WHITE);
                canvas.drawCircle(100f+200f*i,100f+200f*j,20f,paint);
            }
        }
        paint.setColor(primaryColor);
        for(int i=0;i<centersTouched;i++)//Touched centers
            canvas.drawCircle(X.get(i),Y.get(i),20f,paint);

        paint.setStrokeWidth(20f);
        for(int i=0;i<centersTouched-1;i++)//Lines b/w touched centers
            canvas.drawLine(X.get(i),Y.get(i),X.get(i+1),Y.get(i+1),paint);

        switch(choice){
            case 1:
                if(centersTouched>=2) canvas.drawLine(X.get(centersTouched-2),Y.get(centersTouched-2),X.get(centersTouched-1),Y.get(centersTouched-1),paint);
                break;
            case 2:
                if(centersTouched>0) canvas.drawLine(X.get(centersTouched-1),Y.get(centersTouched-1),current.first,current.second,paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action=motionEvent.getAction();
        float x=motionEvent.getX();
        float y=motionEvent.getY();

        float nearX=near(x);
        float nearY=near(y);
        float dist=(float)Math.sqrt(Math.pow(nearX-x,2)+(float)Math.pow(nearY-y,2));

        switch(action)
        {
            case MotionEvent.ACTION_DOWN:
                if(x>600f || y>600f) return true;
                else if(dist<=40 && !touched[ind(nearX)][ind(nearY)])
                {
                    choice=1;
                    touched[ind(nearX)][ind(nearY)]=true;
                    addCenter(nearX,nearY);
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                choice=0;
                if(pattern.equals(password)&&centersTouched>0){
                    primaryColor=Color.GREEN;
                    invalidate();
                    Toast.makeText(getContext(),"Correct !!",Toast.LENGTH_SHORT).show();
                }
                else if(centersTouched>0){
                    primaryColor=Color.RED;
                    invalidate();
                    Toast.makeText(getContext(),"Incorrect !!",Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        init();
                        invalidate();
                    }
                },4000);
                break;

            case MotionEvent.ACTION_MOVE:
                if(dist<=40 && x<=600f && y<=600f) {
                    if( !touched[ind(nearX)][ind(nearY)] ) {
                        touched[ind(nearX)][ind(nearY)] = true;

                        if(Math.abs(nearX-X.get(centersTouched-1))>1f || Math.abs(nearY-Y.get(centersTouched-1))>1f)
                        {
                            float avgX=(nearX+X.get(centersTouched-1))/2;
                            float avgY=(nearY+Y.get(centersTouched-1))/2;
                            if(!touched[ind(avgX)][ind(avgY)])
                            {
                                touched[ind(avgX)][ind(avgY)]=true;
                                addCenter(avgX,avgY);
                            }
                        }
                        addCenter(nearX,nearY);
                    }
                    choice=1;
                }
                else {
                    current=new Pair<>(x,y);
                    choice = 2;
                }
                invalidate();
                break;
        }
        return true;
    }
    protected int ind(float z)
    {
        return ((int)z-100)/200;
    }
    protected float near(float z)
    {
        return (((int)z)/200)*200+100f;
    }
    protected void addCenter(float x,float y)
    {
        X.add(x);
        Y.add(y);
        pattern+=((((int)x-100)/200)+((int)y-100)*3/200);
        centersTouched++;
    }
}
