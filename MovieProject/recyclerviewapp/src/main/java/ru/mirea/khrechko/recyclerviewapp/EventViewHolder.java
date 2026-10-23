package ru.mirea.khrechko.recyclerviewapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventViewHolder extends RecyclerView.ViewHolder {
    final ImageView image;
    final TextView year;
    final TextView title;
    final TextView description;

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.imageEvent);
        year = itemView.findViewById(R.id.textEventYear);
        title = itemView.findViewById(R.id.textEventTitle);
        description = itemView.findViewById(R.id.textEventDescription);
    }
}
