package com.example.mycocktailbar.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.mycocktailbar.database.AppDatabase;
import com.example.mycocktailbar.databinding.ActivityCocktailDetailBinding;
import java.util.stream.Collectors;

public class CocktailDetailActivity extends AppCompatActivity {
    private ActivityCocktailDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCocktailDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        long cocktailId = getIntent().getLongExtra("COCKTAIL_ID", -1);
        if (cocktailId != -1) {
            AppDatabase db = AppDatabase.getDatabase(this);

            db.cocktailDao().getCocktailById(cocktailId).observe(this, cocktail -> {
                if (cocktail != null) {
                    binding.tvName.setText(cocktail.getName());
                    binding.tvRecipe.setText(cocktail.getRecipe());

                    Glide.with(this)
                            .load(cocktail.getImageUrl())
                            .centerCrop()
                            .into(binding.imgDetail);
                }
            });

            db.cocktailDao().getIngredientsForCocktail(cocktailId).observe(this, ingredients -> {
                if (ingredients != null) {
                    String ingredientsText = ingredients.stream()
                            .map(i -> "• " + i.getName())
                            .collect(Collectors.joining("\n"));
                    binding.tvIngredients.setText(ingredientsText);
                }
            });
        }
    }
}