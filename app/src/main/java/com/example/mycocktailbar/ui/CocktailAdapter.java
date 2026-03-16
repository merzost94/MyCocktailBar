package com.example.mycocktailbar.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mycocktailbar.R;
import com.example.mycocktailbar.databinding.ItemCocktailBinding;
import com.example.mycocktailbar.models.Cocktail;
import java.util.ArrayList;
import java.util.List;

public class CocktailAdapter extends RecyclerView.Adapter<CocktailAdapter.CocktailViewHolder> {

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

    public void setAvailableCocktails(List<Cocktail> cocktails) {
        this.availableCocktails = cocktails != null ? cocktails : new ArrayList<>();
        combineLists();
    }

    public void setAlmostAvailableCocktails(List<Cocktail> cocktails) {
        this.almostAvailableCocktails = cocktails != null ? cocktails : new ArrayList<>();
        combineLists();
    }

    private void combineLists() {
        fullList.clear();
        fullList.addAll(availableCocktails);
        fullList.addAll(almostAvailableCocktails);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CocktailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCocktailBinding binding = ItemCocktailBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CocktailViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CocktailViewHolder holder, int position) {
        Cocktail cocktail = fullList.get(position);
        holder.binding.cocktailName.setText(cocktail.getName());
        holder.binding.cocktailCategory.setText("Категория: " + cocktail.getCategory());

        Glide.with(holder.itemView.getContext())
                .load(cocktail.getImageUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(holder.binding.cocktailImage);

        if (availableCocktails.contains(cocktail)) {
            holder.binding.cocktailStatus.setText("Можно приготовить");
            holder.binding.cocktailStatus.setTextColor(0xFF4CAF50);
        } else {
            holder.binding.cocktailStatus.setText("Почти готов (не хватает 1-2)");
            holder.binding.cocktailStatus.setTextColor(0xFFFFA000);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCocktailClick(cocktail);
        });
    }

    @Override
    public int getItemCount() {
        return fullList.size();
    }

    static class CocktailViewHolder extends RecyclerView.ViewHolder {
        ItemCocktailBinding binding;
        CocktailViewHolder(ItemCocktailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}