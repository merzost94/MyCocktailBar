package com.example.mycocktailbar.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "ingredients")
public class Ingredient {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String category;
    private boolean hasItem;

    // Основной конструктор для Room
    public Ingredient(long id, String name, String category, boolean hasItem) {
        this.id = id;
        this.name = name;
        // Защита от null: если category == null, ставим "Другое"
        this.category = category != null ? category : "Другое";
        this.hasItem = hasItem;
    }

    // Конструктор для создания нового ингредиента (без id)
    @Ignore
    public Ingredient(String name, boolean hasItem) {
        this.name = name;
        this.hasItem = hasItem;
        this.category = "Другое";
    }

    @Ignore
    public Ingredient(String name, String category, boolean hasItem) {
        this.name = name;
        this.category = category != null ? category : "Другое";
        this.hasItem = hasItem;
    }

    // Геттеры и сеттеры
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() {
        // На всякий случай защищаем и геттер
        return category != null ? category : "Другое";
    }

    public void setCategory(String category) {
        this.category = category != null ? category : "Другое";
    }

    public boolean isHasItem() { return hasItem; }
    public void setHasItem(boolean hasItem) { this.hasItem = hasItem; }
}