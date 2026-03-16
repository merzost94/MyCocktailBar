package com.example.mycocktailbar.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.mycocktailbar.models.CocktailIngredientCrossRef;
import com.example.mycocktailbar.models.Ingredient;
import java.util.List;

@Dao
public interface CocktailIngredientDao {
    @Insert
    void insert(CocktailIngredientCrossRef crossRef);

    @Query("SELECT i.* FROM ingredients i " +
            "INNER JOIN cocktail_ingredient_cross_ref ci ON i.id = ci.ingredientId " +
            "WHERE ci.cocktailId = :cocktailId")
    List<Ingredient> getIngredientsForCocktail(long cocktailId);

    @Query("SELECT cocktailId FROM cocktail_ingredient_cross_ref WHERE ingredientId = :ingredientId")
    List<Long> getCocktailsForIngredient(long ingredientId);
}