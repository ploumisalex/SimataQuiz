package com.sq.simataquiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;


public class Categories extends AppCompatActivity {

    ListView listView;
    ArrayList<String> categories;
    ImageButton backBtn;
    ImageButton homeBtn;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(i -> {
            if(i==0){
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        categories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.categories)));
        listView = findViewById(R.id.listView);
        CustomAdapter adapter = new CustomAdapter(Categories.this,categories);
        listView.setAdapter(adapter);
        backBtn = findViewById(R.id.imgBtn);
        homeBtn = findViewById(R.id.homeBtn);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(Categories.this, CategoryQuestions.class);
            intent.putExtra("data", i);
            intent.putExtra("state", getIntent().getIntExtra("state", 0));
            startActivity(intent);
        });

        backBtn.setOnClickListener(view -> startActivity(new Intent(Categories.this, MainActivity.class)));

        homeBtn.setOnClickListener(view -> startActivity(new Intent(Categories.this, MainActivity.class)));

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

    private class CustomAdapter extends ArrayAdapter<String>{

        String[] category_questions = getResources().getStringArray(R.array.category_question_numbers);
        ArrayList<String> categories;


        public CustomAdapter(Context context, ArrayList<String> myArrayList) {
            super(context, R.layout.categories_layout,R.id.listItemId,myArrayList);
            this.categories = myArrayList;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.categories_layout,parent,false);
            }
            TextView category_textview = convertView.findViewById(R.id.category_textview);
            TextView category_questions_textview = convertView.findViewById(R.id.question_number_textview);

            category_textview.setText(categories.get(position));
            category_questions_textview.setText(category_questions[position]);



            return convertView;
        }
    }
}