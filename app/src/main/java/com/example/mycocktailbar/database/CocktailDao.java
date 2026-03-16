package com.example.mycocktailbar.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.RoomWarnings;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.models.CocktailIngredientCrossRef;
import com.example.mycocktailbar.models.Ingredient;
import java.util.List;

@Dao
public interface CocktailDao {

    @Query("SELECT * FROM ingredients WHERE isAvailable = :available ORDER BY name ASC")
    LiveData<List<Ingredient>> getIngredientsByStatus(boolean available);

    @Query("SELECT * FROM cocktails WHERE id = :id")
    LiveData<Cocktail> getCocktailById(long id);

    @Query("SELECT * FROM ingredients WHERE name LIKE '%' || :search || '%' ORDER BY name ASC")
    LiveData<List<Ingredient>> searchIngredients(String search);

    @Query("UPDATE ingredients SET isAvailable = :status WHERE id = :id")
    void updateIngredientAvailability(long id, boolean status);

    @Query("SELECT * FROM cocktails ORDER BY name ASC")
    LiveData<List<Cocktail>> getAllCocktails();

    @Query("SELECT * FROM cocktails WHERE name LIKE '%' || :search || '%' ORDER BY name ASC")
    LiveData<List<Cocktail>> searchCocktails(String search);

    @Query("SELECT * FROM cocktails WHERE id NOT IN (" +
            "SELECT cocktailId FROM cocktail_ingredients " +
            "WHERE ingredientId IN (SELECT id FROM ingredients WHERE isAvailable = 0)" +
            ") ORDER BY name ASC")
    LiveData<List<Cocktail>> getAvailableCocktails();

    @Query("SELECT cocktails.* FROM cocktails " +
            "JOIN cocktail_ingredients ON cocktails.id = cocktail_ingredients.cocktailId " +
            "JOIN ingredients ON cocktail_ingredients.ingredientId = ingredients.id " +
            "GROUP BY cocktails.id " +
            "HAVING SUM(CASE WHEN ingredients.isAvailable = 0 THEN 1 ELSE 0 END) BETWEEN 1 AND 2 " +
            "ORDER BY name ASC")
    LiveData<List<Cocktail>> getAlmostAvailableCocktails();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertIngredient(Ingredient ingredient);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCocktail(Cocktail cocktail);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRelation(CocktailIngredientCrossRef crossRef);

    @Query("SELECT * FROM ingredients LIMIT 1")
    Ingredient getAnyIngredient();

    @Query("SELECT * FROM cocktails WHERE category = :category")
    LiveData<List<Cocktail>> getCocktailsByCategory(String category);

    @Query("SELECT DISTINCT category FROM cocktails")
    LiveData<List<String>> getAllCategories();

    @Transaction
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM ingredients " +
            "INNER JOIN cocktail_ingredients ON ingredients.id = cocktail_ingredients.ingredientId " +
            "WHERE cocktail_ingredients.cocktailId = :cocktailId")
    LiveData<List<Ingredient>> getIngredientsForCocktail(long cocktailId);
}