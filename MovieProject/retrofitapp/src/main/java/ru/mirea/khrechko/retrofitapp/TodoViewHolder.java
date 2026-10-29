package ru.mirea.khrechko.retrofitapp;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TodoViewHolder extends RecyclerView.ViewHolder {
    final ImageView imageView;
    final TextView textViewTitle;
    final TextView textViewInfo;
    final CheckBox checkBoxCompleted;

    public TodoViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageViewTodo);
        textViewTitle = itemView.findViewById(R.id.textViewTitle);
        textViewInfo = itemView.findViewById(R.id.textViewInfo);
        checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
    }
}
