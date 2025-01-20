package ru.samsung.itschool.testing_system.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import ru.samsung.itschool.testing_system.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCreateTest = findViewById(R.id.btn_create_test);
        Button btnTakeTest = findViewById(R.id.btn_take_test);

        btnCreateTest.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateTestActivity.class);
            startActivity(intent);
        });

        btnTakeTest.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TestListActivity.class);
            startActivity(intent);
        });
    }
}
