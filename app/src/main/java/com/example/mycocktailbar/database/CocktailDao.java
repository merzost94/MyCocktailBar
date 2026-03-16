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
    // Коктейли
    @Query("SELECT * FROM cocktails WHERE id = :id")
    LiveData<Cocktail> getCocktailById(long id);

    @Query("SELECT * FROM cocktails ORDER BY name")
    LiveData<List<Cocktail>> getAllCocktails();

    @Query("SELECT * FROM cocktails WHERE name LIKE :search OR description LIKE :search OR category LIKE :search")
    LiveData<List<Cocktail>> searchCocktails(String search);

    @Insert
    long insertCocktail(Cocktail cocktail);

    @Query("SELECT * FROM cocktails WHERE category = :category")
    LiveData<List<Cocktail>> getCocktailsByCategory(String category);

    // Доступные коктейли (по ингредиентам)
    @Query("SELECT c.* FROM cocktails c WHERE c.id IN (" +
            "SELECT ci.cocktailId FROM cocktail_ingredient_cross_ref ci " +
            "WHERE ci.ingredientId IN (SELECT id FROM ingredients WHERE hasItem = 1) " +
            "GROUP BY ci.cocktailId " +
            "HAVING COUNT(DISTINCT ci.ingredientId) = " +
            "(SELECT COUNT(*) FROM cocktail_ingredient_cross_ref WHERE cocktailId = ci.cocktailId))")
    LiveData<List<Cocktail>> getAvailableCocktails();

    @Query("SELECT c.* FROM cocktails c WHERE c.id IN (" +
            "SELECT ci.cocktailId FROM cocktail_ingredient_cross_ref ci " +
            "WHERE ci.ingredientId IN (SELECT id FROM ingredients WHERE hasItem = 1) " +
            "GROUP BY ci.cocktailId " +
            "HAVING COUNT(DISTINCT ci.ingredientId) >= " +
            "(SELECT COUNT(*) FROM cocktail_ingredient_cross_ref WHERE cocktailId = ci.cocktailId) - 1 " +
            "AND COUNT(DISTINCT ci.ingredientId) < " +
            "(SELECT COUNT(*) FROM cocktail_ingredient_cross_ref WHERE cocktailId = ci.cocktailId))")
    LiveData<List<Cocktail>> getAlmostAvailableCocktails();

    // Ингредиенты
    @Query("SELECT * FROM ingredients ORDER BY name")
    LiveData<List<Ingredient>> getAllIngredients();

    @Query("SELECT * FROM ingredients WHERE hasItem = 1")
    LiveData<List<Ingredient>> getMyBarIngredients();

    @Query("SELECT * FROM ingredients WHERE name LIKE :search OR category LIKE :search")
    LiveData<List<Ingredient>> searchIngredients(String search);

    @Insert
    long insertIngredient(Ingredient ingredient);

    @Query("UPDATE ingredients SET hasItem = :hasItem WHERE id = :id")
    void updateIngredient(long id, boolean hasItem);

    @Query("SELECT * FROM ingredients WHERE id = :id")
    LiveData<Ingredient> getIngredientById(long id);
}