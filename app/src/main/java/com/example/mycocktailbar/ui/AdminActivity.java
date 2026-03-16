package com.example.mycocktailbar.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mycocktailbar.R;
import com.example.mycocktailbar.database.AppDatabase;
import com.example.mycocktailbar.databinding.ActivityAdminBinding;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.models.Ingredient;

public class AdminActivity extends AppCompatActivity {
    private ActivityAdminBinding binding;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getDatabase(this);

        binding.btnAddIngredient.setOnClickListener(v -> saveIngredient());
        binding.btnAddCocktail.setOnClickListener(v -> saveCocktail());
    }

    private void saveIngredient() {
        String name = binding.editName.getText().toString().trim();
        String category = binding.editCategory.getText().toString().trim();

        if (name.isEmpty()) return;

        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.cocktailDao().insertIngredient(new Ingredient(name, category, false, R.drawable.ic_launcher_foreground));
            runOnUiThread(() -> {
                Toast.makeText(this, "Ингредиент добавлен", Toast.LENGTH_SHORT).show();
                binding.editName.setText("");
            });
        });
    }

    private void saveCocktail() {
        String name = binding.editName.getText().toString().trim();
        String desc = binding.editDescription.getText().toString().trim();
        String category = binding.editCategory.getText().toString().trim();

        if (name.isEmpty()) return;

        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.cocktailDao().insertCocktail(new Cocktail(name, desc, category, "", ""));
            runOnUiThread(() -> {
                Toast.makeText(this, "Коктейль добавлен", Toast.LENGTH_SHORT).show();
                binding.editName.setText("");
                binding.editDescription.setText("");
            });
        });
    }
}