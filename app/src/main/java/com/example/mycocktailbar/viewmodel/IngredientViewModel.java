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
    private LiveData<List<Ingredient>> allIngredients;
    private MutableLiveData<List<Ingredient>> searchResults = new MutableLiveData<>();

    public IngredientViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        allIngredients = database.cocktailDao().getAllIngredients();
    }

    public LiveData<List<Ingredient>> getAllIngredients() {
        return allIngredients;
    }

    public LiveData<List<Ingredient>> getSearchResults() {
        return searchResults;
    }

    public void loadAllIngredients() {
        allIngredients = database.cocktailDao().getAllIngredients();
    }

    public void loadIngredientsByStatus(boolean hasItem) {
        allIngredients = database.cocktailDao().getIngredientsByStatus(hasItem);
    }

    public void searchIngredients(String query) {
        if (query == null || query.trim().isEmpty()) {
            searchResults.setValue(null);
        } else {
            database.cocktailDao().searchIngredients("%" + query + "%").observeForever(ingredients -> {
                searchResults.postValue(ingredients);
            });
        }
    }
}