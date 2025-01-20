package ru.samsung.itschool.testing_system.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {
    private String text;
    private ArrayList<Answer> answers;
    private int points;
    private String imageUri;

    public Question(String text, int points) {
        this.text = text;
        this.points = points;
        this.answers = new ArrayList<>();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getOptionCount() {
        return this.answers.size();
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}

