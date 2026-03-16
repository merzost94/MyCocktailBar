package com.example.mycocktailbar.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.mycocktailbar.database.AppDatabase;
import com.example.mycocktailbar.models.Cocktail;
import java.util.List;

public class CocktailViewModel extends AndroidViewModel {
    private AppDatabase database;
    private LiveData<List<Cocktail>> allCocktails;
    private LiveData<List<Cocktail>> availableCocktails;
    private LiveData<List<Cocktail>> almostAvailableCocktails;
    private MutableLiveData<List<Cocktail>> searchResults = new MutableLiveData<>();

    public CocktailViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        allCocktails = database.cocktailDao().getAllCocktails();
        availableCocktails = database.cocktailDao().getAvailableCocktails();
        almostAvailableCocktails = database.cocktailDao().getAlmostAvailableCocktails();
    }

    public LiveData<List<Cocktail>> getAllCocktails() {
        return allCocktails;
    }

    public LiveData<List<Cocktail>> getAvailableCocktails() {
        return availableCocktails;
    }

    public LiveData<List<Cocktail>> getAlmostAvailableCocktails() {
        return almostAvailableCocktails;
    }

    public LiveData<List<Cocktail>> getSearchResults() {
        return searchResults;
    }

    public void searchCocktails(String query) {
        if (query == null || query.trim().isEmpty()) {
            searchResults.setValue(null);
        } else {
            new Thread(() -> {
                List<Cocktail> results = database.cocktailDao().searchCocktailsSync("%" + query + "%");
                searchResults.postValue(results);
            }).start();
        }
    }
}