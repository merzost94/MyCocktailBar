package com.example.mycocktailbar.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.mycocktailbar.database.AppDatabase;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.models.Ingredient;
import java.util.List;

public class CocktailDetailViewModel extends AndroidViewModel {
    private AppDatabase database;
    private MutableLiveData<Cocktail> cocktail = new MutableLiveData<>();
    private LiveData<List<Ingredient>> ingredients;

    public CocktailDetailViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
    }

    public void loadCocktail(long cocktailId) {
        database.cocktailDao().getCocktailById(cocktailId).observeForever(cocktailData -> {
            cocktail.postValue(cocktailData);
        });
    }

    public void loadIngredientsForCocktail(long cocktailId) {
        ingredients = database.cocktailIngredientDao().getIngredientsForCocktail(cocktailId);
    }

    public LiveData<Cocktail> getCocktail() {
        return cocktail;
    }

    public LiveData<List<Ingredient>> getIngredients() {
        return ingredients;
    }
}