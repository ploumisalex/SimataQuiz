package com.sq.simataquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.sq.simataquiz.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    int correct;
    int total;
    TextView resultsText;
    Button redirectButton;
    ListView myListView;
    ArrayList<Integer> wrongAnswers;
    ArrayList<Integer> wrongQuestions;
    JSONMYClass jsonmyClass;
    int current_state;
    ImageButton backBtn;
    ImageButton homeBtn;
    private View decorView;
    private AdView mAdView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(i -> {
            if(i==0){
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        });
        MobileAds.initialize(this, initializationStatus -> {});

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        jsonmyClass = new JSONMYClass();
        resultsText = findViewById(R.id.resultsText);
        redirectButton = findViewById(R.id.redirectButton);
        myListView = findViewById(R.id.myListView);
        backBtn = findViewById(R.id.imgBtn);
        homeBtn = findViewById(R.id.homeBtn);

        Intent intent = getIntent();
        correct = intent.getIntExtra("correct",0);
        total = intent.getIntExtra("total",0);
        current_state = intent.getIntExtra("state",0);

        if(current_state == 2 | current_state == 3){
            wrongAnswers = getIntent().getIntegerArrayListExtra("wrongAnswers");
            wrongQuestions = getIntent().getIntegerArrayListExtra("wrongQuestions");
            ListAdapter listAdapter = new ListAdapter(ResultsActivity.this,wrongQuestions);
            myListView.setAdapter(listAdapter);
            redirectButton.setText("Δοκίμασε ξανά");
            resultsText.setText(correct + " σωστές από " + total);
        }
        else if(current_state == 1){
            redirectButton.setText("Εξασκήσου σε άλλη κατηγορία");
            resultsText.setText(getResources().getString(R.string.complete_cat) + " " + getResources().getStringArray(R.array.categories)[getIntent().getIntExtra("category",0)]);
        }
        else{
            redirectButton.setText("Διάβασε άλλη κατηγορία");
            resultsText.setText(getResources().getString(R.string.complete_cat)+ " " + getResources().getStringArray(R.array.categories)[getIntent().getIntExtra("category",0)]);
        }

        redirectButton.setOnClickListener(view -> {
            if(current_state == 0){
                Intent intent1 = new Intent(ResultsActivity.this, Categories.class);
                intent1.putExtra("state", 0);
                startActivity(intent1);
            }
            else if(current_state == 1){
                Intent intent1 = new Intent(ResultsActivity.this, Categories.class);
                intent1.putExtra("state", 1);
                startActivity(intent1);
            }
            else if(current_state == 2){
                Intent intent1 = new Intent(ResultsActivity.this, StudyActivity.class);
                intent1.putExtra("state", 2);
                intent1.putExtra("data", 5);
                startActivity(intent1);
            }else{
                Intent intent1 = new Intent(ResultsActivity.this, StudyActivity.class);
                intent1.putExtra("state", 3);
                intent1.putExtra("data", 50);
                startActivity(intent1);
            }
        });

        backBtn.setOnClickListener(view -> startActivity(new Intent(ResultsActivity.this,MainActivity.class)));

        homeBtn.setOnClickListener(view -> startActivity(new Intent(ResultsActivity.this, MainActivity.class)));
    }

    private class ListAdapter extends ArrayAdapter<Integer>  {

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

            try {
                qCategory.setText(getResources().getStringArray(R.array.categories)[jsonmyClass.get_question_category(wrongQuestions.get(position))]);
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
                optionButtons[wrongAnswers.get(position)].setBackgroundColor(optionButtons[wrongAnswers.get(position)].getContext().getResources().getColor(R.color.incorrect));

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