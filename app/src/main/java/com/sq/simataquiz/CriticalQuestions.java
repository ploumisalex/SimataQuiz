package com.sq.simataquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CriticalQuestions extends AppCompatActivity {

    JSONMYClass jsonmyClass;
    JSONArray criticalQuestions;
    ArrayList<String> criticalQuestionIds;
    ArrayList<Integer> criticalQuestionNums;
    ListView critList;

    ImageButton homeBtn,backBtn;
    Button deleteAll;
    TextView tvTotal;
    CriticalDB db;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_critical_questions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(i -> {
            if(i==0){
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        });


        backBtn = findViewById(R.id.imgBtn);
        homeBtn = findViewById(R.id.homeBtn);
        deleteAll = findViewById(R.id.deleteAll);
        tvTotal = findViewById(R.id.totalQs);

        backBtn.setOnClickListener(view -> startActivity(new Intent(CriticalQuestions.this, MainActivity.class)));

        homeBtn.setOnClickListener(view -> startActivity(new Intent(CriticalQuestions.this, MainActivity.class)));

        jsonmyClass = new JSONMYClass();
        db = new CriticalDB(CriticalQuestions.this);
        criticalQuestions = new JSONArray();
        critList = findViewById(R.id.criticalList);

        get_question_ids();


        CriticalQuestions.ListAdapter listAdapter = new CriticalQuestions.ListAdapter(CriticalQuestions.this,criticalQuestionNums);
        critList.setAdapter(listAdapter);

        deleteAll.setOnClickListener(view -> {
            db.deleteALL();
            get_question_ids();
            ListAdapter listAdapter1 = new ListAdapter(CriticalQuestions.this,criticalQuestionNums);
            critList.setAdapter(listAdapter1);
        });


    }

    public void get_question_ids(){
        criticalQuestionIds = new ArrayList<>();
        criticalQuestionNums = new ArrayList<>();
        Cursor cursor = db.get_nums();
        if(cursor.getCount() != 0){
            while(cursor.moveToNext()){
                criticalQuestionIds.add(cursor.getString(0));
                criticalQuestionNums.add(cursor.getInt(1));
            }
        }
        tvTotal.setText("Σύνολο ερωτήσεων: " + criticalQuestionNums.size());
    }

    private class ListAdapter extends ArrayAdapter<Integer> {

        public ListAdapter(Context context, ArrayList<Integer> wrongQuestions) {
            super(context, R.layout.answers_results,R.id.listItemId,wrongQuestions);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            JSONObject tempObject = jsonmyClass.get_json_question_overall(getItem(position));

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.answers_results,parent,false);
            }

            TextView textView = convertView.findViewById(R.id.questiontextR);
            TextView qCategory = convertView.findViewById(R.id.listItemCategory);
            ImageView imgView = convertView.findViewById(R.id.questionimgR);
            Button[] optionButtons = new Button[3];
            optionButtons[0] = convertView.findViewById(R.id.option1btnR);
            optionButtons[1] = convertView.findViewById(R.id.option2btnR);
            optionButtons[2] = convertView.findViewById(R.id.option3btnR);
            Button deleteBtn = convertView.findViewById(R.id.deleteBtn);

            try {
                qCategory.setText(getResources().getStringArray(R.array.categories)[jsonmyClass.get_question_category(criticalQuestionNums.get(position))]);
                textView.setText(tempObject.getString("question"));
                if(!TextUtils.isEmpty(tempObject.getString("img"))){
                    imgView.setImageResource(getApplicationContext().getResources().getIdentifier(tempObject.getString("img"), "drawable", getApplicationContext().getPackageName()));
                }
                else{
                    imgView.setVisibility(View.GONE);
                }
                String[] options = new String[3];
                options[0] = tempObject.getString("option1");
                options[1] = tempObject.getString("option2");
                options[2] = tempObject.getString("option3");
                for(int j = 0 ; j < 3; j ++){
                    if(!TextUtils.isEmpty(options[j])){
                        optionButtons[j].setText(options[j]);
                        optionButtons[j].setVisibility(View.VISIBLE);
                        optionButtons[j].setBackgroundColor(optionButtons[j].getContext().getResources().getColor(R.color.btnbg));
                    }
                    else{
                        optionButtons[j].setVisibility(View.INVISIBLE);
                    }
                }
                optionButtons[tempObject.getInt("correct")].setBackgroundColor(optionButtons[tempObject.getInt("correct")].getContext().getResources().getColor(R.color.correct));
                deleteBtn.setVisibility(View.VISIBLE);
                deleteBtn.setOnClickListener(view -> {
                    db.delete_question(criticalQuestionIds.get(position));
                    get_question_ids();
                    ListAdapter listAdapter = new ListAdapter(CriticalQuestions.this,criticalQuestionNums);
                    critList.setAdapter(listAdapter);
                });
                } catch (JSONException e) {
                e.printStackTrace();
            }

            return super.getView(position, convertView, parent);
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


}