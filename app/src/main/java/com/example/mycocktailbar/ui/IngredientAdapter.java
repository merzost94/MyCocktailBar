package com.example.mycocktailbar.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mycocktailbar.databinding.ItemIngredientBinding;
import com.example.mycocktailbar.models.Ingredient;
import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private List<Ingredient> ingredients = new ArrayList<>();
    private OnIngredientClickListener listener;

    public interface OnIngredientClickListener {
        void onIngredientClick(Ingredient ingredient);
    }

    public IngredientAdapter(OnIngredientClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemIngredientBinding binding = ItemIngredientBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.binding.ingredientName.setText(ingredient.getName());
        holder.binding.ingredientCheckbox.setChecked(ingredient.isHasItem());

        holder.binding.ingredientCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ingredient.setHasItem(isChecked);
            listener.onIngredientClick(ingredient);
        });

        holder.itemView.setOnClickListener(v -> {
            boolean newStatus = !ingredient.isHasItem();
            holder.binding.ingredientCheckbox.setChecked(newStatus);
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemIngredientBinding binding;

        ViewHolder(ItemIngredientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}