package ru.samsung.itschool.testing_system.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.samsung.itschool.testing_system.R;
import ru.samsung.itschool.testing_system.models.Test;

public class TestAdapter extends ArrayAdapter<Test> {

    private Context context;
    private ArrayList<Test> tests;

    public TestAdapter(Context context, ArrayList<Test> tests) {
        super(context, 0, tests);
        this.context = context;
        this.tests = tests;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.test_item, parent, false);
        }

        Test currentTest = tests.get(position);

        TextView testNameTextView = convertView.findViewById(R.id.testName);
        testNameTextView.setText(currentTest.getName());

        TextView timeLimitTextView = convertView.findViewById(R.id.timeLimit);
        timeLimitTextView.setText("Время: " + (currentTest.getTimeLimit() / 60) + " минут");

        return convertView;
    }
}
