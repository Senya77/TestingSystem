package ru.samsung.itschool.testing_system.activities;

import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.samsung.itschool.testing_system.R;
import ru.samsung.itschool.testing_system.models.Answer;
import ru.samsung.itschool.testing_system.models.Question;
import ru.samsung.itschool.testing_system.models.Test;

public class TestActivity extends AppCompatActivity {

    private LinearLayout questionsContainer;
    private Button nextButton, submitButton;
    private ImageView questionImageView;
    private TextView testNameTextView, timerTextView, currentQuestionIndexTextView;
    private Test test;
    private int totalScore = 0;
    private int currentQuestionIndex = 0;
    private CountDownTimer countDownTimer;
    private int timeLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        test = (Test) getIntent().getSerializableExtra("test");

        if (test == null) {
            Toast.makeText(this, "Тест не был передан", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        testNameTextView = findViewById(R.id.testName);
        testNameTextView.setText(test.getName());


        timerTextView = findViewById(R.id.timerText);
        timeLeft = test.getTimeLimit() * 1000;  // время в миллисекундах
        startTimer();

        questionImageView = findViewById(R.id.questionImage);


        currentQuestionIndexTextView = findViewById(R.id.currentQuestionIndex);
        currentQuestionIndexTextView.setText("Вопрос 1 из " + test.getQuestions().size());

        questionsContainer = findViewById(R.id.questionsContainer);
        nextButton = findViewById(R.id.nextButton);
        submitButton = findViewById(R.id.submitButton);

        displayCurrentQuestion();

        if (currentQuestionIndex + 1 == test.getQuestions().size()) {
            nextButton.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateScore();
                currentQuestionIndex++;
                displayCurrentQuestion();
                currentQuestionIndexTextView.setText("Вопрос " + (currentQuestionIndex + 1) + " из " + test.getQuestions().size());

                if (currentQuestionIndex + 1 == test.getQuestions().size()) {
                    nextButton.setVisibility(View.GONE);
                    submitButton.setVisibility(View.VISIBLE);
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateScore();
                showResults();
            }
        });
    }

    public int calculateAllScore() {
        int totalScore = 0;

        for (Question question : test.getQuestions()) {
            for (Answer answer : question.getAnswers()) {
                if (answer.isCorrect()) {
                    totalScore += question.getPoints();
                }
            }
        }

        return totalScore;
    }


    private void displayCurrentQuestion() {
        if (currentQuestionIndex >= test.getQuestions().size()) {
            return;
        }

        questionsContainer.removeAllViews();
        Question question = test.getQuestions().get(currentQuestionIndex);

        ImageView questionImageView = findViewById(R.id.questionImage);
        String imageUri = question.getImageUri();


        if (imageUri != null && !imageUri.isEmpty()) {
            Log.d("TestInfo", imageUri);
            loadImageFromUri(imageUri);
        } else {
            questionImageView.setVisibility(View.GONE);
        }
        TextView questionTextView = new TextView(this);
        questionTextView.setText(question.getText());
        questionTextView.setTextSize(18);
        questionsContainer.addView(questionTextView);

        for (Answer answer : question.getAnswers()) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(answer.getText());
            checkBox.setTag(answer);
            questionsContainer.addView(checkBox);
        }
    }
    private void loadImageFromUri(String imageUri) {
        try {
            Uri uri = Uri.parse(imageUri);
            questionImageView.setImageURI(uri);
            questionImageView.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
            questionImageView.setVisibility(View.GONE);
        }
    }

    private void calculateScore() {
        if (currentQuestionIndex >= test.getQuestions().size()) {
            return;
        }

        Question question = test.getQuestions().get(currentQuestionIndex);

        for (int i = 0; i < questionsContainer.getChildCount(); i++) {
            View child = questionsContainer.getChildAt(i);

            if (child instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) child;
                Answer answer = (Answer) checkBox.getTag();

                if (answer.isCorrect() && checkBox.isChecked()) {
                    totalScore += question.getPoints();
                }
            }
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = (int) millisUntilFinished;
                int minutes = (timeLeft / 1000) / 60;
                int seconds = (timeLeft / 1000) % 60;
                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                calculateScore();
                showResults();
            }
        };
        countDownTimer.start();
    }

    private void showResults() {
        countDownTimer.cancel();

        questionsContainer.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        currentQuestionIndexTextView.setVisibility(View.GONE);
        timerTextView.setVisibility(View.GONE);
        questionImageView.setVisibility(View.GONE);

        int allScore = calculateAllScore();

        TextView resultsTextView = findViewById(R.id.resultsTextView);
        String result = "Тест завершен\nБаллы: " + totalScore + "/" + allScore + "\nВремя: " + (test.getTimeLimit() - timeLeft / 1000) + " секунд.";
        resultsTextView.setText(result);
        resultsTextView.setVisibility(View.VISIBLE);
    }

}

