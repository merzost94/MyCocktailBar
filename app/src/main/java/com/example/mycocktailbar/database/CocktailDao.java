package com.example.mycocktailbar.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.models.Ingredient;
import java.util.List;

@Dao
public interface CocktailDao {
    @Query("SELECT * FROM cocktails WHERE id = :id")
    LiveData<Cocktail> getCocktailById(long id);

    @Query("SELECT * FROM cocktails ORDER BY name")
    LiveData<List<Cocktail>> getAllCocktails();

    @Query("SELECT * FROM cocktails ORDER BY name")
    List<Cocktail> getAllCocktailsSync();

    @Query("SELECT * FROM cocktails WHERE name LIKE :search OR description LIKE :search")
    LiveData<List<Cocktail>> searchCocktails(String search);

    @Query("SELECT * FROM cocktails WHERE name LIKE :search OR description LIKE :search")
    List<Cocktail> searchCocktailsSync(String search);

    @Insert
    long insertCocktail(Cocktail cocktail);

    @Update
    void updateCocktail(Cocktail cocktail);

    @Query("SELECT * FROM cocktails WHERE category = :category")
    LiveData<List<Cocktail>> getCocktailsByCategory(String category);

    @Query("SELECT * FROM ingredients ORDER BY name")
    LiveData<List<Ingredient>> getAllIngredients();

    @Query("SELECT * FROM ingredients ORDER BY name")
    List<Ingredient> getAllIngredientsSync();

    @Query("SELECT * FROM ingredients WHERE hasItem = :hasItem")
    LiveData<List<Ingredient>> getIngredientsByStatus(boolean hasItem);

    @Query("SELECT * FROM ingredients WHERE hasItem = :hasItem")
    List<Ingredient> getIngredientsByStatusSync(boolean hasItem);

    @Query("SELECT * FROM ingredients WHERE name LIKE :search")
    LiveData<List<Ingredient>> searchIngredients(String search);

    @Insert
    long insertIngredient(Ingredient ingredient);

    @Query("UPDATE ingredients SET hasItem = :hasItem WHERE id = :id")
    void updateIngredientAvailability(long id, boolean hasItem);

    // ОСНОВНОЙ ЗАПРОС для доступных коктейлей (все ингредиенты есть)
    @Query("SELECT c.* FROM cocktails c WHERE c.id IN (" +
            "SELECT ci.cocktailId FROM cocktail_ingredient_cross_ref ci " +
            "WHERE ci.ingredientId IN (SELECT id FROM ingredients WHERE hasItem = 1) " +
            "GROUP BY ci.cocktailId " +
            "HAVING COUNT(DISTINCT ci.ingredientId) = " +
            "(SELECT COUNT(*) FROM cocktail_ingredient_cross_ref WHERE cocktailId = ci.cocktailId)) " +
            "ORDER BY c.name")
    LiveData<List<Cocktail>> getAvailableCocktails();

    // Синхронная версия для отладки
    @Query("SELECT c.* FROM cocktails c WHERE c.id IN (" +
            "SELECT ci.cocktailId FROM cocktail_ingredient_cross_ref ci " +
            "WHERE ci.ingredientId IN (SELECT id FROM ingredients WHERE hasItem = 1) " +
            "GROUP BY ci.cocktailId " +
            "HAVING COUNT(DISTINCT ci.ingredientId) = " +
            "(SELECT COUNT(*) FROM cocktail_ingredient_cross_ref WHERE cocktailId = ci.cocktailId)) " +
            "ORDER BY c.name")
    List<Cocktail> getAvailableCocktailsSync();

    // Почти готовые (не хватает 1 ингредиента)
    @Query("SELECT c.* FROM cocktails c WHERE c.id IN (" +
            "SELECT ci.cocktailId FROM cocktail_ingredient_cross_ref ci " +
            "WHERE ci.ingredientId IN (SELECT id FROM ingredients WHERE hasItem = 1) " +
            "GROUP BY ci.cocktailId " +
            "HAVING COUNT(DISTINCT ci.ingredientId) = " +
            "(SELECT COUNT(*) FROM cocktail_ingredient_cross_ref WHERE cocktailId = ci.cocktailId) - 1) " +
            "ORDER BY c.name")
    LiveData<List<Cocktail>> getAlmostAvailableCocktails();

    // Поиск по всем коктейлям
    @Query("SELECT * FROM cocktails WHERE name LIKE '%' || :query || '%' ORDER BY name")
    LiveData<List<Cocktail>> searchAllCocktails(String query);

    // Поиск по доступным коктейлям
    @Query("SELECT * FROM cocktails WHERE id IN (" +
            "SELECT ci.cocktailId FROM cocktail_ingredient_cross_ref ci " +
            "WHERE ci.ingredientId IN (SELECT id FROM ingredients WHERE hasItem = 1) " +
            "GROUP BY ci.cocktailId " +
            "HAVING COUNT(DISTINCT ci.ingredientId) = " +
            "(SELECT COUNT(*) FROM cocktail_ingredient_cross_ref WHERE cocktailId = ci.cocktailId)) " +
            "AND name LIKE '%' || :query || '%' " +
            "ORDER BY name")
    LiveData<List<Cocktail>> searchAvailableCocktails(String query);

    // Получить коктейль по ID (синхронно)
    @Query("SELECT * FROM cocktails WHERE id = :id")
    Cocktail getCocktailByIdSync(long id);

    // Для отладки - проверить количество ингредиентов в коктейле
    @Query("SELECT COUNT(*) FROM cocktail_ingredient_cross_ref WHERE cocktailId = :cocktailId")
    int getTotalIngredientsCount(long cocktailId);

    // Для отладки - проверить сколько ингредиентов есть в баре для этого коктейля
    @Query("SELECT COUNT(*) FROM cocktail_ingredient_cross_ref ci " +
            "WHERE ci.cocktailId = :cocktailId AND ci.ingredientId IN " +
            "(SELECT id FROM ingredients WHERE hasItem = 1)")
    int getAvailableIngredientsCount(long cocktailId);

    @Query("DELETE FROM cocktails WHERE id = :id")
    void deleteCocktailById(long id);

    @Query("DELETE FROM cocktail_ingredient_cross_ref WHERE cocktailId = :cocktailId")
    void deleteIngredientsForCocktail(long cocktailId);
}