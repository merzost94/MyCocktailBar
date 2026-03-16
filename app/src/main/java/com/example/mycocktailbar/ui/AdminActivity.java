package com.example.mycocktailbar.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mycocktailbar.database.AppDatabase;
import com.example.mycocktailbar.databinding.ActivityAdminBinding;
import com.example.mycocktailbar.models.Cocktail;

public class AdminActivity extends AppCompatActivity {
    private ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSave.setOnClickListener(v -> {
            String name = binding.editName.getText().toString();
            String category = binding.editCategory.getText().toString();
            String instruction = binding.editInstruction.getText().toString();

            if (!name.isEmpty() && !category.isEmpty()) {
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    AppDatabase.getDatabase(this).cocktailDao()
                            .insertCocktail(new Cocktail(name, instruction, category));
                });
                Toast.makeText(this, "Коктейль добавлен!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}