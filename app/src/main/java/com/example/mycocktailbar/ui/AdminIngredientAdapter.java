package com.example.mycocktailbar.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mycocktailbar.databinding.ItemAdminIngredientBinding;
import com.example.mycocktailbar.models.Ingredient;
import com.example.mycocktailbar.database.AppDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AdminIngredientAdapter extends RecyclerView.Adapter<AdminIngredientAdapter.IngredientViewHolder> {
    private List<Ingredient> ingredients = new ArrayList<>();
    private OnIngredientActionListener listener;
    private AppDatabase database;

    public interface OnIngredientActionListener {
        void onEdit(Ingredient ingredient);
        void onDelete(Ingredient ingredient);
    }

    public AdminIngredientAdapter(OnIngredientActionListener listener, AppDatabase database) {
        this.listener = listener;
        this.database = database;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminIngredientBinding binding = ItemAdminIngredientBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new IngredientViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void submitList(List<Ingredient> newList) {
        this.ingredients = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final ItemAdminIngredientBinding binding;

        IngredientViewHolder(ItemAdminIngredientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Ingredient ingredient) {
            binding.ingredientName.setText(ingredient.getName());

            // Показываем количество коктейлей с этим ингредиентом
            Executors.newSingleThreadExecutor().execute(() -> {
                List<Long> cocktailIds = database.cocktailIngredientDao()
                        .getCocktailsForIngredient(ingredient.getId());
                int count = cocktailIds.size();

                runOnUiThread(() -> {
                    binding.cocktailCount.setText("Используется в " + count + " коктейлях");
                });
            });

            // Кнопка редактирования
            binding.btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(ingredient);
                }
            });

            // Кнопка удаления
            binding.btnDelete.setOnClickListener(v -> {
                showDeleteConfirmation(ingredient);
            });

            // Клик на всю карточку - редактирование
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(ingredient);
                }
            });
        }

        private void showDeleteConfirmation(Ingredient ingredient) {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle("Удаление ингредиента")
                    .setMessage("Удалить \"" + ingredient.getName() + "\"?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        if (listener != null) {
                            listener.onDelete(ingredient);
                        }
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        }
    }
}