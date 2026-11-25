package ru.mirea.khrechko.fragmentapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {
    private final List<TaskItem> tasks = new ArrayList<>();
    private TextView progress;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state);
        int number = requireArguments().getInt(MainActivity.ARG_STUDENT_NUMBER, 0);
        ((TextView) view.findViewById(R.id.textStudentNumber)).setText("Номер по списку: " + number);
        progress = view.findViewById(R.id.textProgress);
        tasks.clear();
        tasks.add(new TaskItem("Изучить основы Fragment", true));
        tasks.add(new TaskItem("Создать разметку фрагмента", false));
        tasks.add(new TaskItem("Передать аргумент через Bundle", false));
        tasks.add(new TaskItem("Проверить обработку статуса задачи", false));
        RecyclerView recycler = view.findViewById(R.id.recyclerTasks);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(new TaskAdapter(tasks, this::updateProgress));
        updateProgress();
    }

    private void updateProgress() {
        int complete = 0;
        for (TaskItem task : tasks) if (task.done) complete++;
        if (progress != null) progress.setText("Выполнено: " + complete + " из " + tasks.size());
    }

    private static final class TaskItem {
        final String title; boolean done;
        TaskItem(String title, boolean done) { this.title = title; this.done = done; }
    }

    private static final class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.Holder> {
        private final List<TaskItem> items; private final Runnable onChanged;
        TaskAdapter(List<TaskItem> items, Runnable onChanged) { this.items = items; this.onChanged = onChanged; }
        @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false));
        }
        @Override public void onBindViewHolder(@NonNull Holder holder, int position) {
            TaskItem item = items.get(position);
            holder.title.setText(item.title);
            holder.status.setText(item.done ? "Статус: выполнено" : "Статус: ожидает выполнения");
            holder.check.setOnCheckedChangeListener(null);
            holder.check.setChecked(item.done);
            holder.check.setOnCheckedChangeListener((button, checked) -> {
                item.done = checked;
                holder.status.setText(checked ? "Статус: выполнено" : "Статус: ожидает выполнения");
                onChanged.run();
            });
        }
        @Override public int getItemCount() { return items.size(); }
        static class Holder extends RecyclerView.ViewHolder {
            final TextView title, status; final android.widget.CheckBox check;
            Holder(@NonNull View item) { super(item); title=item.findViewById(R.id.textTaskTitle); status=item.findViewById(R.id.textTaskStatus); check=item.findViewById(R.id.checkTaskDone); }
        }
    }
}
