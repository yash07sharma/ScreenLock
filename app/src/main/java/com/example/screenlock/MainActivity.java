package com.example.screenlock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Lock lock;
    PatternSet patternSet;
    Button reset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        lock=findViewById(R.id.lock);
        patternSet=findViewById(R.id.patternSet);
        reset=findViewById(R.id.reset);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patternSet.init();
                patternSet.setVisibility(View.VISIBLE);
                lock.setVisibility(View.INVISIBLE);
                reset.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        Toast.makeText(MainActivity.this,patternSet.getPattern(),Toast.LENGTH_SHORT).show();
        patternSet.setVisibility(View.INVISIBLE);
        lock.setPassword(patternSet.getPattern());
        lock.init();
        lock.setVisibility(View.VISIBLE);
        reset.setVisibility(View.VISIBLE);
        return super.onTouchEvent(motionEvent);
    }
}
