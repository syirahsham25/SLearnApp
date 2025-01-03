package com.example.project;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
public class TeacherReviewActivity extends AppCompatActivity {
    private LinearLayout questionsContainer;
    private ArrayList<Question> quizQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_review);

        questionsContainer = findViewById(R.id.questionsContainer);

        // Set up back button
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        // Get questions from intent
        loadQuestions();
        displayQuestions();
    }

    @SuppressWarnings("unchecked")
    private void loadQuestions() {
        quizQuestions = new ArrayList<>();
        if (getIntent().hasExtra("quiz_questions")) {
            quizQuestions = (ArrayList<Question>) getIntent().getSerializableExtra("quiz_questions");
        }

        if (quizQuestions.isEmpty()) {
            showEmptyState();
        }
    }

    private void showEmptyState() {
        TextView emptyView = new TextView(this);
        emptyView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        emptyView.setText("No questions available to review");
        emptyView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        emptyView.setPadding(32, 32, 32, 32);
        emptyView.setTextColor(Color.GRAY);

        questionsContainer.addView(emptyView);
    }

    private void displayQuestions() {
        questionsContainer.removeAllViews(); // Clear existing views

        for (int i = 0; i < quizQuestions.size(); i++) {
            Question question = quizQuestions.get(i);
            View questionView = getLayoutInflater().inflate(
                    R.layout.each_question_layout, questionsContainer, false);

            // Find views
            TextView questionNumberText = questionView.findViewById(R.id.questionNumberText);
            TextView questionText = questionView.findViewById(R.id.questionText);
            TextView optionAText = questionView.findViewById(R.id.optionAText);
            TextView optionBText = questionView.findViewById(R.id.optionBText);
            TextView optionCText = questionView.findViewById(R.id.optionCText);
            TextView optionDText = questionView.findViewById(R.id.optionDText);
            TextView correctAnswerText = questionView.findViewById(R.id.correctAnswerText);

            // Set texts
            questionNumberText.setText("Question " + (i + 1));
            questionText.setText(question.questionText);
            optionAText.setText("A. " + question.optionA);
            optionBText.setText("B. " + question.optionB);
            optionCText.setText("C. " + question.optionC);
            optionDText.setText("D. " + question.optionD);
            correctAnswerText.setText("Correct Answer: Option " + question.correctAnswer);

            // Highlight correct answer
            TextView correctOptionText = null;
            switch(question.correctAnswer) {
                case "A": correctOptionText = optionAText; break;
                case "B": correctOptionText = optionBText; break;
                case "C": correctOptionText = optionCText; break;
                case "D": correctOptionText = optionDText; break;
            }

            if (correctOptionText != null) {
                correctOptionText.setBackgroundResource(R.color.correct_answer_background);
                // Add animation
                correctOptionText.setAlpha(0f);
                correctOptionText.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .start();
            }

            questionsContainer.addView(questionView);

            // Add spacing between questions
            if (i < quizQuestions.size() - 1) {
                View spacer = new View(this);
                spacer.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        getResources().getDimensionPixelSize(R.dimen.question_spacing)));
                questionsContainer.addView(spacer);
            }
        }
    }
}