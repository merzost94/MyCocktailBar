package com.example.mycocktailbar.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.models.CocktailIngredientCrossRef;
import com.example.mycocktailbar.models.Ingredient;
import java.util.List;

@Dao
public interface CocktailIngredientDao {
    @Insert
    void insert(CocktailIngredientCrossRef crossRef);

    @Query("SELECT i.* FROM ingredients i " +
            "INNER JOIN cocktail_ingredient_cross_ref c ON i.id = c.ingredientId " +
            "WHERE c.cocktailId = :cocktailId")
    LiveData<List<Ingredient>> getIngredientsForCocktail(long cocktailId);

    @Query("SELECT c.* FROM cocktails c " +
            "WHERE c.id IN (" +
            "    SELECT c2.id FROM cocktails c2 " +
            "    INNER JOIN cocktail_ingredient_cross_ref ci ON c2.id = ci.cocktailId " +
            "    WHERE ci.ingredientId IN (SELECT id FROM ingredients WHERE hasItem = 1) " +
            "    GROUP BY c2.id " +
            "    HAVING COUNT(DISTINCT ci.ingredientId) = (" +
            "        SELECT COUNT(*) FROM cocktail_ingredient_cross_ref WHERE cocktailId = c2.id" +
            "    )" +
            ")")
    LiveData<List<Cocktail>> getAvailableCocktails();

    @Query("SELECT c.* FROM cocktails c " +
            "WHERE c.id IN (" +
            "    SELECT c2.id FROM cocktails c2 " +
            "    INNER JOIN cocktail_ingredient_cross_ref ci ON c2.id = ci.cocktailId " +
            "    WHERE ci.ingredientId IN (SELECT id FROM ingredients WHERE hasItem = 1) " +
            "    GROUP BY c2.id " +
            "    HAVING COUNT(DISTINCT ci.ingredientId) >= (" +
            "        SELECT COUNT(*) FROM cocktail_ingredient_cross_ref WHERE cocktailId = c2.id" +
            "    ) - 1 " +
            "    AND COUNT(DISTINCT ci.ingredientId) < (" +
            "        SELECT COUNT(*) FROM cocktail_ingredient_cross_ref WHERE cocktailId = c2.id" +
            "    )" +
            ")")
    LiveData<List<Cocktail>> getAlmostAvailableCocktails();

    @Query("SELECT COUNT(*) FROM cocktail_ingredient_cross_ref WHERE cocktailId = :cocktailId")
    int getIngredientCountForCocktail(long cocktailId);

    @Query("SELECT COUNT(*) FROM cocktail_ingredient_cross_ref ci " +
            "WHERE ci.cocktailId = :cocktailId AND ci.ingredientId IN (SELECT id FROM ingredients WHERE hasItem = 1)")
    int getAvailableIngredientCountForCocktail(long cocktailId);
}