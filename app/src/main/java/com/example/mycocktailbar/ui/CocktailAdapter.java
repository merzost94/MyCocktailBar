package com.example.mycocktailbar.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mycocktailbar.databinding.ItemCocktailBinding;
import com.example.mycocktailbar.models.Cocktail; // ИЗМЕНЕНО: model -> models
import java.util.ArrayList;
import java.util.List;

public class CocktailAdapter extends RecyclerView.Adapter<CocktailAdapter.CocktailViewHolder> {
    private List<Cocktail> cocktails = new ArrayList<>();

    @NonNull
    @Override
    public CocktailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCocktailBinding binding = ItemCocktailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CocktailViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CocktailViewHolder holder, int position) {
        Cocktail cocktail = cocktails.get(position);
        holder.bind(cocktail);
    }

    @Override
    public int getItemCount() {
        return cocktails.size();
    }

    public void submitList(List<Cocktail> newList) {
        this.cocktails = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    class CocktailViewHolder extends RecyclerView.ViewHolder {
        private final ItemCocktailBinding binding;

        CocktailViewHolder(ItemCocktailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Cocktail cocktail) {
            binding.setCocktail(cocktail);
            binding.executePendingBindings();
        }
    }
}