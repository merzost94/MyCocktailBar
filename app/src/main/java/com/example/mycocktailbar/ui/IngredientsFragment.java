package com.example.mycocktailbar.ui;

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
import com.example.mycocktailbar.models.Ingredient;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

public class IngredientsFragment extends Fragment implements Searchable {
    private FragmentWithTabsBinding binding;
    private IngredientAdapter adapter;
    private AppDatabase db;
    private boolean showMyBar = true;
    private String currentQuery = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWithTabsBinding.inflate(inflater, container, false);
        db = AppDatabase.getDatabase(requireContext());

        adapter = new IngredientAdapter(new ArrayList<>(), ingredient -> {
            onIngredientClick(ingredient);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        if (binding.tabLayout.getTabCount() == 0) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Мой Бар"));
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Добавить"));
        }

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                showMyBar = tab.getPosition() == 0;
                loadData(currentQuery);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadData("");
        return binding.getRoot();
    }

    private void loadData(String query) {
        this.currentQuery = query;

        if (query.isEmpty()) {
            db.cocktailDao().getIngredientsByStatus(showMyBar)
                    .observe(getViewLifecycleOwner(), list -> adapter.setIngredients(list));
        } else {
            db.cocktailDao().searchIngredients(query)
                    .observe(getViewLifecycleOwner(), list -> adapter.setIngredients(list));
        }
    }

    private void onIngredientClick(Ingredient ingredient) {
        boolean newStatus = !ingredient.isAvailable();
        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.cocktailDao().updateIngredientAvailability(ingredient.getId(), newStatus);
        });
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