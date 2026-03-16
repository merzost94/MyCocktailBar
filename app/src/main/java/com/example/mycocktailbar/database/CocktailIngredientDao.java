package com.example.mycocktailbar.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.mycocktailbar.models.Cocktail;
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

    @Query("SELECT c.* FROM cocktails c " +
            "INNER JOIN cocktail_ingredient_cross_ref ci ON c.id = ci.cocktailId " +
            "WHERE ci.ingredientId = :ingredientId")
    List<Cocktail> getCocktailsForIngredient(long ingredientId);

    // Добавим метод для проверки
    @Query("SELECT COUNT(*) FROM cocktail_ingredient_cross_ref")
    int getTotalCrossRefCount();

    @Query("SELECT * FROM cocktail_ingredient_cross_ref")
    List<CocktailIngredientCrossRef> getAllCrossRefs();

    @Query("DELETE FROM cocktail_ingredient_cross_ref WHERE cocktailId = :cocktailId")
    void deleteByCocktailId(long cocktailId);
}