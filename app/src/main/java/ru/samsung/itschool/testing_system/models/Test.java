package ru.samsung.itschool.testing_system.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Test implements Serializable {
    private String name;
    private ArrayList<Question> questions;
    private int timeLimit;

    public Test(String name, ArrayList<Question> questions, int timeLimit) {
        this.name = name;
        this.questions = questions;
        this.timeLimit = timeLimit;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

}
