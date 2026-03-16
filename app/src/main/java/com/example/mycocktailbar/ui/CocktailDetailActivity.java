package com.example.mycocktailbar.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.mycocktailbar.R;
import com.example.mycocktailbar.databinding.ActivityCocktailDetailBinding;
import com.example.mycocktailbar.models.Cocktail;
import com.example.mycocktailbar.viewmodel.CocktailDetailViewModel;

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

        cocktailId = getIntent().getLongExtra("cocktail_id", -1);
        if (cocktailId == -1) {
            Toast.makeText(this, "Ошибка загрузки коктейля", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(CocktailDetailViewModel.class);

        setupRecyclerView();
        setupToolbar();

        viewModel.getCocktail().observe(this, cocktail -> {
            if (cocktail != null) {
                binding.setCocktail(cocktail);
                loadImage(binding.cocktailImage, cocktail.getImageUrl());
            }
        });

        viewModel.getIngredients().observe(this, ingredients -> {
            if (ingredients != null) {
                adapter.submitList(ingredients);
                binding.setIngredients(ingredients);

                int availableCount = 0;
                for (int i = 0; i < ingredients.size(); i++) {
                    if (ingredients.get(i).isHasItem()) {
                        availableCount++;
                    }
                }

                if (ingredients.isEmpty()) {
                    binding.statusText.setVisibility(android.view.View.GONE);
                } else if (availableCount == ingredients.size()) {
                    binding.statusText.setText("✅ Можно приготовить");
                    binding.statusText.setTextColor(ContextCompat.getColor(this, R.color.status_available));
                    binding.statusText.setVisibility(android.view.View.VISIBLE);
                } else if (availableCount >= ingredients.size() - 2) {
                    binding.statusText.setText("⚠️ Не хватает " + (ingredients.size() - availableCount) + " ингредиентов");
                    binding.statusText.setTextColor(ContextCompat.getColor(this, R.color.status_almost));
                    binding.statusText.setVisibility(android.view.View.VISIBLE);
                } else {
                    binding.statusText.setText("❌ Не хватает " + (ingredients.size() - availableCount) + " ингредиентов");
                    binding.statusText.setTextColor(ContextCompat.getColor(this, R.color.status_unavailable));
                    binding.statusText.setVisibility(android.view.View.VISIBLE);
                }
            }
        });

        viewModel.loadCocktail(cocktailId);
    }

    // ДОБАВЛЕН МЕТОД для загрузки изображения
    private void loadImage(ImageView imageView, String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageView.setVisibility(android.view.View.GONE);
            return;
        }

        imageView.setVisibility(android.view.View.VISIBLE);

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_cocktail_placeholder)
                .error(R.drawable.ic_cocktail_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();

        Glide.with(this)
                .load(imageUrl)
                .apply(options)
                .into(imageView);
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