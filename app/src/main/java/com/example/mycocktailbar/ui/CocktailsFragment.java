package com.example.mycocktailbar.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mycocktailbar.R;
import com.example.mycocktailbar.databinding.FragmentCocktailsBinding;
import com.example.mycocktailbar.viewmodels.CocktailViewModel;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

public class CocktailsFragment extends Fragment implements Searchable {
    private FragmentCocktailsBinding binding;
    private CocktailViewModel viewModel;
    private CocktailAdapter adapter;
    private boolean showAllMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCocktailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupViewModel();
        setupListeners();

        // По умолчанию выбираем "Доступные"
        binding.tabLayout.getTabAt(0).select();
    }

    private void setupRecyclerView() {
        adapter = new CocktailAdapter(cocktail -> {
            if (cocktail != null && cocktail.getId() > 0) {
                startActivity(CocktailDetailActivity.createIntent(requireContext(), cocktail.getId()));
            } else {
                Toast.makeText(getContext(), "Ошибка загрузки коктейля", Toast.LENGTH_SHORT).show();
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CocktailViewModel.class);

        viewModel.getAvailableCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (!showAllMode && cocktails != null) {
                adapter.setAvailableCocktails(cocktails);
            }
        });

        viewModel.getAlmostAvailableCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (!showAllMode && cocktails != null) {
                adapter.setAlmostAvailableCocktails(cocktails);
            }
        });

        viewModel.getAllCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (showAllMode && cocktails != null) {
                adapter.setAllCocktails(cocktails);
            }
        });

        viewModel.getSearchResults().observe(getViewLifecycleOwner(), cocktails -> {
            if (cocktails != null && !cocktails.isEmpty()) {
                adapter.setAllCocktails(cocktails);
            } else if (cocktails != null && cocktails.isEmpty()) {
                Toast.makeText(getContext(), "Ничего не найдено", Toast.LENGTH_SHORT).show();
                if (showAllMode) {
                    viewModel.loadAllCocktails();
                } else {
                    viewModel.loadAvailableCocktails();
                }
            }
        });

        viewModel.loadAvailableCocktails();
    }

    private void setupListeners() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    showAllMode = false;
                    viewModel.loadAvailableCocktails();
                } else {
                    showAllMode = true;
                    viewModel.loadAllCocktails();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.searchButton.setOnClickListener(v -> {
            String query = binding.searchInput.getText().toString().trim();
            performSearch(query);
        });

        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.searchInput.getText().toString().trim();
                performSearch(query);
                return true;
            }
            return false;
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            if (showAllMode) {
                viewModel.loadAllCocktails();
            } else {
                viewModel.loadAvailableCocktails();
            }
        } else {
            viewModel.searchCocktails(query);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void search(String query) {
        performSearch(query);
    }
}