package com.example.mycocktailbar.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.mycocktailbar.database.AppDatabase;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.models.Ingredient;
import java.util.List;

public class CocktailDetailViewModel extends AndroidViewModel {
    private MutableLiveData<Cocktail> cocktail = new MutableLiveData<>();
    private MutableLiveData<List<Ingredient>> ingredients = new MutableLiveData<>();

    public CocktailDetailViewModel(Application application) {
        super(application);
    }

    public void loadCocktail(long cocktailId) {
        AppDatabase database = AppDatabase.getInstance(getApplication());

        // Загружаем коктейль
        database.cocktailDao().getCocktailById(cocktailId).observeForever(cocktail -> {
            this.cocktail.setValue(cocktail);
        });

        // Загружаем ингредиенты
        loadIngredientsForCocktail(cocktailId);
    }

    public void loadIngredientsForCocktail(long cocktailId) {
        AppDatabase database = AppDatabase.getInstance(getApplication());

        new Thread(() -> {
            List<Ingredient> ingredientList = database.cocktailIngredientDao()
                    .getIngredientsForCocktail(cocktailId);
            ingredients.postValue(ingredientList);
        }).start();
    }

    public LiveData<Cocktail> getCocktail() {
        return cocktail;
    }

    public LiveData<List<Ingredient>> getIngredients() {
        return ingredients;
    }
}