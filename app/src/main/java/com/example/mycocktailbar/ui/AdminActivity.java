package com.example.mycocktailbar.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mycocktailbar.MainActivity;
import com.example.mycocktailbar.R;
import com.example.mycocktailbar.databinding.ActivityAdminBinding;
import com.example.mycocktailbar.databinding.DialogAddIngredientBinding;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.models.CocktailIngredientCrossRef;
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
    private CocktailAdapter cocktailAdapter;
    private AdminIngredientAdapter ingredientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = AppDatabase.getInstance(this);

        setupToolbar();
        setupTabs();
        setupRecyclerViews();
        loadData();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Админ панель");
        }
    }

    private void setupRecyclerViews() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cocktailAdapter = new CocktailAdapter(cocktail -> {
            showEditCocktailDialog(cocktail);
        });

        ingredientAdapter = new AdminIngredientAdapter(new AdminIngredientAdapter.OnIngredientActionListener() {
            @Override
            public void onEdit(Ingredient ingredient) {
                showEditIngredientDialog(ingredient);
            }

            @Override
            public void onDelete(Ingredient ingredient) {
                deleteIngredient(ingredient);
            }
        }, database);
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
        database.cocktailDao().getAllCocktails().observe(this, list -> {
            cocktails = list;
            if (binding.tabLayout.getSelectedTabPosition() == 0) {
                showCocktails();
            }
        });

        database.ingredientDao().getAllIngredients().observe(this, list -> {
            ingredients = list;
            if (binding.tabLayout.getSelectedTabPosition() == 1) {
                showIngredients();
            }
        });
    }

    private void showCocktails() {
        binding.recyclerView.setAdapter(cocktailAdapter);
        cocktailAdapter.submitList(cocktails);

        if (cocktails.isEmpty()) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.emptyView.setVisibility(View.VISIBLE);
            binding.emptyView.setText("Нет коктейлей");
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.emptyView.setVisibility(View.GONE);
        }
    }

    private void showIngredients() {
        binding.recyclerView.setAdapter(ingredientAdapter);
        ingredientAdapter.submitList(ingredients);

        if (ingredients.isEmpty()) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.emptyView.setVisibility(View.VISIBLE);
            binding.emptyView.setText("Нет ингредиентов");
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.emptyView.setVisibility(View.GONE);
        }
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

        builder.setPositiveButton("Далее", (dialog, which) -> {
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
            showIngredientSelectionDialog(cocktail, false);
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void showEditCocktailDialog(Cocktail cocktail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Редактировать коктейль");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_cocktail, null);
        builder.setView(view);

        TextInputEditText nameInput = view.findViewById(R.id.cocktail_name_input);
        TextInputEditText categoryInput = view.findViewById(R.id.cocktail_category_input);
        TextInputEditText descriptionInput = view.findViewById(R.id.cocktail_description_input);
        TextInputEditText instructionsInput = view.findViewById(R.id.cocktail_instructions_input);
        TextInputEditText imageInput = view.findViewById(R.id.cocktail_image_input);

        nameInput.setText(cocktail.getName());
        categoryInput.setText(cocktail.getCategory());
        descriptionInput.setText(cocktail.getDescription());
        instructionsInput.setText(cocktail.getInstructions());
        imageInput.setText(cocktail.getImageUrl());

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String category = categoryInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String instructions = instructionsInput.getText().toString().trim();
            String imageUrl = imageInput.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show();
                return;
            }

            cocktail.setName(name);
            cocktail.setCategory(category);
            cocktail.setDescription(description);
            cocktail.setInstructions(instructions);
            cocktail.setImageUrl(imageUrl);

            showIngredientSelectionDialog(cocktail, true);
        });

        builder.setNegativeButton("Отмена", null);
        builder.setNeutralButton("Удалить", (dialog, which) -> {
            showDeleteCocktailConfirmation(cocktail);
        });

        builder.show();
    }

    private void showIngredientSelectionDialog(Cocktail cocktail, boolean isEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isEdit ? "Редактировать ингредиенты" : "Выберите ингредиенты");

        View view = getLayoutInflater().inflate(R.layout.dialog_select_ingredients, null);
        builder.setView(view);

        androidx.recyclerview.widget.RecyclerView recyclerView = view.findViewById(R.id.ingredientsRecyclerView);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSave = view.findViewById(R.id.btnSave);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AlertDialog dialog = builder.create();
        dialog.show();

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Ingredient> allIngredients = database.ingredientDao().getAllIngredientsSync();
            List<Long> selectedIds = new ArrayList<>();

            if (isEdit) {
                List<Ingredient> cocktailIngredients = database.cocktailIngredientDao()
                        .getIngredientsForCocktail(cocktail.getId());
                for (Ingredient ing : cocktailIngredients) {
                    selectedIds.add(ing.getId());
                }
            }

            List<Long> finalSelectedIds = selectedIds;
            runOnUiThread(() -> {
                IngredientSelectionAdapter adapter = new IngredientSelectionAdapter((ing, isSelected) -> {});

                adapter.submitList(allIngredients);
                adapter.setSelectedIngredients(finalSelectedIds);
                recyclerView.setAdapter(adapter);

                btnCancel.setOnClickListener(v -> dialog.dismiss());

                btnSave.setOnClickListener(v -> {
                    List<Long> selectedIngredientIds = adapter.getSelectedIngredients();
                    saveCocktailWithIngredients(cocktail, selectedIngredientIds, isEdit);
                    dialog.dismiss();
                });
            });
        });
    }

    private void saveCocktailWithIngredients(Cocktail cocktail, List<Long> selectedIngredientIds, boolean isEdit) {
        Executors.newSingleThreadExecutor().execute(() -> {
            long cocktailId;

            if (isEdit) {
                cocktailId = cocktail.getId();
                database.cocktailDao().updateCocktail(cocktail);
                database.cocktailIngredientDao().deleteByCocktailId(cocktailId);
            } else {
                cocktailId = database.cocktailDao().insertCocktail(cocktail);
                cocktail.setId(cocktailId);
            }

            for (long ingredientId : selectedIngredientIds) {
                CocktailIngredientCrossRef crossRef = new CocktailIngredientCrossRef(cocktailId, ingredientId);
                database.cocktailIngredientDao().insert(crossRef);
            }

            int count = selectedIngredientIds.size();
            runOnUiThread(() -> {
                Toast.makeText(this, "Коктейль сохранен с " + count + " ингредиентами", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void showDeleteCocktailConfirmation(Cocktail cocktail) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление коктейля")
                .setMessage("Удалить \"" + cocktail.getName() + "\"?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        database.cocktailIngredientDao().deleteByCocktailId(cocktail.getId());
                        database.cocktailDao().deleteCocktailById(cocktail.getId());
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Коктейль удален", Toast.LENGTH_SHORT).show();
                        });
                    });
                })
                .setNegativeButton("Отмена", null)
                .show();
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

    private void showEditIngredientDialog(Ingredient ingredient) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Редактировать ингредиент");

        DialogAddIngredientBinding dialogBinding = DialogAddIngredientBinding.inflate(getLayoutInflater());
        dialogBinding.ingredientNameInput.setText(ingredient.getName());
        builder.setView(dialogBinding.getRoot());

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String name = dialogBinding.ingredientNameInput.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show();
                return;
            }

            ingredient.setName(name);

            Executors.newSingleThreadExecutor().execute(() -> {
                database.ingredientDao().updateIngredient(ingredient);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Ингредиент обновлен", Toast.LENGTH_SHORT).show();
                });
            });
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void deleteIngredient(Ingredient ingredient) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление ингредиента")
                .setMessage("Удалить \"" + ingredient.getName() + "\"?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        database.ingredientDao().deleteById((int) ingredient.getId());
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Ингредиент удален", Toast.LENGTH_SHORT).show();
                        });
                    });
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

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