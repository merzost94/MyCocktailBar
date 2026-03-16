package com.example.mycocktailbar.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mycocktailbar.databinding.ItemIngredientBinding;
import com.example.mycocktailbar.models.Ingredient; // ИЗМЕНЕНО: model -> models
import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    private List<Ingredient> ingredients = new ArrayList<>();
    private OnItemCheckedListener listener;

    public interface OnItemCheckedListener {
        void onItemChecked(Ingredient ingredient, boolean isChecked);
    }

    public IngredientAdapter(OnItemCheckedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemIngredientBinding binding = ItemIngredientBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        private final ItemIngredientBinding binding;

        IngredientViewHolder(ItemIngredientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Ingredient ingredient) {
            binding.setIngredient(ingredient);
            binding.checkbox.setOnCheckedChangeListener(null);
            binding.checkbox.setChecked(ingredient.isHasItem());
            binding.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                listener.onItemChecked(ingredient, isChecked);
            });
            binding.executePendingBindings();
        }
    }
}