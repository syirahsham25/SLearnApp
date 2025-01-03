package com.example.project;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import android.view.ViewGroup;

public class TeacherAddQuizActivity extends AppCompatActivity {
    private LinearLayout questionsContainer;
    private Button addQuestionButton;
    private Button saveButton;
    private int questionCount = 0;
    private ArrayList<QuestionData> questionsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_quizz);

        questionsContainer = findViewById(R.id.questionsContainer);
        addQuestionButton = findViewById(R.id.addQ_btn);
        saveButton = findViewById(R.id.save_btn);

        addNewQuestion(); // Add first question by default

        addQuestionButton.setOnClickListener(v -> addNewQuestion());
        saveButton.setOnClickListener(v -> saveQuiz());
    }

    private void addNewQuestion() {
        questionCount++;

        View questionView = getLayoutInflater().inflate(R.layout.tr_question_template_layout, questionsContainer, false);

        TextView questionNumberText = questionView.findViewById(R.id.questionNumberText);
        questionNumberText.setText("Question " + questionCount);

        EditText questionInput = questionView.findViewById(R.id.questionInput);
        RadioButton radioOptionA = questionView.findViewById(R.id.radioOptionA);
        RadioButton radioOptionB = questionView.findViewById(R.id.radioOptionB);
        RadioButton radioOptionC = questionView.findViewById(R.id.radioOptionC);
        RadioButton radioOptionD = questionView.findViewById(R.id.radioOptionD);
        EditText optionAInput = questionView.findViewById(R.id.optionAInput);
        EditText optionBInput = questionView.findViewById(R.id.optionBInput);
        EditText optionCInput = questionView.findViewById(R.id.optionCInput);
        EditText optionDInput = questionView.findViewById(R.id.optionDInput);

        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        radioOptionA.setId(View.generateViewId());
        radioOptionB.setId(View.generateViewId());
        radioOptionC.setId(View.generateViewId());
        radioOptionD.setId(View.generateViewId());

        ((ViewGroup) radioOptionA.getParent()).removeView(radioOptionA);
        ((ViewGroup) radioOptionB.getParent()).removeView(radioOptionB);
        ((ViewGroup) radioOptionC.getParent()).removeView(radioOptionC);
        ((ViewGroup) radioOptionD.getParent()).removeView(radioOptionD);

        radioGroup.addView(radioOptionA);
        radioGroup.addView(radioOptionB);
        radioGroup.addView(radioOptionC);
        radioGroup.addView(radioOptionD);

        questionsContainer.addView(questionView);

        QuestionData questionData = new QuestionData(
                questionCount,
                questionInput,
                radioGroup,
                optionAInput,
                optionBInput,
                optionCInput,
                optionDInput,
                radioOptionA,
                radioOptionB,
                radioOptionC,
                radioOptionD
        );
        questionsList.add(questionData);
    }

    private void saveQuiz() {
        ArrayList<Question> finalQuestions = new ArrayList<>();

        for (QuestionData qData : questionsList) {
            String questionText = qData.questionInput.getText().toString().trim();
            String optionA = qData.optionAInput.getText().toString().trim();
            String optionB = qData.optionBInput.getText().toString().trim();
            String optionC = qData.optionCInput.getText().toString().trim();
            String optionD = qData.optionDInput.getText().toString().trim();

            String correctAnswer = "";
            if (qData.radioOptionA.isChecked()) correctAnswer = "A";
            else if (qData.radioOptionB.isChecked()) correctAnswer = "B";
            else if (qData.radioOptionC.isChecked()) correctAnswer = "C";
            else if (qData.radioOptionD.isChecked()) correctAnswer = "D";

            if (questionText.isEmpty() || optionA.isEmpty() || optionB.isEmpty()
                    || optionC.isEmpty() || optionD.isEmpty()) {
                Toast.makeText(this, "Please complete all fields for Question " + qData.questionNumber,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (correctAnswer.isEmpty()) {
                Toast.makeText(this, "Please select the correct answer for Question " + qData.questionNumber,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Question question = new Question(
                    questionText,
                    optionA,
                    optionB,
                    optionC,
                    optionD,
                    correctAnswer
            );
            finalQuestions.add(question);
        }

        // Launch review activity with the questions
        Intent intent = new Intent(this, TeacherReviewActivity.class);
        intent.putExtra("quiz_questions", finalQuestions);
        startActivity(intent);

        Toast.makeText(this, "Quiz saved successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private static class QuestionData {
        int questionNumber;
        EditText questionInput;
        RadioGroup radioGroup;
        EditText optionAInput;
        EditText optionBInput;
        EditText optionCInput;
        EditText optionDInput;
        RadioButton radioOptionA;
        RadioButton radioOptionB;
        RadioButton radioOptionC;
        RadioButton radioOptionD;

        QuestionData(int questionNumber, EditText questionInput, RadioGroup radioGroup,
                     EditText optionAInput, EditText optionBInput, EditText optionCInput,
                     EditText optionDInput, RadioButton radioOptionA, RadioButton radioOptionB,
                     RadioButton radioOptionC, RadioButton radioOptionD) {
            this.questionNumber = questionNumber;
            this.questionInput = questionInput;
            this.radioGroup = radioGroup;
            this.optionAInput = optionAInput;
            this.optionBInput = optionBInput;
            this.optionCInput = optionCInput;
            this.optionDInput = optionDInput;
            this.radioOptionA = radioOptionA;
            this.radioOptionB = radioOptionB;
            this.radioOptionC = radioOptionC;
            this.radioOptionD = radioOptionD;
        }
    }
}