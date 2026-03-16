package com.example.mycocktailbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.mycocktailbar.R;
import com.example.mycocktailbar.databinding.ActivityCocktailDetailBinding;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.models.Ingredient;
import com.example.mycocktailbar.viewmodels.CocktailDetailViewModel;
import java.util.List;
import java.util.stream.Collectors;

public class CocktailDetailActivity extends AppCompatActivity {
    private ActivityCocktailDetailBinding binding;
    private CocktailDetailViewModel viewModel;
    private long cocktailId = -1;

    public static Intent createIntent(Context context, long cocktailId) {
        Intent intent = new Intent(context, CocktailDetailActivity.class);
        intent.putExtra("cocktail_id", cocktailId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCocktailDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cocktailId = getIntent().getLongExtra("cocktail_id", -1);

        if (cocktailId == -1) {
            Toast.makeText(this, "Ошибка: коктейль не найден", Toast.LENGTH_SHORT).show();
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
                binding.cocktailName.setText(cocktail.getName());
                binding.cocktailDescription.setText(cocktail.getDescription());
                binding.cocktailInstructions.setText(cocktail.getInstructions());

                // Загружаем изображение
                Glide.with(this)
                        .load(cocktail.getImageUrl())
                        .placeholder(R.drawable.ic_cocktail_placeholder)
                        .error(R.drawable.ic_cocktail_placeholder)
                        .into(binding.cocktailImage);
            } else {
                Toast.makeText(this, "Коктейль не найден в базе данных", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getIngredients().observe(this, ingredients -> {
            if (ingredients != null && !ingredients.isEmpty()) {
                String ingredientsText = ingredients.stream()
                        .map(ingredient -> "• " + ingredient.getName())
                        .collect(Collectors.joining("\n"));
                binding.cocktailIngredients.setText(ingredientsText);
            } else {
                binding.cocktailIngredients.setText("Ингредиенты не указаны");
            }
        });
    }
}