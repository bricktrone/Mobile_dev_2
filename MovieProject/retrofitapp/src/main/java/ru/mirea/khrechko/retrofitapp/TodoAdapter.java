package ru.mirea.khrechko.retrofitapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoViewHolder> {
    public interface OnCompletedChangedListener {
        void onCompletedChanged(Todo todo, boolean completed);
    }

    public static final int MODE_DEFAULT = 0;
    public static final int MODE_CENTER_CROP = 1;
    public static final int MODE_CIRCLE = 2;
    public static final int MODE_SMALL = 3;

    private final List<Todo> todos = new ArrayList<>();
    private final OnCompletedChangedListener listener;
    private int imageMode = MODE_DEFAULT;

    public TodoAdapter(OnCompletedChangedListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Todo> items) {
        todos.clear();
        if (items != null) todos.addAll(items);
        notifyDataSetChanged();
    }

    public void setImageMode(int mode) {
        imageMode = mode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todos.get(position);
        holder.textViewTitle.setText(todo.getTitle());
        holder.textViewInfo.setText("#" + todo.getId() + " · пользователь " + todo.getUserId());

        RequestCreator request = Picasso.get().load(todo.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_dialog_alert);
        switch (imageMode) {
            case MODE_CENTER_CROP:
                request.fit().centerCrop();
                break;
            case MODE_CIRCLE:
                request.fit().centerCrop().transform(new CircleTransformation());
                break;
            case MODE_SMALL:
                request.resize(48, 48).centerInside();
                break;
            default:
                request.fit().centerInside();
                break;
        }
        request.into(holder.imageView);

        holder.checkBoxCompleted.setOnCheckedChangeListener(null);
        holder.checkBoxCompleted.setChecked(todo.getCompleted());
        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // isPressed отличает действие пользователя от bind/recycling ViewHolder.
            if (buttonView.isPressed() && listener != null) {
                listener.onCompletedChanged(todo, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() { return todos.size(); }
}
