package com.example.project;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;


public class StudentQuizActivity extends AppCompatActivity {
    private TextView questionText;
    private RadioGroup optionsRadioGroup;
    private RadioButton radioOptionA, radioOptionB, radioOptionC, radioOptionD;
    private Button submitButton;
    private ArrayList<Question> quizQuestions;
    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_quizz);

        // Initialize views
        initializeViews();
        // Load questions from database
        loadQuestions();
        // Display first question
        displayQuestion(currentQuestionIndex);

        // Set up submit button
        submitButton.setOnClickListener(v -> handleSubmission());
    }

    private void initializeViews() {
        questionText = findViewById(R.id.questionText);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        radioOptionA = findViewById(R.id.radioOptionA);
        radioOptionB = findViewById(R.id.radioOptionB);
        radioOptionC = findViewById(R.id.radioOptionC);
        radioOptionD = findViewById(R.id.radioOptionD);
        submitButton = findViewById(R.id.submitButton);
    }

    private void loadQuestions() {
        // TODO: Replace with your actual database loading logic
        quizQuestions = new ArrayList<>();

    }

    private void displayQuestion(int index) {
        if (index < quizQuestions.size()) {
            Question currentQuestion = quizQuestions.get(index);

            // Display question
            questionText.setText(currentQuestion.questionText);

            // Set options
            radioOptionA.setText(currentQuestion.optionA);
            radioOptionB.setText(currentQuestion.optionB);
            radioOptionC.setText(currentQuestion.optionC);
            radioOptionD.setText(currentQuestion.optionD);

            // Clear previous selection
            optionsRadioGroup.clearCheck();
        }
    }

    private void handleSubmission() {
        // Check if an option is selected
        if (optionsRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        Question currentQuestion = quizQuestions.get(currentQuestionIndex);
        String selectedAnswer = "";

        // Determine which option was selected
        if (radioOptionA.isChecked()) selectedAnswer = "A";
        else if (radioOptionB.isChecked()) selectedAnswer = "B";
        else if (radioOptionC.isChecked()) selectedAnswer = "C";
        else if (radioOptionD.isChecked()) selectedAnswer = "D";

        // Check if answer is correct
        if (selectedAnswer.equals(currentQuestion.correctAnswer)) {
            score++;
        }

        // Move to next question or finish quiz
        currentQuestionIndex++;
        if (currentQuestionIndex < quizQuestions.size()) {
            displayQuestion(currentQuestionIndex);
        } else {
            showResults();
        }
    }

    private void showResults() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quiz Complete")
                .setMessage("Your score: " + score + " out of " + quizQuestions.size())
                .setPositiveButton("Finish", (dialog, which) -> {
                    // TODO: Save results to database if needed
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    // Model class for Question (if not already defined elsewhere)
    public static class Question {
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
}