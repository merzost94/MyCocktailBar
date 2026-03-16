package com.example.mycocktailbar.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.models.CocktailIngredientCrossRef;
import com.example.mycocktailbar.models.Ingredient;

@Database(entities = {Ingredient.class, Cocktail.class, CocktailIngredientCrossRef.class}, version = 19, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CocktailDao cocktailDao();
    public abstract IngredientDao ingredientDao();
    public abstract CocktailIngredientDao cocktailIngredientDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "cocktail_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}