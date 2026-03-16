package com.example.mycocktailbar.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.models.Ingredient;
import java.util.List;

@Dao
public interface CocktailDao {
    @Query("SELECT * FROM cocktails WHERE id = :id")
    LiveData<Cocktail> getCocktailById(long id);

    @Query("SELECT * FROM cocktails ORDER BY name")
    LiveData<List<Cocktail>> getAllCocktails();

    @Query("SELECT * FROM cocktails WHERE name LIKE :search OR description LIKE :search")
    LiveData<List<Cocktail>> searchCocktails(String search);

    @Query("SELECT * FROM cocktails WHERE name LIKE :search OR description LIKE :search")
    List<Cocktail> searchCocktailsSync(String search);

    @Insert
    long insertCocktail(Cocktail cocktail);

    @Query("SELECT * FROM cocktails WHERE category = :category")
    LiveData<List<Cocktail>> getCocktailsByCategory(String category);

    @Query("SELECT * FROM ingredients ORDER BY name")
    LiveData<List<Ingredient>> getAllIngredients();

    @Query("SELECT * FROM ingredients WHERE hasItem = :hasItem")
    LiveData<List<Ingredient>> getIngredientsByStatus(boolean hasItem);

    @Query("SELECT * FROM ingredients WHERE name LIKE :search")
    LiveData<List<Ingredient>> searchIngredients(String search);

    @Insert
    long insertIngredient(Ingredient ingredient);

    @Query("UPDATE ingredients SET hasItem = :hasItem WHERE id = :id")
    void updateIngredientAvailability(long id, boolean hasItem);
}