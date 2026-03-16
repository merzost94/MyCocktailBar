package com.example.mycocktailbar.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.mycocktailbar.R;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.models.CocktailIngredientCrossRef;
import com.example.mycocktailbar.models.Ingredient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Ingredient.class, Cocktail.class, CocktailIngredientCrossRef.class}, version = 17, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CocktailDao cocktailDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "cocktail_db")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                CocktailDao dao = INSTANCE.cocktailDao();
                if (dao.getAnyIngredient() == null) {
                    long gin = dao.insertIngredient(new Ingredient("Джин", "Алкоголь", false, R.drawable.ic_launcher_foreground));
                    long vodka = dao.insertIngredient(new Ingredient("Водка", "Алкоголь", false, R.drawable.ic_launcher_foreground));
                    long rum = dao.insertIngredient(new Ingredient("Ром белый", "Алкоголь", false, R.drawable.ic_launcher_foreground));
                    long tequila = dao.insertIngredient(new Ingredient("Текила", "Алкоголь", false, R.drawable.ic_launcher_foreground));
                    long whiskey = dao.insertIngredient(new Ingredient("Виски", "Алкоголь", false, R.drawable.ic_launcher_foreground));
                    long vermouth = dao.insertIngredient(new Ingredient("Вермут сухой", "Алкоголь", false, R.drawable.ic_launcher_foreground));
                    long triplesec = dao.insertIngredient(new Ingredient("Трипл Сек", "Ликер", false, R.drawable.ic_launcher_foreground));

                    long lime = dao.insertIngredient(new Ingredient("Лайм", "Фрукты", false, R.drawable.ic_launcher_foreground));
                    long orange = dao.insertIngredient(new Ingredient("Апельсин", "Фрукты", false, R.drawable.ic_launcher_foreground));
                    long mint = dao.insertIngredient(new Ingredient("Мята", "Зелень", false, R.drawable.ic_launcher_foreground));

                    long cola = dao.insertIngredient(new Ingredient("Кола", "Напитки", false, R.drawable.ic_launcher_foreground));
                    long tonic = dao.insertIngredient(new Ingredient("Тоник", "Напитки", false, R.drawable.ic_launcher_foreground));
                    long orangeJuice = dao.insertIngredient(new Ingredient("Апельсиновый сок", "Напитки", false, R.drawable.ic_launcher_foreground));
                    long sugar = dao.insertIngredient(new Ingredient("Сахарный сироп", "Сиропы", false, R.drawable.ic_launcher_foreground));

                    long c1 = dao.insertCocktail(new Cocktail("Маргарита", "Смешать текилу, ликер и сок лайма", "Классика"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c1, tequila, "50 мл"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c1, triplesec, "20 мл"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c1, lime, "15 мл"));

                    long c2 = dao.insertCocktail(new Cocktail("Джин-Тоник", "Смешать джин с тоником и добавить лайм", "Лонг"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c2, gin, "50 мл"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c2, tonic, "150 мл"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c2, lime, "1 долька"));

                    long c3 = dao.insertCocktail(new Cocktail("Отвертка", "Водка с апельсиновым соком", "Простые"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c3, vodka, "50 мл"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c3, orangeJuice, "150 мл"));

                    long c4 = dao.insertCocktail(new Cocktail("Куба Либре", "Ром с колой и соком лайма", "Лонг"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c4, rum, "50 мл"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c4, cola, "120 мл"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c4, lime, "10 мл"));

                    long c5 = dao.insertCocktail(new Cocktail("Мохито", "Размять мяту с сахаром, добавить ром и газировку", "Освежающие"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c5, rum, "50 мл"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c5, mint, "6 листьев"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c5, sugar, "10 мл"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c5, lime, "половинка"));

                    long c6 = dao.insertCocktail(new Cocktail("Мартини Драй", "Смешать джин с сухим вермутом", "Крепкие"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c6, gin, "60 мл"));
                    dao.insertRelation(new CocktailIngredientCrossRef(c6, vermouth, "10 мл"));
                }
            });
        }
    };
}