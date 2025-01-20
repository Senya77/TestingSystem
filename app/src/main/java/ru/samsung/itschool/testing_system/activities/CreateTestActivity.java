package ru.samsung.itschool.testing_system.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ru.samsung.itschool.testing_system.database.DatabaseHelper;
import ru.samsung.itschool.testing_system.models.Question;
import ru.samsung.itschool.testing_system.adapters.QuestionAdapter;
import ru.samsung.itschool.testing_system.R;
import ru.samsung.itschool.testing_system.models.Test;

public class CreateTestActivity extends AppCompatActivity {
    private EditText setTestName, setTimeLimit;
    private ListView lvQuestions;
    private Button btnAddQuestion, btnSaveTest;

    private ArrayList<Question> questionList = new ArrayList<>();
    private QuestionAdapter questionAdapter;
    private int questionPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);

        setTestName = findViewById(R.id.et_test_name);
        lvQuestions = findViewById(R.id.lv_questions);
        btnAddQuestion = findViewById(R.id.btn_add_question);
        setTimeLimit = findViewById(R.id.et_time_limit);
        btnSaveTest = findViewById(R.id.btn_save_test);

        questionAdapter = new QuestionAdapter(this, questionList);
        lvQuestions.setAdapter(questionAdapter);

        btnAddQuestion.setOnClickListener(v -> {
            Intent intent = new Intent(CreateTestActivity.this, AddQuestionActivity.class);
            startActivityForResult(intent, 100);
            questionPosition = questionList.size() + 1;
        });

        btnSaveTest.setOnClickListener(v -> {
            String testName = setTestName.getText().toString().trim();
            String timeLimitString = setTimeLimit.getText().toString().trim();
            int timeLimit = timeLimitString.isEmpty() ? -1 : Integer.parseInt(timeLimitString);

            if (testName.isEmpty()) {
                Toast.makeText(CreateTestActivity.this, "Введите название теста", Toast.LENGTH_SHORT).show();
                return;
            }

            if (questionList.isEmpty()) {
                Toast.makeText(CreateTestActivity.this, "Добавьте хотя бы один вопрос", Toast.LENGTH_SHORT).show();
                return;
            }

            if (timeLimitString.isEmpty()){
                Toast.makeText(CreateTestActivity.this, "Введите ограничение по времени", Toast.LENGTH_SHORT).show();
                return;
            }

            Test newTest = new Test(testName, questionList, timeLimit * 60);
            DatabaseHelper dbHelper = new DatabaseHelper(CreateTestActivity.this);
            dbHelper.saveTest(newTest);
            Toast.makeText(CreateTestActivity.this, "Тест сохранен!", Toast.LENGTH_SHORT).show();
            finish();
        });

        lvQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Question selectedItem = questionList.get(position);

                Intent intent = new Intent(CreateTestActivity.this, AddQuestionActivity.class);
                intent.putExtra("text", selectedItem.getText());
                intent.putExtra("options", selectedItem.getOptionCount());
                intent.putExtra("points", selectedItem.getPoints());
                intent.putExtra("image", selectedItem.getImageUri());
                intent.putParcelableArrayListExtra("answers", selectedItem.getAnswers());

                startActivityForResult(intent, 100);
                questionPosition = position;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Question question = (Question) data.getSerializableExtra("question");
            if (questionPosition > questionList.size()){
                questionList.add(question);
                questionAdapter.notifyDataSetChanged();
            }
            else {
                questionList.set(questionPosition, question);
                questionAdapter.notifyDataSetChanged();
            }
        }
    }
}
