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
import com.example.mycocktailbar.viewmodel.IngredientViewModel;
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
    }

    private void setupRecyclerView() {
        adapter = new IngredientAdapter((ingredient, isChecked) -> {
            // Убрали quantity, оставили только id и isChecked
            viewModel.updateIngredient(ingredient.getId(), isChecked);
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
}