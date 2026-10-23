package ru.mirea.khrechko.recyclerviewapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private final List<HistoricalEvent> events;
    private Context context;

    public EventRecyclerViewAdapter(List<HistoricalEvent> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.event_item_view, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        HistoricalEvent event = events.get(position);
        holder.year.setText(String.valueOf(event.getYear()));
        holder.title.setText(event.getTitle());
        holder.description.setText(event.getDescription());
        int imageId = context.getResources().getIdentifier(
                event.getImageName(), "drawable", context.getPackageName());
        holder.image.setImageResource(imageId == 0 ? android.R.drawable.ic_menu_report_image : imageId);
        holder.itemView.setOnClickListener(v -> Toast.makeText(context,
                event.toString(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() { return events.size(); }
}
