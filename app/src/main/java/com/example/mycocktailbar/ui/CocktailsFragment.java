package com.example.mycocktailbar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mycocktailbar.Searchable;
import com.example.mycocktailbar.database.AppDatabase;
import com.example.mycocktailbar.databinding.FragmentWithTabsBinding;
import com.example.mycocktailbar.models.Cocktail;

import java.util.ArrayList;

public class CocktailsFragment extends Fragment implements Searchable {
    private FragmentWithTabsBinding binding;
    private AppDatabase db;
    private CocktailAdapter adapter;
    private String currentQuery = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWithTabsBinding.inflate(inflater, container, false);
        db = AppDatabase.getDatabase(requireContext());

        adapter = new CocktailAdapter(cocktail -> {
            Intent intent = new Intent(getContext(), CocktailDetailActivity.class);
            intent.putExtra("COCKTAIL_ID", cocktail.getId());
            startActivity(intent);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        binding.tabLayout.setVisibility(View.GONE);

        if (binding.searchView != null) {
            binding.searchView.setVisibility(View.GONE);
        }

        loadData("");
        return binding.getRoot();
    }

    private void loadData(String query) {
        this.currentQuery = query;
        if (query.isEmpty()) {
            db.cocktailDao().getAvailableCocktails().observe(getViewLifecycleOwner(), list -> {
                if (list != null) adapter.setAvailableCocktails(list);
            });

            db.cocktailDao().getAlmostAvailableCocktails().observe(getViewLifecycleOwner(), list -> {
                if (list != null) adapter.setAlmostAvailableCocktails(list);
            });
        } else {
            db.cocktailDao().searchCocktails(query).observe(getViewLifecycleOwner(), list -> {
                if (list != null) {
                    adapter.setAvailableCocktails(list);
                    adapter.setAlmostAvailableCocktails(new ArrayList<>());
                }
            });
        }
    }

    @Override
    public void filter(String text) {
        loadData(text);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}