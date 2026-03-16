package com.example.mycocktailbar.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mycocktailbar.R;
import com.example.mycocktailbar.database.AppDatabase;
import com.example.mycocktailbar.databinding.ActivityAdminBinding;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.models.Ingredient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class AdminActivity extends AppCompatActivity {
    private ActivityAdminBinding binding;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);

        binding.btnAddCocktail.setOnClickListener(v -> showAddCocktailDialog());
        binding.btnAddIngredient.setOnClickListener(v -> showAddIngredientDialog());
    }

    private void showAddCocktailDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_cocktail, null);
        TextInputEditText nameInput = dialogView.findViewById(R.id.cocktail_name_input);
        TextInputEditText descInput = dialogView.findViewById(R.id.cocktail_description_input);
        TextInputEditText categoryInput = dialogView.findViewById(R.id.cocktail_category_input);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Добавить коктейль")
                .setView(dialogView)
                .setPositiveButton("Добавить", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String desc = descInput.getText().toString().trim();
                    String category = categoryInput.getText().toString().trim();

                    if (!name.isEmpty() && !desc.isEmpty() && !category.isEmpty()) {
                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            db.cocktailDao().insertCocktail(new Cocktail(name, desc, category, "", ""));
                            runOnUiThread(() -> Toast.makeText(this, "Коктейль добавлен", Toast.LENGTH_SHORT).show());
                        });
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showAddIngredientDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_ingredient_admin, null);
        TextInputEditText nameInput = dialogView.findViewById(R.id.ingredient_name_input);
        TextInputEditText categoryInput = dialogView.findViewById(R.id.ingredient_category_input);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Добавить ингредиент")
                .setView(dialogView)
                .setPositiveButton("Добавить", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    if (!name.isEmpty()) {
                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            db.cocktailDao().insertIngredient(new Ingredient(name, false));
                            runOnUiThread(() -> Toast.makeText(this, "Ингредиент добавлен", Toast.LENGTH_SHORT).show());
                        });
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}