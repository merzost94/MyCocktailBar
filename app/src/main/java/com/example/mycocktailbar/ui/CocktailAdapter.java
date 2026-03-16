package com.example.mycocktailbar.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mycocktailbar.R;
import com.example.mycocktailbar.databinding.ItemCocktailBinding;
import com.example.mycocktailbar.models.Cocktail;
import java.util.ArrayList;
import java.util.List;

public class CocktailAdapter extends RecyclerView.Adapter<CocktailAdapter.ViewHolder> {
    private List<Cocktail> availableCocktails = new ArrayList<>();
    private List<Cocktail> almostAvailableCocktails = new ArrayList<>();
    private List<Cocktail> allCocktails = new ArrayList<>();
    private List<Cocktail> currentList = new ArrayList<>();
    private OnCocktailClickListener listener;
    private DisplayMode displayMode = DisplayMode.AVAILABLE;

    public enum DisplayMode {
        AVAILABLE,
        ALL
    }

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
        this.availableCocktails = cocktails != null ? cocktails : new ArrayList<>();
        if (displayMode == DisplayMode.AVAILABLE) {
            updateAvailableList();
        }
    }

    public void setAlmostAvailableCocktails(List<Cocktail> cocktails) {
        this.almostAvailableCocktails = cocktails != null ? cocktails : new ArrayList<>();
        if (displayMode == DisplayMode.AVAILABLE) {
            updateAvailableList();
        }
    }

    public void setAllCocktails(List<Cocktail> cocktails) {
        this.allCocktails = cocktails != null ? cocktails : new ArrayList<>();
        this.displayMode = DisplayMode.ALL;
        this.currentList = this.allCocktails;
        notifyDataSetChanged();
    }

    private void updateAvailableList() {
        displayMode = DisplayMode.AVAILABLE;
        currentList.clear();
        currentList.addAll(availableCocktails);
        currentList.addAll(almostAvailableCocktails);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position >= currentList.size()) return;

        Cocktail cocktail = currentList.get(position);
        if (cocktail == null) return;

        holder.binding.cocktailName.setText(cocktail.getName() != null ? cocktail.getName() : "");
        holder.binding.cocktailCategory.setText(cocktail.getCategory() != null ? cocktail.getCategory() : "");
        holder.binding.cocktailDescription.setText(cocktail.getDescription() != null ? cocktail.getDescription() : "");

        Glide.with(holder.itemView.getContext())
                .load(cocktail.getImageUrl())
                .placeholder(R.drawable.ic_cocktail_placeholder)
                .into(holder.binding.cocktailImage);

        if (displayMode == DisplayMode.AVAILABLE) {
            holder.binding.cocktailStatus.setVisibility(View.VISIBLE);
            if (availableCocktails.contains(cocktail)) {
                holder.binding.cocktailStatus.setText("✅ Можно приготовить");
                holder.binding.cocktailStatus.setTextColor(0xFF4CAF50);
            } else {
                holder.binding.cocktailStatus.setText("⚠️ Почти готов (не хватает 1-2)");
                holder.binding.cocktailStatus.setTextColor(0xFFFFA000);
            }
        } else {
            holder.binding.cocktailStatus.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && cocktail != null) {
                listener.onCocktailClick(cocktail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCocktailBinding binding;

        ViewHolder(ItemCocktailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}