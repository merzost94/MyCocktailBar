package com.example.mycocktailbar.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.mycocktailbar.models.Ingredient;
import java.util.List;

@Dao
public interface IngredientDao {
    @Insert
    long insertIngredient(Ingredient ingredient);

    @Update
    void updateIngredient(Ingredient ingredient);

    @Query("SELECT * FROM ingredients ORDER BY name")
    LiveData<List<Ingredient>> getAllIngredients();

    @Query("SELECT * FROM ingredients WHERE hasItem = 1")
    LiveData<List<Ingredient>> getAvailableIngredients();

    @Query("UPDATE ingredients SET hasItem = :hasItem WHERE id = :ingredientId")
    void updateIngredientStatus(long ingredientId, boolean hasItem);
}