package com.example.mycocktailbar.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ingredients")
public class Ingredient {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String category;
    private boolean isAvailable;
    private int imageResourceId;

    // Удалили @Ignore и пустой конструктор.
    // Теперь Room и твой AppDatabase используют этот:
    public Ingredient(String name, String category, boolean isAvailable, int imageResourceId) {
        this.name = name;
        this.category = category;
        this.isAvailable = isAvailable;
        this.imageResourceId = imageResourceId;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getImageResourceId() { return imageResourceId; }
    public void setImageResourceId(int imageResourceId) { this.imageResourceId = imageResourceId; }
}