package com.karth.aptitudetrainer;

import java.io.Serializable;

public class Question implements Serializable {
    public final String difficulty;
    public final String text;
    public final String[] options;
    public final int answerIndex;
    public final String hint;
    public final String explanation;
    public final String company;
    public final int year;
    public final String testName;

    public Question(String difficulty, String text, String[] options, int answerIndex,
                    String hint, String explanation, String company, int year, String testName) {
        this.difficulty = difficulty;
        this.text = text;
        this.options = options;
        this.answerIndex = answerIndex;
        this.hint = hint;
        this.explanation = explanation;
        this.company = company;
        this.year = year;
        this.testName = testName;
    }

    public String sourceLabel() {
        return "(" + company + ", " + year + " • " + testName + ")";
    }

    public String stableId() {
        return difficulty + "|" + company + "|" + year + "|" + testName + "|" + text;
    }
}
