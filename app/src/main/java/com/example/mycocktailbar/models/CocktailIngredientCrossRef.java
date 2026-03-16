package com.example.mycocktailbar.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "cocktail_ingredients",
        primaryKeys = {"cocktailId", "ingredientId"},
        indices = {@Index("ingredientId")} // Индекс для ускорения запросов
)
public class CocktailIngredientCrossRef {
    public long cocktailId;
    public long ingredientId;
    public String amount;

    public CocktailIngredientCrossRef(long cocktailId, long ingredientId, String amount) {
        this.cocktailId = cocktailId;
        this.ingredientId = ingredientId;
        this.amount = amount;
    }
}