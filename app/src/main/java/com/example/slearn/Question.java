package com.example.project;

import java.io.Serializable;


public class Question implements Serializable {
    String questionText;
    String optionA;
    String optionB;
    String optionC;
    String optionD;
    String correctAnswer;

    public Question(String questionText, String optionA, String optionB,
                    String optionC, String optionD, String correctAnswer) {
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
    }
}