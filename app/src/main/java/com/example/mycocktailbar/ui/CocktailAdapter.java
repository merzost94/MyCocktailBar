package com.example.mycocktailbar.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mycocktailbar.databinding.ItemCocktailBinding;
import com.example.mycocktailbar.models.Cocktail;
import java.util.ArrayList;
import java.util.List;

public class CocktailAdapter extends RecyclerView.Adapter<CocktailAdapter.ViewHolder> {
    private List<Cocktail> availableCocktails = new ArrayList<>();
    private List<Cocktail> almostAvailableCocktails = new ArrayList<>();
    private List<Cocktail> fullList = new ArrayList<>();
    private OnCocktailClickListener listener;

    public interface OnCocktailClickListener {
        void onCocktailClick(Cocktail cocktail);
    }

    public CocktailAdapter(OnCocktailClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCocktailBinding binding = ItemCocktailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    public void setAvailableCocktails(List<Cocktail> cocktails) {
        this.availableCocktails = cocktails;
        updateFullList();
    }

    public void setAlmostAvailableCocktails(List<Cocktail> cocktails) {
        this.almostAvailableCocktails = cocktails;
        updateFullList();
    }

    private void updateFullList() {
        fullList.clear();
        fullList.addAll(availableCocktails);
        fullList.addAll(almostAvailableCocktails);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cocktail cocktail = fullList.get(position);
        holder.binding.cocktailName.setText(cocktail.getName());
        holder.binding.cocktailCategory.setText(cocktail.getCategory());
        holder.binding.cocktailDescription.setText(cocktail.getDescription());

        Glide.with(holder.itemView.getContext())
                .load(cocktail.getImageUrl())
                .placeholder(com.example.mycocktailbar.R.drawable.ic_cocktail_placeholder)
                .into(holder.binding.cocktailImage);

        if (availableCocktails.contains(cocktail)) {
            holder.itemView.setAlpha(1.0f);
        } else {
            holder.itemView.setAlpha(0.7f);
        }

        holder.itemView.setOnClickListener(v -> listener.onCocktailClick(cocktail));
    }

    @Override
    public int getItemCount() {
        return fullList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCocktailBinding binding;

        ViewHolder(ItemCocktailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}