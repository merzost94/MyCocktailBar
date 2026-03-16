package com.example.mycocktailbar.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mycocktailbar.databinding.FragmentIngredientsBinding;
import com.example.mycocktailbar.viewmodel.IngredientViewModel;
import com.example.mycocktailbar.models.Ingredient;
import androidx.appcompat.widget.SearchView;
import java.util.List;

public class IngredientsFragment extends Fragment {
    private FragmentIngredientsBinding binding;
    private IngredientViewModel viewModel;
    private IngredientAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentIngredientsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(IngredientViewModel.class);

        setupRecyclerView();
        setupSearchView();

        viewModel.getIngredients().observe(getViewLifecycleOwner(), ingredients -> {
            adapter.submitList(ingredients);
            updateEmptyState(ingredients);
        });
    }

    private void setupRecyclerView() {
        adapter = new IngredientAdapter((ingredient, isChecked) -> {
            ingredient.setHasItem(isChecked);
            viewModel.updateIngredient(ingredient);
        });

        binding.recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewIngredients.setAdapter(adapter);
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setSearchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.setSearchQuery(newText);
                return true;
            }
        });
    }

    private void updateEmptyState(List<Ingredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            binding.emptyView.setVisibility(View.VISIBLE);
        } else {
            binding.emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}