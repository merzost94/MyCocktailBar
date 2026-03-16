package com.example.mycocktailbar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mycocktailbar.databinding.FragmentCocktailsBinding;
import com.example.mycocktailbar.viewmodel.CocktailViewModel;
import com.example.mycocktailbar.models.Cocktail;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.widget.SearchView;
import java.util.List;

public class CocktailsFragment extends Fragment {
    private FragmentCocktailsBinding binding;
    private CocktailViewModel viewModel;
    private CocktailAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCocktailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CocktailViewModel.class);

        setupRecyclerView();
        setupSearchView();
        setupTabLayout();

        viewModel.getCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            adapter.submitList(cocktails);
            updateEmptyState(cocktails);
        });
    }

    private void setupRecyclerView() {
        adapter = new CocktailAdapter(cocktail -> {
            Intent intent = new Intent(getActivity(), CocktailDetailActivity.class);
            intent.putExtra("cocktail_id", cocktail.getId());
            startActivity(intent);
        });
        binding.recyclerViewCocktails.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCocktails.setAdapter(adapter);
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

    private void setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.setMode(tab.getPosition() == 1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void updateEmptyState(List<Cocktail> cocktails) {
        if (cocktails == null || cocktails.isEmpty()) {
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