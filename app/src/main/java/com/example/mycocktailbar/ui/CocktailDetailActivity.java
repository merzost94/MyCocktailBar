package com.example.mycocktailbar.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mycocktailbar.databinding.ActivityCocktailDetailBinding;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.models.Ingredient;
import com.example.mycocktailbar.viewmodels.CocktailDetailViewModel;
import java.util.List;
import java.util.stream.Collectors;

public class CocktailDetailActivity extends AppCompatActivity {
    private ActivityCocktailDetailBinding binding;
    private CocktailDetailViewModel viewModel;
    private Cocktail currentCocktail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCocktailDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        long cocktailId = getIntent().getLongExtra("cocktail_id", -1);
        if (cocktailId == -1) {
            Toast.makeText(this, "Ошибка загрузки коктейля", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(CocktailDetailViewModel.class);
        viewModel.loadCocktail(cocktailId);
        viewModel.loadIngredientsForCocktail(cocktailId);

        setupObservers();
    }

    private void setupObservers() {
        viewModel.getCocktail().observe(this, cocktail -> {
            if (cocktail != null) {
                currentCocktail = cocktail;
                binding.cocktailName.setText(cocktail.getName());
                binding.cocktailDescription.setText(cocktail.getDescription());
                binding.cocktailInstructions.setText(cocktail.getInstructions());
            }
        });

        viewModel.getIngredients().observe(this, ingredients -> {
            if (ingredients != null) {
                String ingredientsText = ingredients.stream()
                        .map(ingredient -> "• " + ingredient.getName())
                        .collect(Collectors.joining("\n"));
                binding.cocktailIngredients.setText(ingredientsText);
            }
        });
    }
}