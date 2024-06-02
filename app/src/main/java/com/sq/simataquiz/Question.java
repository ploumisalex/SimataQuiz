package com.sq.simataquiz;

public class Question {
    int id;
    String question;
    String image;
    String option1;
    String option2;
    String option3;
    int correctAnswer;
    int selectedAnswer = -1;

    public Question(int id, String question, String image, String option1, String option2, String option3, int correctAnswer) {
        this.id = id;
        this.question = question;
        this.image = image;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.correctAnswer = correctAnswer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(int selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public boolean check_for_correct_answer(int i){
        if(i == this.correctAnswer){
            return true;
        }
        else {
            return false;
        }
    }

    public String get_option(int num){
        if( num == 0 ){
            return this.option1;
        }
        else if( num == 1 ){
            return this.option2;
        }
        else{
            return this.option3;
        }
    }
}
