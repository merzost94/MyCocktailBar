package com.example.mycocktailbar.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;              // ДОБАВЛЕНО
import android.view.MenuItem;         // ДОБАВЛЕНО
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mycocktailbar.MainActivity;  // ДОБАВЛЕНО для logout
import com.example.mycocktailbar.R;
import com.example.mycocktailbar.databinding.ActivityAdminBinding;
import com.example.mycocktailbar.databinding.DialogAddIngredientBinding;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.models.Ingredient;
import com.example.mycocktailbar.database.AppDatabase;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AdminActivity extends AppCompatActivity {
    private ActivityAdminBinding binding;
    private AppDatabase database;
    private List<Cocktail> cocktails = new ArrayList<>();
    private List<Ingredient> ingredients = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = AppDatabase.getInstance(this);

        setupToolbar();
        setupTabs();
        loadData();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Админ панель");
        }
    }

    private void setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(new com.google.android.material.tabs.TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(com.google.android.material.tabs.TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    showCocktails();
                } else {
                    showIngredients();
                }
            }

            @Override
            public void onTabUnselected(com.google.android.material.tabs.TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(com.google.android.material.tabs.TabLayout.Tab tab) {}
        });

        binding.fabAdd.setOnClickListener(v -> {
            if (binding.tabLayout.getSelectedTabPosition() == 0) {
                showAddCocktailDialog();
            } else {
                showAddIngredientDialog();
            }
        });
    }

    private void loadData() {
        // Загружаем коктейли
        database.cocktailDao().getAllCocktails().observe(this, list -> {
            cocktails = list;
            if (binding.tabLayout.getSelectedTabPosition() == 0) {
                showCocktails();
            }
        });

        // Загружаем ингредиенты
        database.ingredientDao().getAllIngredients().observe(this, list -> {
            ingredients = list;
            if (binding.tabLayout.getSelectedTabPosition() == 1) {
                showIngredients();
            }
        });
    }

    private void showCocktails() {
        // TODO: показать список коктейлей
        binding.recyclerView.setVisibility(android.view.View.GONE);
        binding.emptyView.setVisibility(android.view.View.VISIBLE);
        binding.emptyView.setText("Нет коктейлей");
    }

    private void showIngredients() {
        // TODO: показать список ингредиентов
        binding.recyclerView.setVisibility(android.view.View.GONE);
        binding.emptyView.setVisibility(android.view.View.VISIBLE);
        binding.emptyView.setText("Нет ингредиентов");
    }

    private void showAddCocktailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить коктейль");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_cocktail, null);
        builder.setView(view);

        TextInputEditText nameInput = view.findViewById(R.id.cocktail_name_input);
        TextInputEditText categoryInput = view.findViewById(R.id.cocktail_category_input);
        TextInputEditText descriptionInput = view.findViewById(R.id.cocktail_description_input);
        TextInputEditText instructionsInput = view.findViewById(R.id.cocktail_instructions_input);
        TextInputEditText imageInput = view.findViewById(R.id.cocktail_image_input);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String category = categoryInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String instructions = instructionsInput.getText().toString().trim();
            String imageUrl = imageInput.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show();
                return;
            }

            Cocktail cocktail = new Cocktail(name, description, category, instructions, imageUrl);

            Executors.newSingleThreadExecutor().execute(() -> {
                database.cocktailDao().insertCocktail(cocktail);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Коктейль добавлен", Toast.LENGTH_SHORT).show();
                });
            });
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void showAddIngredientDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить ингредиент");

        DialogAddIngredientBinding dialogBinding = DialogAddIngredientBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            String name = dialogBinding.ingredientNameInput.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show();
                return;
            }

            Ingredient ingredient = new Ingredient(name, false);

            Executors.newSingleThreadExecutor().execute(() -> {
                database.ingredientDao().insertIngredient(ingredient);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Ингредиент добавлен", Toast.LENGTH_SHORT).show();
                });
            });
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    // Добавь в меню админки
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            MainActivity.logout(this);
            Toast.makeText(this, "Выход из админ-режима", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}