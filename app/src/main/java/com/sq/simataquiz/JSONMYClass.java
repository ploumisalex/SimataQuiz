package com.sq.simataquiz;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class JSONMYClass {

    static JSONArray jsonarray ;
    int[] class_questions = {16,41,54,81,87,115,216,223,242,257,261,270,292,305,325,339,363,401,413,649,691,706,762,804,824};
    static boolean initialized = false;

    public void initialize_json(Context context, String fileName){
        if(!initialized){
            String jsonString;
            try {
                InputStream is = context.getAssets().open(fileName);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                jsonString = new String(buffer, "UTF-8");
                try {
                    jsonarray = new JSONArray(jsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            initialized = true;
        }
    }


    public JSONArray get_json_category(int index){
        JSONArray jsonArray = null;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonarray.getJSONObject(index);
            jsonArray = jsonObject.getJSONArray("questions");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public JSONObject get_json_question(int catIndex, int qIndex){
        JSONObject jsonObjectR = null;
        JSONObject jsonObject = null;
        JSONArray tempArray = null;
        try {
            jsonObject = jsonarray.getJSONObject(catIndex);
            tempArray = jsonObject.getJSONArray("questions");
            jsonObjectR = tempArray.getJSONObject(qIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectR;
    }

    public JSONObject get_json_question_overall(int num){
        JSONObject jsonObject = null;
        int qIndex;
        for(int i = 0 ; i < class_questions.length; i ++){
            if(num <= class_questions[i]){
                if(i == 0){
                    qIndex = num;
                }
                else{
                    qIndex = num - class_questions[i-1] - 1;
                }
                jsonObject = get_json_question(i,qIndex);
                break;
            }
        }
        return jsonObject;
    }

    public JSONArray get_x_random_questions(int x){
        JSONArray tempArray = new JSONArray();
        ArrayList<Integer> qNums = new ArrayList<Integer>();
        while(tempArray.length() < x){
            int randomNum = ThreadLocalRandom.current().nextInt(0, 824 + 1);
            if(!qNums.contains(randomNum)){
                qNums.add(randomNum);
                tempArray.put(get_json_question_overall(randomNum));
            }
        }
        return tempArray;
    }

    public Integer get_question_category(int num){
        for(int i = 0 ; i < class_questions.length; i ++){
            if(num <= class_questions[i]){
                return i;
            }
        }
        return 0;
    }
}
