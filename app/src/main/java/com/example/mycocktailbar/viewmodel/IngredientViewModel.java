package com.example.mycocktailbar.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.mycocktailbar.database.IngredientDao;
import com.example.mycocktailbar.database.AppDatabase; // ИЗМЕНЕНО: CocktailDatabase -> AppDatabase
import com.example.mycocktailbar.models.Ingredient; // ИЗМЕНЕНО: model -> models
import java.util.List;
import java.util.ArrayList;

public class IngredientViewModel extends AndroidViewModel {
    private IngredientDao ingredientDao;
    private MediatorLiveData<List<Ingredient>> ingredients = new MediatorLiveData<>();
    private MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    public IngredientViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application); // ИЗМЕНЕНО
        ingredientDao = database.ingredientDao();
        loadIngredients();
    }

    private void loadIngredients() {
        ingredients.removeSource(ingredientDao.getAllIngredients());

        LiveData<List<Ingredient>> source = ingredientDao.getAllIngredients();

        ingredients.addSource(source, ingredientList -> {
            String query = searchQuery.getValue();
            if (query == null || query.isEmpty()) {
                ingredients.setValue(ingredientList);
            } else {
                List<Ingredient> filtered = new ArrayList<>();
                for (Ingredient ingredient : ingredientList) {
                    if (ingredient.getName().toLowerCase().contains(query.toLowerCase())) {
                        filtered.add(ingredient);
                    }
                }
                ingredients.setValue(filtered);
            }
        });
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
        loadIngredients();
    }

    public LiveData<List<Ingredient>> getIngredients() {
        return ingredients;
    }

    public void updateIngredient(Ingredient ingredient) {
        new Thread(() -> ingredientDao.updateIngredient(ingredient)).start();
    }
}