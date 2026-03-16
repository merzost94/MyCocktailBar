package com.example.mycocktailbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mycocktailbar.databinding.ActivityIngredientsBinding;
import com.example.mycocktailbar.database.AppDatabase;
import com.example.mycocktailbar.models.Ingredient;
import com.example.mycocktailbar.ui.IngredientAdapter;
import com.example.mycocktailbar.viewmodels.IngredientViewModel;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class IngredientsActivity extends AppCompatActivity {
    private ActivityIngredientsBinding binding;
    private IngredientViewModel viewModel;
    private IngredientAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIngredientsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);
        setupRecyclerView();
        setupViewModel();
        setupListeners();
    }

    private void setupRecyclerView() {
        adapter = new IngredientAdapter(ingredient -> {
            boolean newStatus = !ingredient.isHasItem();
            AppDatabase.databaseWriteExecutor.execute(() -> {
                db.cocktailDao().updateIngredientAvailability(ingredient.getId(), newStatus);
            });
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(IngredientViewModel.class);

        viewModel.getAllIngredients().observe(this, ingredients -> {
            adapter.setIngredients(ingredients);
        });
    }

    private void setupListeners() {
        binding.toggleBar.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.btn_my_bar && isChecked) {
                viewModel.loadIngredientsByStatus(true);
            } else if (checkedId == R.id.btn_all && isChecked) {
                viewModel.loadAllIngredients();
            }
        });

        binding.fabAdd.setOnClickListener(v -> showAddIngredientDialog());
    }

    private void showAddIngredientDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_ingredient, null);
        TextInputEditText editText = dialogView.findViewById(R.id.ingredient_name_input);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Добавить ингредиент")
                .setView(dialogView)
                .setPositiveButton("Добавить", (dialog, which) -> {
                    String name = editText.getText().toString().trim();
                    if (!name.isEmpty()) {
                        Ingredient newIngredient = new Ingredient(name, false);
                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            db.cocktailDao().insertIngredient(newIngredient);
                            runOnUiThread(() -> Toast.makeText(this, "Ингредиент добавлен", Toast.LENGTH_SHORT).show());
                        });
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}