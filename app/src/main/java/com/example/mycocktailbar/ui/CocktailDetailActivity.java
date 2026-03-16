package com.example.mycocktailbar.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mycocktailbar.databinding.ActivityCocktailDetailBinding;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.models.Ingredient;
import com.example.mycocktailbar.viewmodel.CocktailDetailViewModel;
import java.util.List;

public class CocktailDetailActivity extends AppCompatActivity {
    private ActivityCocktailDetailBinding binding;
    private CocktailDetailViewModel viewModel;
    private IngredientDetailAdapter adapter;
    private long cocktailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCocktailDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Получаем ID коктейля из Intent
        cocktailId = getIntent().getLongExtra("cocktail_id", -1);
        if (cocktailId == -1) {
            Toast.makeText(this, "Ошибка загрузки коктейля", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(CocktailDetailViewModel.class);

        setupRecyclerView();
        setupToolbar();

        // Наблюдаем за коктейлем
        viewModel.getCocktail().observe(this, cocktail -> {
            if (cocktail != null) {
                binding.setCocktail(cocktail);
            }
        });

        // Наблюдаем за ингредиентами
        viewModel.getIngredients().observe(this, ingredients -> {
            if (ingredients != null) {
                adapter.submitList(ingredients);
                binding.setIngredients(ingredients);
                // Показываем/скрываем пустой текст
                binding.emptyIngredientsText.setVisibility(
                        ingredients.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });

        // Загружаем данные
        viewModel.loadCocktail(cocktailId);
    }

    private void setupRecyclerView() {
        adapter = new IngredientDetailAdapter();
        binding.ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.ingredientsRecyclerView.setAdapter(adapter);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}