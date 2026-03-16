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
    @Query("SELECT * FROM ingredients ORDER BY name")
    LiveData<List<Ingredient>> getAllIngredients();

    @Query("SELECT * FROM ingredients WHERE name LIKE '%' || :query || '%' ORDER BY name")
    LiveData<List<Ingredient>> searchIngredients(String query);

    @Query("SELECT * FROM ingredients WHERE hasItem = :hasItem ORDER BY name")
    LiveData<List<Ingredient>> getIngredientsByStatus(boolean hasItem);

    @Insert
    long insertIngredient(Ingredient ingredient);

    @Update
    void updateIngredient(Ingredient ingredient);

    @Query("UPDATE ingredients SET hasItem = :hasItem WHERE id = :id")
    void updateAvailability(long id, boolean hasItem);

    @Query("DELETE FROM ingredients WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM ingredients ORDER BY name")
    List<Ingredient> getAllIngredientsSync();
}