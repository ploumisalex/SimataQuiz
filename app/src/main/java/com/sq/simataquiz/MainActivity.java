package com.sq.simataquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button studyButton;
    Button testButton;
    Button examsButton;
    Button r5ndomButton;
    Button criticalButton;
    Button informationButton;
    Button reviewBtn;
    JSONMYClass jsonmyClass;
     private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(i -> {
            if(i==0){
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        studyButton = findViewById(R.id.studybutton);
        testButton = findViewById(R.id.testsbutton);
        examsButton = findViewById(R.id.examsbutton);
        r5ndomButton = findViewById(R.id.r5ndombutton);
        criticalButton = findViewById(R.id.criticalBtn);
        informationButton = findViewById(R.id.infoBtn);
        reviewBtn = findViewById(R.id.reviewBtn);
        jsonmyClass = new JSONMYClass();
        jsonmyClass.initialize_json(getApplicationContext(),"questions.json");

        studyButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Categories.class);
            intent.putExtra("state", 0);
            startActivity(intent);
        });

        testButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Categories.class);
            intent.putExtra("state", 1);
            startActivity(intent);
        });

        r5ndomButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, StudyActivity.class);
            intent.putExtra("state", 2);
            intent.putExtra("data", 5);
            startActivity(intent);
        });

        examsButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, StudyActivity.class);
            intent.putExtra("state", 3);
            intent.putExtra("data", 30);
            startActivity(intent);
        });

        criticalButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, CriticalQuestions.class)));

        informationButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Information.class)));

        reviewBtn.setOnClickListener(view -> {
            final String myappPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + myappPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + myappPackageName)));
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }

    private int hideSystemBars(){
        return View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    }
}