package ru.samsung.itschool.testing_system.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import java.util.ArrayList;

import ru.samsung.itschool.testing_system.R;
import ru.samsung.itschool.testing_system.models.Question;

public class QuestionAdapter extends android.widget.BaseAdapter {

    private final Context context;
    private final ArrayList<Question> questions;

    public QuestionAdapter(Context context, ArrayList<Question> questions) {
        this.context = context;
        this.questions = questions;
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Object getItem(int position) {
        return questions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_question, parent, false);
        }

        TextView tvQuestionText = convertView.findViewById(R.id.tv_question_text);
        TextView tvOptionCount = convertView.findViewById(R.id.tv_option_count);

        Button button = convertView.findViewById(R.id.btn_delete_question);

        Question question = questions.get(position);

        tvQuestionText.setText(question.getText());
        tvOptionCount.setText("Вариантов ответа: " + question.getOptionCount());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Подтвердить удаление.");
                builder.setMessage("Вы уверены, что хотите удалить вопрос?");
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        questions.remove(position);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Закрываем диалог
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return convertView;
    }
}
