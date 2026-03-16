package com.example.mycocktailbar.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mycocktailbar.databinding.FragmentIngredientsBinding;
import com.example.mycocktailbar.database.AppDatabase;
import com.example.mycocktailbar.models.Ingredient;
import com.example.mycocktailbar.viewmodels.IngredientViewModel;

public class IngredientsFragment extends Fragment implements Searchable {
    private FragmentIngredientsBinding binding;
    private IngredientViewModel viewModel;
    private IngredientAdapter adapter;
    private AppDatabase db;
    private boolean showMyBar = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIngredientsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getInstance(requireContext());
        setupRecyclerView();
        setupViewModel();
        setupListeners();
    }

    private void setupRecyclerView() {
        adapter = new IngredientAdapter(ingredient -> {
            boolean newStatus = !ingredient.isHasItem();
            AppDatabase.databaseWriteExecutor.execute(() -> {
                db.cocktailDao().updateIngredientAvailability(ingredient.getId(), newStatus);
            });
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(IngredientViewModel.class);

        viewModel.getAllIngredients().observe(getViewLifecycleOwner(), ingredients -> {
            adapter.setIngredients(ingredients);
        });
    }

    private void setupListeners() {
        binding.toggleBar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showMyBar = isChecked;
            if (isChecked) {
                viewModel.loadIngredientsByStatus(true);
            } else {
                viewModel.loadAllIngredients();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void search(String query) {
        if (query == null || query.trim().isEmpty()) {
            if (showMyBar) {
                viewModel.loadIngredientsByStatus(true);
            } else {
                viewModel.loadAllIngredients();
            }
        } else {
            viewModel.searchIngredients(query);
        }
    }
}