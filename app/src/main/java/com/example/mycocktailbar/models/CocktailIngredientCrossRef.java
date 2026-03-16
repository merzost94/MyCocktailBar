package com.example.mycocktailbar.models;

import androidx.room.Entity;

@Entity(tableName = "cocktail_ingredient_cross_ref", primaryKeys = {"cocktailId", "ingredientId"})
public class CocktailIngredientCrossRef {
    private long cocktailId;
    private long ingredientId;

    public CocktailIngredientCrossRef(long cocktailId, long ingredientId) {
        this.cocktailId = cocktailId;
        this.ingredientId = ingredientId;
    }

    public long getCocktailId() {
        return cocktailId;
    }

    public void setCocktailId(long cocktailId) {
        this.cocktailId = cocktailId;
    }

    public long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(long ingredientId) {
        this.ingredientId = ingredientId;
    }
}