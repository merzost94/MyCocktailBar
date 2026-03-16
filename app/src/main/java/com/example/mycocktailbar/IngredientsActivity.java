package com.example.mycocktailbar;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mycocktailbar.database.AppDatabase;
import com.example.mycocktailbar.databinding.ActivityIngredientsBinding;
import com.example.mycocktailbar.models.Ingredient;
import com.example.mycocktailbar.ui.IngredientAdapter;
import java.util.ArrayList;

public class IngredientsActivity extends AppCompatActivity {

    private ActivityIngredientsBinding binding;
    private IngredientAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIngredientsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getDatabase(this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Мой Бар");
        }

        adapter = new IngredientAdapter(new ArrayList<>(), ingredient -> {
        });

        binding.recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewIngredients.setAdapter(adapter);

        db.cocktailDao().getIngredientsByStatus(true).observe(this, ingredients -> {
            if (ingredients != null) {
                adapter.setIngredients(ingredients);
            }
        });

        binding.fabAddIngredient.setOnClickListener(v -> showAddIngredientDialog());
    }

    private void showAddIngredientDialog() {
        String[] allNames = getResources().getStringArray(R.array.base_ingredients);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить в бар");
        builder.setItems(allNames, (dialog, which) -> addIngredientToDb(allNames[which]));
        builder.show();
    }

    private void addIngredientToDb(String name) {
        Ingredient newIngredient = new Ingredient(name, "Base", true, R.drawable.ic_launcher_foreground);
        AppDatabase.databaseWriteExecutor.execute(() -> db.cocktailDao().insertIngredient(newIngredient));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}