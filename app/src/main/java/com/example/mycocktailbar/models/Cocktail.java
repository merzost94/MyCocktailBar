package com.example.mycocktailbar.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import java.util.List;

@Entity(tableName = "cocktails")
public class Cocktail {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String description;
    private String category;
    private String instructions;
    private String imageUrl;
    @Ignore
    private List<Ingredient> ingredients;

    // Конструктор для Room
    public Cocktail(String name, String description, String category, String instructions, String imageUrl) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
    }

    // Геттеры и сеттеры
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}