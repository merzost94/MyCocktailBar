package com.example.mycocktailbar.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.models.CocktailIngredientCrossRef;
import com.example.mycocktailbar.models.Ingredient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Ingredient.class, Cocktail.class, CocktailIngredientCrossRef.class}, version = 25, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CocktailDao cocktailDao();
    public abstract IngredientDao ingredientDao();
    public abstract CocktailIngredientDao cocktailIngredientDao();

    private static volatile AppDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "cocktail_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    databaseWriteExecutor.execute(() -> {
                                        AppDatabase database = getInstance(context);
                                        CocktailDao cocktailDao = database.cocktailDao();
                                        IngredientDao ingredientDao = database.ingredientDao();
                                        CocktailIngredientDao crossDao = database.cocktailIngredientDao();

                                        // Добавляем ингредиенты
                                        long vodkaId = ingredientDao.insertIngredient(new Ingredient("Водка", false));
                                        long tequilaId = ingredientDao.insertIngredient(new Ingredient("Текила", false));
                                        long ginId = ingredientDao.insertIngredient(new Ingredient("Джин", false));
                                        long rumId = ingredientDao.insertIngredient(new Ingredient("Ром", false));
                                        long limeJuiceId = ingredientDao.insertIngredient(new Ingredient("Сок лайма", false));
                                        long orangeJuiceId = ingredientDao.insertIngredient(new Ingredient("Апельсиновый сок", false));
                                        long colaId = ingredientDao.insertIngredient(new Ingredient("Кола", false));
                                        long tonicId = ingredientDao.insertIngredient(new Ingredient("Тоник", false));
                                        long mintId = ingredientDao.insertIngredient(new Ingredient("Мята", false));
                                        long sugarId = ingredientDao.insertIngredient(new Ingredient("Сахар", false));
                                        long tripleSecId = ingredientDao.insertIngredient(new Ingredient("Трипл сек", false));
                                        long coffeeLiqueurId = ingredientDao.insertIngredient(new Ingredient("Кофейный ликер", false));

                                        // Добавляем коктейли
                                        long margaritaId = cocktailDao.insertCocktail(new Cocktail(
                                                "Маргарита",
                                                "Классический коктейль на основе текилы",
                                                "Классика",
                                                "1. Кромку бокала смочить соком лайма и окунуть в соль.\n2. Смешать в шейкере текилу, трипл сек и сок лайма.\n3. Процедить в бокал со льдом.",
                                                "https://www.thecocktaildb.com/images/media/drink/5noda61589575158.jpg"
                                        ));

                                        long ginTonicId = cocktailDao.insertCocktail(new Cocktail(
                                                "Джин-Тоник",
                                                "Освежающий коктейль с джином и тоником",
                                                "Лонг",
                                                "1. Наполнить хайбол льдом.\n2. Налить джин, сверху тоник.\n3. Перемешать и украсить долькой лайма.",
                                                "https://www.thecocktaildb.com/images/media/drink/z0omyp1582480501.jpg"
                                        ));

                                        long screwdriverId = cocktailDao.insertCocktail(new Cocktail(
                                                "Отвертка",
                                                "Простой коктейль из водки и апельсинового сока",
                                                "Простые",
                                                "1. В стакан со льдом налить водку.\n2. Долить апельсиновый сок до краев.\n3. Слегка перемешать.",
                                                "https://www.thecocktaildb.com/images/media/drink/8z2p911582486177.jpg"
                                        ));

                                        long cubaLibreId = cocktailDao.insertCocktail(new Cocktail(
                                                "Куба Либре",
                                                "Ром с колой и соком лайма",
                                                "Лонг",
                                                "1. Наполнить стакан льдом.\n2. Налить сок лайма и ром.\n3. Добавить колу и аккуратно перемешать.",
                                                "https://www.thecocktaildb.com/images/media/drink/uuxwvq1448111044.jpg"
                                        ));

                                        long mojitoId = cocktailDao.insertCocktail(new Cocktail(
                                                "Мохито",
                                                "Освежающий коктейль с мятой и ромом",
                                                "Освежающие",
                                                "1. В стакане размять мяту с сахаром и дольками лайма.\n2. Насыпать колотый лед.\n3. Влить ром и долить газировку.",
                                                "https://www.thecocktaildb.com/images/media/drink/metwgh1606770327.jpg"
                                        ));

                                        // Связываем коктейли с ингредиентами
                                        // Маргарита
                                        crossDao.insert(new CocktailIngredientCrossRef(margaritaId, tequilaId));
                                        crossDao.insert(new CocktailIngredientCrossRef(margaritaId, tripleSecId));
                                        crossDao.insert(new CocktailIngredientCrossRef(margaritaId, limeJuiceId));

                                        // Джин-тоник
                                        crossDao.insert(new CocktailIngredientCrossRef(ginTonicId, ginId));
                                        crossDao.insert(new CocktailIngredientCrossRef(ginTonicId, tonicId));
                                        crossDao.insert(new CocktailIngredientCrossRef(ginTonicId, limeJuiceId));

                                        // Отвертка
                                        crossDao.insert(new CocktailIngredientCrossRef(screwdriverId, vodkaId));
                                        crossDao.insert(new CocktailIngredientCrossRef(screwdriverId, orangeJuiceId));

                                        // Куба Либре
                                        crossDao.insert(new CocktailIngredientCrossRef(cubaLibreId, rumId));
                                        crossDao.insert(new CocktailIngredientCrossRef(cubaLibreId, colaId));
                                        crossDao.insert(new CocktailIngredientCrossRef(cubaLibreId, limeJuiceId));

                                        // Мохито
                                        crossDao.insert(new CocktailIngredientCrossRef(mojitoId, rumId));
                                        crossDao.insert(new CocktailIngredientCrossRef(mojitoId, mintId));
                                        crossDao.insert(new CocktailIngredientCrossRef(mojitoId, sugarId));
                                        crossDao.insert(new CocktailIngredientCrossRef(mojitoId, limeJuiceId));
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}