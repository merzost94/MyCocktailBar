package com.example.mycocktailbar;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mycocktailbar.databinding.ActivityIngredientsBinding;
import com.example.mycocktailbar.ui.IngredientAdapter;
import com.example.mycocktailbar.viewmodel.IngredientViewModel;

public class IngredientsActivity extends AppCompatActivity {
    private ActivityIngredientsBinding binding;
    private IngredientViewModel viewModel;
    private IngredientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIngredientsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(IngredientViewModel.class);

        setupRecyclerView();

        viewModel.getIngredients().observe(this, ingredients -> {
            adapter.submitList(ingredients);
        });
    }

    private void setupRecyclerView() {
        adapter = new IngredientAdapter((ingredient, isChecked) -> {
            ingredient.setHasItem(isChecked);
            viewModel.updateIngredient(ingredient);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }
}