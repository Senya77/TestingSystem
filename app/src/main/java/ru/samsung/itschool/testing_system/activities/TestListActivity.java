package ru.samsung.itschool.testing_system.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ru.samsung.itschool.testing_system.database.DatabaseHelper;
import ru.samsung.itschool.testing_system.R;
import ru.samsung.itschool.testing_system.models.Test;
import ru.samsung.itschool.testing_system.adapters.TestAdapter;

public class TestListActivity extends AppCompatActivity {

    private ListView listViewTests;
    private DatabaseHelper dbHelper;
    private ArrayList<Test> testList;
    private TestAdapter testAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);

        dbHelper = new DatabaseHelper(this);
        listViewTests = findViewById(R.id.lv_tests);

        // Получаем все тесты из базы данных
        testList = dbHelper.getAllTests();

        // Настроим адаптер для ListView
        testAdapter = new TestAdapter(this, testList);
        listViewTests.setAdapter(testAdapter);

        listViewTests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
                Test selectedTest = testList.get(position);
                showTestSelectionDialog(selectedTest);
            }
        });
    }

    private void showTestSelectionDialog(Test selectedTest) {
        new AlertDialog.Builder(this)
                .setMessage("Хотите пройти тест '" + selectedTest.getName() + "'?")
                .setPositiveButton("Да", (dialog, which) -> {
                    Intent intent = new Intent(TestListActivity.this, TestActivity.class);
                    intent.putExtra("test", selectedTest);
                    startActivity(intent);
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}
