package com.example.mycocktailbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mycocktailbar.R;
import com.example.mycocktailbar.databinding.ActivityIngredientsBinding;
import com.example.mycocktailbar.database.AppDatabase;
import com.example.mycocktailbar.models.Ingredient;
import com.example.mycocktailbar.ui.IngredientAdapter;
import com.example.mycocktailbar.viewmodels.IngredientViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class IngredientsActivity extends AppCompatActivity {
    private ActivityIngredientsBinding binding;
    private IngredientViewModel viewModel;
    private IngredientAdapter adapter;
    private AppDatabase db;
    private boolean showMyBar = false;

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
        adapter = new IngredientAdapter((ingredient, isChecked) -> {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                db.cocktailDao().updateIngredientAvailability(ingredient.getId(), isChecked);
                if (showMyBar) {
                    runOnUiThread(() -> loadIngredientsByStatus(true));
                }
            });
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(IngredientViewModel.class);

        viewModel.getIngredients().observe(this, ingredients -> {
            adapter.setIngredients(ingredients);
        });
    }

    private void setupListeners() {
        binding.toggleBar.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.btn_my_bar && isChecked) {
                showMyBar = true;
                loadIngredientsByStatus(true);
            } else if (checkedId == R.id.btn_all && isChecked) {
                showMyBar = false;
                viewModel.loadAllIngredients();
            }
        });

        binding.fabAdd.setOnClickListener(v -> showAddIngredientDialog());
    }

    private void loadIngredientsByStatus(boolean hasItem) {
        viewModel.loadIngredientsByStatus(hasItem);
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
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Ингредиент добавлен", Toast.LENGTH_SHORT).show();
                                if (showMyBar) {
                                    loadIngredientsByStatus(true);
                                } else {
                                    viewModel.loadAllIngredients();
                                }
                            });
                        });
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}