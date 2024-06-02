package com.sq.simataquiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class StudyActivity extends AppCompatActivity {

    public TextView questionText;
    public TextView questionNum;
    public ImageView questionImg;
    public Button nextQuestionBtn;
    public Button prevQuestionBtn;
    public Button[] optionButtons = new Button[3];
    Question tempQuestion;

    // 0 = study/ 1 = test / 2 = random / 3 = exams
    int current_state = 0;
    JSONMYClass jsonmyClass;

    int data;
    JSONArray questionsArray;
    int jsonArrayLength = 0;
    int currentIndex = 0;
    int correctAnswers = 0;
    int totalAnswers = 0;

    ArrayList<Integer> wrongAnswers;
    ArrayList<Integer> wrongQuestions;

    ImageButton backBtn;
    ImageButton homeBtn;
    private View decorView;

    CriticalDB db;

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(i -> {
            if(i==0){
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        });
        MobileAds.initialize(this, initializationStatus -> {
        });

        jsonmyClass = new JSONMYClass();
        db = new CriticalDB(StudyActivity.this);

        optionButtons[0] = findViewById(R.id.option1btn);
        optionButtons[1] = findViewById(R.id.option2btn);
        optionButtons[2] = findViewById(R.id.option3btn);
        questionText = findViewById(R.id.questiontext);
        questionImg = findViewById(R.id.questionimg);
        questionNum = findViewById(R.id.questionNum);
        nextQuestionBtn = findViewById(R.id.nextquestionbtn);
        prevQuestionBtn = findViewById(R.id.previousquestionbtn);
        wrongAnswers = new ArrayList<>();
        wrongQuestions = new ArrayList<>();
        backBtn = findViewById(R.id.imgBtn);
        homeBtn = findViewById(R.id.homeBtn);

        current_state = getIntent().getIntExtra("state",0);
        currentIndex = getIntent().getIntExtra("continue",0);


        data = getIntent().getIntExtra("data",0);
        if(current_state == 2 || current_state == 3){
            questionsArray = jsonmyClass.get_x_random_questions(data);
        }
        else{
            questionsArray = jsonmyClass.get_json_category(data);
            prevQuestionBtn.setVisibility(View.VISIBLE);
        }
        jsonArrayLength = questionsArray.length();
        if(jsonArrayLength >= 19){
            loadAd();
        }

        prepare_next_question();

        for(int i = 0 ; i < optionButtons.length; i ++){
            int buttonId = i;
            optionButtons[i].setOnClickListener(view -> option_selected(buttonId));
        }

        nextQuestionBtn.setOnClickListener(view -> prepare_next_question());

        prevQuestionBtn.setOnClickListener(view -> {
            if(currentIndex > 1 ){
                currentIndex -= 2;
                prepare_next_question();
            }
        });

        backBtn.setOnClickListener(view -> {
            if(current_state == 2 || current_state == 3){
                startActivity(new Intent(StudyActivity.this, MainActivity.class));
            }else{
                Intent intent = new Intent(StudyActivity.this, CategoryQuestions.class);
                intent.putExtra("data", data);
                intent.putExtra("state", current_state);
                startActivity(intent);
            }
        });

        homeBtn.setOnClickListener(view -> startActivity(new Intent(StudyActivity.this, MainActivity.class)));

    }

    public void option_selected(int num){
        set_buttons_clickable(false);
        int correct_answer= tempQuestion.getCorrectAnswer();
        tempQuestion.setSelectedAnswer(num);
        if(num == correct_answer){
            correctAnswers++;
            if(current_state != 2 && current_state != 3){
                optionButtons[num].setBackgroundColor(optionButtons[num].getContext().getResources().getColor(R.color.correct));
                nextQuestionBtn.setVisibility(View.VISIBLE);
            }
            else{
                prepare_next_question();
            }
        }else {
            wrongAnswers.add(num);
            wrongQuestions.add(tempQuestion.getId());
            db.add_question_num(tempQuestion.getId());
            if(current_state != 2 && current_state != 3){
                optionButtons[num].setBackgroundColor(optionButtons[num].getContext().getResources().getColor(R.color.incorrect));
                optionButtons[correct_answer].setBackgroundColor(optionButtons[correct_answer].getContext().getResources().getColor(R.color.correct));
                nextQuestionBtn.setVisibility(View.VISIBLE);
            }
            else{
                prepare_next_question();
            }
        }
    }

    public void set_button_texts(){
        for(int i = 0 ; i < optionButtons.length; i ++){
            if(!TextUtils.isEmpty(tempQuestion.get_option(i))){
                optionButtons[i].setText(tempQuestion.get_option(i));
                optionButtons[i].setVisibility(View.VISIBLE);
                optionButtons[i].setBackgroundColor(optionButtons[i].getContext().getResources().getColor(R.color.btnbg));
            }
            else{
                optionButtons[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    public void set_buttons_clickable(boolean myBool){
        for (Button optionButton : optionButtons) {
            optionButton.setClickable(myBool);
        }
    }

    public void get_question_from_json_array(int index){
        try {
            JSONObject jsonQuestion = questionsArray.getJSONObject(index);
            String question = jsonQuestion.getString("question");
            String option1 = jsonQuestion.getString("option1");
            String option2 = jsonQuestion.getString("option2");
            String option3 = jsonQuestion.getString("option3");
            String img = jsonQuestion.getString("img");
            int correct = jsonQuestion.getInt("correct");
            int id = jsonQuestion.getInt("num");
            tempQuestion = new Question(id, question,img,option1,option2,option3,correct);
            if(!TextUtils.isEmpty(img)){
                questionImg.setImageResource(getApplicationContext().getResources().getIdentifier(img, "drawable", getApplicationContext().getPackageName()));
            }
            else{
                questionImg.setImageResource(android.R.color.transparent);
            }
            questionText.setText(tempQuestion.getQuestion());
            questionNum.setText("Ερώτηση " + (currentIndex + 1) + " από " + jsonArrayLength);
            set_button_texts();
            set_buttons_clickable(current_state != 0);
            if(current_state != 0){
                nextQuestionBtn.setVisibility(View.INVISIBLE);
            }
            else{
                nextQuestionBtn.setVisibility(View.VISIBLE);
                correctAnswers++;
                for(int j = 0; j < 3; j ++){
                    if(j == correct){
                        optionButtons[j].setBackgroundColor(optionButtons[j].getContext().getResources().getColor(R.color.correct));
                    }
                    else{
                        optionButtons[j].setBackgroundColor(optionButtons[j].getContext().getResources().getColor(R.color.btnbg));
                    }
                }
            }
            currentIndex++;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void prepare_next_question(){
        totalAnswers++;
        if(currentIndex < jsonArrayLength){
            get_question_from_json_array(currentIndex);
        }
        else{
            if(totalAnswers >= 19){
                showInterstitial();
            }else{
                toResultsActivity();
            }
        }
    }

    public void toResultsActivity(){
        Intent intent = new Intent(StudyActivity.this, ResultsActivity.class);
        intent.putExtra("correct", correctAnswers);
        intent.putExtra("total", jsonArrayLength);
        if(current_state == 2 || current_state == 3){
            intent.putIntegerArrayListExtra("wrongQuestions", wrongQuestions);
            intent.putIntegerArrayListExtra("wrongAnswers", wrongAnswers);
        }else{
            intent.putExtra("category", data);
        }
        intent.putExtra("state", current_state);
        startActivity(intent);
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
    //getResources().getString(R.string.app_id_interstitial)
    //ca-app-pub-3940256099942544/1033173712
    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,getResources().getString(R.string.app_id_interstitial),adRequest,new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        StudyActivity.this.interstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        StudyActivity.this.interstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                        toResultsActivity();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        StudyActivity.this.interstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        interstitialAd = null;
                    }
                });
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
            toResultsActivity();
        }
    }

}