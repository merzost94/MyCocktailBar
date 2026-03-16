package com.example.mycocktailbar.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.mycocktailbar.database.CocktailDao;
import com.example.mycocktailbar.database.AppDatabase; // ИЗМЕНЕНО: CocktailDatabase -> AppDatabase
import com.example.mycocktailbar.models.Cocktail; // ИЗМЕНЕНО: model -> models
import java.util.List;
import java.util.ArrayList;

public class CocktailViewModel extends AndroidViewModel {
    private CocktailDao cocktailDao;
    private MediatorLiveData<List<Cocktail>> cocktails = new MediatorLiveData<>();
    private MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private MutableLiveData<Boolean> isAvailableMode = new MutableLiveData<>(false);

    public CocktailViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application); // ИЗМЕНЕНО
        cocktailDao = database.cocktailDao();
        loadCocktails();
    }

    private void loadCocktails() {
        cocktails.removeSource(cocktailDao.getAllCocktails());
        cocktails.removeSource(cocktailDao.getAvailableCocktails());

        LiveData<List<Cocktail>> source;
        if (Boolean.TRUE.equals(isAvailableMode.getValue())) {
            source = cocktailDao.getAvailableCocktails();
        } else {
            source = cocktailDao.getAllCocktails();
        }

        cocktails.addSource(source, cocktailList -> {
            String query = searchQuery.getValue();
            if (query == null || query.isEmpty()) {
                cocktails.setValue(cocktailList);
            } else {
                List<Cocktail> filtered = new ArrayList<>();
                for (Cocktail cocktail : cocktailList) {
                    if (cocktail.getName().toLowerCase().contains(query.toLowerCase())) {
                        filtered.add(cocktail);
                    }
                }
                cocktails.setValue(filtered);
            }
        });
    }

    public void setMode(boolean available) {
        if (isAvailableMode.getValue() != available) {
            isAvailableMode.setValue(available);
            loadCocktails();
        }
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
        loadCocktails();
    }

    public LiveData<List<Cocktail>> getCocktails() {
        return cocktails;
    }
}