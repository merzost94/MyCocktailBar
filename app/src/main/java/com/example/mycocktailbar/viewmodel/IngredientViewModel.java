package com.example.mycocktailbar.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.mycocktailbar.database.AppDatabase;
import com.example.mycocktailbar.models.Ingredient;
import java.util.List;

public class IngredientViewModel extends AndroidViewModel {
    private AppDatabase database;
    private MutableLiveData<List<Ingredient>> ingredients = new MutableLiveData<>();

    public IngredientViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        loadAllIngredients();
    }

    public LiveData<List<Ingredient>> getIngredients() {
        return ingredients;
    }

    public void loadAllIngredients() {
        database.cocktailDao().getAllIngredients().observeForever(ingredientList -> {
            ingredients.postValue(ingredientList);
        });
    }

    public void loadIngredientsByStatus(boolean hasItem) {
        database.cocktailDao().getIngredientsByStatus(hasItem).observeForever(ingredientList -> {
            ingredients.postValue(ingredientList);
        });
    }

    public void searchIngredients(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadAllIngredients();
        } else {
            database.cocktailDao().searchIngredients("%" + query + "%").observeForever(ingredientList -> {
                ingredients.postValue(ingredientList);
            });
        }
    }
}