package com.example.mycocktailbar.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.mycocktailbar.database.AppDatabase;
import com.example.mycocktailbar.models.Ingredient;
import java.util.ArrayList;
import java.util.List;

public class IngredientViewModel extends AndroidViewModel {
    private AppDatabase database;
    private MutableLiveData<List<Ingredient>> allIngredients = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<Ingredient>> myBarIngredients = new MutableLiveData<>(new ArrayList<>());

    public IngredientViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        loadAllIngredients();
        loadMyBarIngredients();
    }

    public void loadAllIngredients() {
        database.cocktailDao().getAllIngredients().observeForever(ingredients -> {
            allIngredients.postValue(ingredients != null ? ingredients : new ArrayList<>());
        });
    }

    public void loadMyBarIngredients() {
        database.cocktailDao().getMyBarIngredients().observeForever(ingredients -> {
            myBarIngredients.postValue(ingredients != null ? ingredients : new ArrayList<>());
        });
    }

    public void updateIngredient(long id, boolean hasItem) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.cocktailDao().updateIngredient(id, hasItem);
            // После обновления перезагружаем списки
            loadAllIngredients();
            loadMyBarIngredients();
        });
    }

    public LiveData<List<Ingredient>> getAllIngredients() {
        return allIngredients;
    }

    public LiveData<List<Ingredient>> getMyBarIngredients() {
        return myBarIngredients;
    }
}