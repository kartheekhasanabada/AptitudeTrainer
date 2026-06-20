package com.karth.aptitudetrainer;

import java.io.Serializable;

public class Question implements Serializable {
    public final String difficulty;
    public final String text;
    public final String[] options;
    public final int answerIndex;
    public final String hint;
    public final String explanation;

    public Question(String difficulty, String text, String[] options, int answerIndex, String hint, String explanation) {
        this.difficulty = difficulty;
        this.text = text;
        this.options = options;
        this.answerIndex = answerIndex;
        this.hint = hint;
        this.explanation = explanation;
    }
}
