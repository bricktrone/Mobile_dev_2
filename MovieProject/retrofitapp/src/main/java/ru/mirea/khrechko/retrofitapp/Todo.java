package ru.mirea.khrechko.retrofitapp;

import com.google.gson.annotations.SerializedName;

public class Todo {
    @SerializedName("userId")
    private int userId;
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("completed")
    private boolean completed;

    public int getUserId() { return userId; }
    public int getId() { return id; }
    public String getTitle() { return title == null ? "Без названия" : title; }
    public boolean getCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getImageUrl() {
        return "https://picsum.photos/seed/todo" + id + "/300/300";
    }
}
