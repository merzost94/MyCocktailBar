package com.example.mycocktailbar.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.mycocktailbar.models.Cocktail;
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
}