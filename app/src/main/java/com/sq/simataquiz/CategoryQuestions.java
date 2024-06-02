package com.sq.simataquiz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sq.simataquiz.databinding.ActivityCategoryQuestionsBinding;

import java.util.ArrayList;

public class CategoryQuestions extends AppCompatActivity {

    TextView category;
    TextView categoryTotal;
    int[] class_questions = {16,41,54,81,87,115,216,223,242,257,261,270,292,305,325,339,401,363,413,649,691,706,762,804,824};
    ArrayList<String> question_ids;
    String categoryString;
    ImageButton backBtn;
    ImageButton homeBtn;
    private View decorView;
    MediaPlayer mediaPlayer;
    private AdView mAdView;

    ActivityCategoryQuestionsBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(i -> {
            if(i==0){
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        mAdView = findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        category = findViewById(R.id.categoryText);
        categoryTotal = findViewById(R.id.categoryTotal);
        categoryString = getResources().getStringArray(R.array.categories)[getIntent().getIntExtra("data",0)];
        category.setText(categoryString);
        mediaPlayer = MediaPlayer.create(this,R.raw.bubble);


        question_ids = new ArrayList<>();
        create_table(getIntent().getIntExtra("data",0));

        categoryTotal.setText(question_ids.size() + " Ερωτήσεις");
        backBtn = findViewById(R.id.imgBtn);
        homeBtn = findViewById(R.id.homeBtn);

        CustomGridAdapter adapter = new CustomGridAdapter(CategoryQuestions.this, question_ids);
        binding.gridView.setAdapter(adapter);


        binding.gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            mediaPlayer.start();
            Intent intent = new Intent(CategoryQuestions.this, StudyActivity.class);
            intent.putExtra("state", getIntent().getIntExtra("state", 0));
            intent.putExtra("data", getIntent().getIntExtra("data",0));
            intent.putExtra("continue", i);
            startActivity(intent);
        });

        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(CategoryQuestions.this, Categories.class);
            intent.putExtra("state", getIntent().getIntExtra("state",0));
            startActivity(intent);
        });

        homeBtn.setOnClickListener(view -> startActivity(new Intent(CategoryQuestions.this, MainActivity.class)));

    }

    public void create_table(int num) {
        int start = 0;
        if(num != 0 ){
            start = class_questions[num-1] + 1;
        }
        for(int i = start ; i <= class_questions[num]; i ++){
            question_ids.add(categoryString + " " + i);
        }
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

    private static class CustomGridAdapter extends BaseAdapter {
        Context context;
        ArrayList<String> list;

        LayoutInflater inflater;

        public CustomGridAdapter(Context context, ArrayList<String> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            if(inflater == null){
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            if( convertView == null){
                convertView = inflater.inflate(R.layout.grid_item,null);
            }
            TextView textView = convertView.findViewById(R.id.gridTextView);
            textView.setText(list.get(i));

            return convertView;
        }
    }
}