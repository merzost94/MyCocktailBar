package com.example.mycocktailbar.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.mycocktailbar.database.AppDatabase;
import com.example.mycocktailbar.models.Cocktail;
import java.util.ArrayList;
import java.util.List;

public class CocktailViewModel extends AndroidViewModel {
    private AppDatabase database;
    private MutableLiveData<List<Cocktail>> availableCocktails = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<Cocktail>> almostAvailableCocktails = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<Cocktail>> allCocktails = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<Cocktail>> searchResults = new MutableLiveData<>(new ArrayList<>());

    public CocktailViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
    }

    public void loadAllCocktails() {
        database.cocktailDao().getAllCocktails().observeForever(cocktails -> {
            allCocktails.postValue(cocktails != null ? cocktails : new ArrayList<>());
        });
    }

    public void loadAvailableCocktails() {
        database.cocktailDao().getAvailableCocktails().observeForever(cocktails -> {
            availableCocktails.postValue(cocktails != null ? cocktails : new ArrayList<>());
        });

        database.cocktailDao().getAlmostAvailableCocktails().observeForever(cocktails -> {
            almostAvailableCocktails.postValue(cocktails != null ? cocktails : new ArrayList<>());
        });
    }

    public LiveData<List<Cocktail>> getAvailableCocktails() {
        return availableCocktails;
    }

    public LiveData<List<Cocktail>> getAlmostAvailableCocktails() {
        return almostAvailableCocktails;
    }

    public LiveData<List<Cocktail>> getAllCocktails() {
        return allCocktails;
    }

    public LiveData<List<Cocktail>> getSearchResults() {
        return searchResults;
    }

    public void searchCocktails(String query) {
        if (query == null || query.trim().isEmpty()) {
            searchResults.setValue(new ArrayList<>());
        } else {
            database.cocktailDao().searchCocktails("%" + query + "%").observeForever(cocktails -> {
                searchResults.postValue(cocktails != null ? cocktails : new ArrayList<>());
            });
        }
    }
}