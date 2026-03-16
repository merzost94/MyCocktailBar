package com.example.mycocktailbar.ui; // или твой пакет

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
// Импорт должен вести на пакет твоего приложения + .databinding.НазваниеФайлаBinding
import com.example.mycocktailbar.databinding.ActivityCocktailDetailBinding;

public class CocktailDetailActivity extends AppCompatActivity {
    private ActivityCocktailDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCocktailDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        long id = getIntent().getLongExtra("COCKTAIL_ID", -1);
    }
}