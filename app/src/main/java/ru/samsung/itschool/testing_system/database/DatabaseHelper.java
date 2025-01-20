package ru.samsung.itschool.testing_system.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ru.samsung.itschool.testing_system.models.Answer;
import ru.samsung.itschool.testing_system.models.Question;
import ru.samsung.itschool.testing_system.models.Test;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "testing_system.db";
    private static final int DATABASE_VERSION = 1;

    // Таблица тестов
    private static final String TABLE_TESTS = "tests";
    private static final String COLUMN_TEST_ID = "test_id";
    private static final String COLUMN_TEST_NAME = "name";
    private static final String COLUMN_TIME_LIMIT = "time_limit";

    // Таблица вопросов
    private static final String TABLE_QUESTIONS = "questions";
    private static final String COLUMN_QUESTION_ID = "question_id";
    private static final String COLUMN_QUESTION_TEXT = "text";
    private static final String COLUMN_QUESTION_TEST_ID = "test_id"; // Внешний ключ на таблицу tests
    private static final String COLUMN_QUESTION_POINTS = "points";

    // Таблица вариантов ответов
    private static final String TABLE_ANSWERS = "answers";
    private static final String COLUMN_ANSWER_ID = "answer_id";
    private static final String COLUMN_ANSWER_TEXT = "text";
    private static final String COLUMN_ANSWER_QUESTION_ID = "question_id"; // Внешний ключ на таблицу questions
    private static final String COLUMN_IS_CORRECT = "is_correct";
    private static final String COLUMN_IMAGE_URI = "image_uri"; // Новый столбец для хранения URI изображения

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создаем таблицу тестов
        String CREATE_TABLE_TESTS = "CREATE TABLE " + TABLE_TESTS + " (" +
                COLUMN_TEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TEST_NAME + " TEXT, " +
                COLUMN_TIME_LIMIT + " INTEGER)";
        db.execSQL(CREATE_TABLE_TESTS);

        // Создаем таблицу вопросов
        String CREATE_TABLE_QUESTIONS = "CREATE TABLE " + TABLE_QUESTIONS + " (" +
                COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_QUESTION_TEXT + " TEXT, " +
                COLUMN_QUESTION_TEST_ID + " INTEGER, " +
                COLUMN_IMAGE_URI + " TEXT, " + // Добавляем колонку для изображения
                COLUMN_QUESTION_POINTS + " INTEGER, " + // Добавляем колонку для баллов
                "FOREIGN KEY(" + COLUMN_QUESTION_TEST_ID + ") REFERENCES " + TABLE_TESTS + "(" + COLUMN_TEST_ID + "))";
        db.execSQL(CREATE_TABLE_QUESTIONS);

        // Создаем таблицу ответов
        String CREATE_TABLE_ANSWERS = "CREATE TABLE " + TABLE_ANSWERS + " (" +
                COLUMN_ANSWER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ANSWER_TEXT + " TEXT, " +
                COLUMN_IS_CORRECT + " INTEGER, " +
                COLUMN_ANSWER_QUESTION_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_ANSWER_QUESTION_ID + ") REFERENCES " + TABLE_QUESTIONS + "(" + COLUMN_QUESTION_ID + "))";
        db.execSQL(CREATE_TABLE_ANSWERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWERS);
        onCreate(db);
    }

    // Сохранение теста в базе данных
    public long saveTest(Test test) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEST_NAME, test.getName());
        values.put(COLUMN_TIME_LIMIT, test.getTimeLimit());

        long testId = db.insert(TABLE_TESTS, null, values);

        // Сохранение вопросов
        for (Question question : test.getQuestions()) {
            long questionId = saveQuestion(db, question, testId);
            for (Answer answer : question.getAnswers()) {
                saveAnswer(db, answer, questionId);
            }
        }
        return testId;
    }

    // Сохранение вопроса в базе данных
    private long saveQuestion(SQLiteDatabase db, Question question, long testId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION_TEXT, question.getText());
        values.put(COLUMN_QUESTION_TEST_ID, testId);
        values.put(COLUMN_IMAGE_URI, question.getImageUri()); // Сохраняем URI изображения
        values.put(COLUMN_QUESTION_POINTS, question.getPoints()); // Сохраняем количество баллов

        return db.insert(TABLE_QUESTIONS, null, values);
    }

    // Сохранение ответа в базе данных
    private void saveAnswer(SQLiteDatabase db, Answer answer, long questionId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ANSWER_TEXT, answer.getText());
        values.put(COLUMN_IS_CORRECT, answer.isCorrect() ? 1 : 0);
        values.put(COLUMN_ANSWER_QUESTION_ID, questionId);

        db.insert(TABLE_ANSWERS, null, values);
    }

    // Получение всех тестов
    public ArrayList<Test> getAllTests() {
        ArrayList<Test> tests = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_TESTS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_TEST_NAME));
                @SuppressLint("Range") int timeLimit = cursor.getInt(cursor.getColumnIndex(COLUMN_TIME_LIMIT));
                @SuppressLint("Range") ArrayList<Question> questions = getQuestionsByTestId(cursor.getLong(cursor.getColumnIndex(COLUMN_TEST_ID)));
                tests.add(new Test(name, questions, timeLimit));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tests;
    }

    // Получение вопросов для теста
    private ArrayList<Question> getQuestionsByTestId(long testId) {
        ArrayList<Question> questions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_QUESTIONS + " WHERE " + COLUMN_QUESTION_TEST_ID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(testId)});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String text = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_TEXT));
                @SuppressLint("Range") String imageUri = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI));
                @SuppressLint("Range") int points = cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_POINTS));
                @SuppressLint("Range") ArrayList<Answer> answers = getAnswersByQuestionId(cursor.getLong(cursor.getColumnIndex(COLUMN_QUESTION_ID)));
                Question question = new Question(text, points); // Передаем баллы в конструктор
                question.setAnswers(answers);
                question.setImageUri(imageUri);
                questions.add(question);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questions;
    }

    // Получение вариантов ответов для вопроса
    private ArrayList<Answer> getAnswersByQuestionId(long questionId) {
        ArrayList<Answer> answers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ANSWERS + " WHERE " + COLUMN_ANSWER_QUESTION_ID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(questionId)});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String text = cursor.getString(cursor.getColumnIndex(COLUMN_ANSWER_TEXT));
                @SuppressLint("Range") boolean isCorrect = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_CORRECT)) == 1;
                answers.add(new Answer(text, isCorrect));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return answers;
    }
}
