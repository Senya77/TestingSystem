package ru.samsung.itschool.testing_system.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import ru.samsung.itschool.testing_system.models.Answer;
import ru.samsung.itschool.testing_system.models.Question;
import ru.samsung.itschool.testing_system.R;

public class AddQuestionActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etQuestionText;
    private EditText etOptionCount;
    private EditText etPoints;
    private Button btnGenerateOptions;
    private Button btnSaveQuestion;
    private LinearLayout optionsContainer;
    private Button btnAddImage;
    private ImageView ivQuestionImage;

    private ArrayList<Answer> answers = new ArrayList<>();
    private Uri imageUri = null;
    private String savedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        etQuestionText = findViewById(R.id.et_question_text);
        etOptionCount = findViewById(R.id.et_option_count);
        etPoints = findViewById(R.id.et_points); // Поле для баллов
        btnGenerateOptions = findViewById(R.id.btn_generate_options);
        btnSaveQuestion = findViewById(R.id.btn_save_question);
        optionsContainer = findViewById(R.id.ll_options);
        btnAddImage = findViewById(R.id.btn_add_image);
        ivQuestionImage = findViewById(R.id.iv_question_image);

        btnGenerateOptions.setOnClickListener(v -> generateAnswerFields());

        btnSaveQuestion.setOnClickListener(v -> saveQuestion());

        btnAddImage.setOnClickListener(v -> openImageChooser());


        Bundle extras = getIntent().getExtras();
        if (extras != null){
            fillFields(extras);
        }
    }

    protected void fillFields(Bundle extras) {
        String text = extras.getString("text");
        int points = extras.getInt("points");
        int options = extras.getInt("options");
        String imageUriString = extras.getString("image");

        if (imageUriString != null) {
            ivQuestionImage.setImageURI(Uri.parse(imageUriString)); // Отображаем изображение в ImageView
            ivQuestionImage.setVisibility(View.VISIBLE); // Показываем ImageView
            savedImagePath = imageUriString;
        }

        etQuestionText.setText(text);
        etPoints.setText(Integer.toString(points));
        etOptionCount.setText(Integer.toString(options));
        generateAnswerFields();

        ArrayList<Answer> answers = extras.getParcelableArrayList("answers");
        for (int i = 0; i < optionsContainer.getChildCount(); i++){
            View optionView = optionsContainer.getChildAt(i);

            EditText etOptionText = optionView.findViewById(R.id.et_option_text);
            CheckBox cbCorrect = optionView.findViewById(R.id.cb_correct);

            etOptionText.setText(answers.get(i).getText());
            cbCorrect.setChecked(answers.get(i).isCorrect());
        }

    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                savedImagePath = saveImageToInternalStorage(this, imageUri, getContentResolver());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ivQuestionImage.setImageURI(Uri.parse(savedImagePath));
            ivQuestionImage.setVisibility(View.VISIBLE);
        }
    }

    private String saveImageToInternalStorage(Context context, Uri imageUri, ContentResolver contentResolver) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = contentResolver.openInputStream(imageUri);
            if(inputStream == null) return null;

            String fileName = UUID.randomUUID().toString() + ".jpg";
            File file = new File(context.getFilesDir(), fileName);

            outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return file.getAbsolutePath();
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    private void saveQuestion() {
        String questionText = etQuestionText.getText().toString().trim();
        String pointsStr = etPoints.getText().toString().trim();

        if (questionText.isEmpty()) {
            Toast.makeText(this, "Введите текст вопроса", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pointsStr.isEmpty()) {
            Toast.makeText(this, "Введите количество баллов за правильный ответ", Toast.LENGTH_SHORT).show();
            return;
        }

        int points;
        try {
            points = Integer.parseInt(pointsStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Некорректное значение баллов", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hasCorrectAnswer = false;

        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            View optionView = optionsContainer.getChildAt(i);

            EditText etOptionText = optionView.findViewById(R.id.et_option_text);
            String answerText = etOptionText.getText().toString().trim();

            CheckBox cbCorrect = optionView.findViewById(R.id.cb_correct);
            boolean isCorrect = cbCorrect.isChecked();

            if (answerText.isEmpty()) {
                Toast.makeText(this, "Заполните все варианты", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isCorrect) {
                hasCorrectAnswer = true;
            }

            answers.get(i).setText(answerText);
            answers.get(i).setCorrect(isCorrect);
        }

        if (!hasCorrectAnswer) {
            Toast.makeText(this, "Должен быть хотя бы один правильный ответ", Toast.LENGTH_SHORT).show();
            return;
        }

        Question question = new Question(questionText, answers.size());
        question.setAnswers(answers);
        question.setPoints(points);

        question.setImageUri(savedImagePath != null ? savedImagePath : null);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("question", question);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void generateAnswerFields() {
        optionsContainer.removeAllViews();
        answers.clear();

        String optionCountStr = etOptionCount.getText().toString().trim();
        int optionCount;
        try {
            optionCount = Integer.parseInt(optionCountStr);
            if (optionCount < 2) {
                Toast.makeText(this, "Необходимо более 1 варианта ответа", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Введите корректное количество вариантов", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < optionCount; i++) {
            View optionView = getLayoutInflater().inflate(R.layout.item_option, optionsContainer, false);

            EditText etOptionText = optionView.findViewById(R.id.et_option_text);
            etOptionText.setHint("Вариант " + (i + 1));

            optionView.findViewById(R.id.cb_correct).setTag(i);
            optionsContainer.addView(optionView);

            answers.add(new Answer("", false));
        }
    }
}
