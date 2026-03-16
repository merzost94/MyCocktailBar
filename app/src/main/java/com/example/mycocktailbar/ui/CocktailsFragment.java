package com.example.mycocktailbar.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mycocktailbar.R;
import com.example.mycocktailbar.databinding.FragmentCocktailsBinding;
import com.example.mycocktailbar.viewmodel.CocktailViewModel;
import com.example.mycocktailbar.models.Cocktail;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class CocktailsFragment extends Fragment implements Searchable {
    private FragmentCocktailsBinding binding;
    private CocktailViewModel viewModel;
    private CocktailAdapter adapter;
    private int currentTabPosition = 0;
    private boolean isSearchActive = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCocktailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupViewModel();
        setupListeners();
        if (binding.tabLayout != null && binding.tabLayout.getTabCount() > 0) {
            binding.tabLayout.getTabAt(0).select();
        }
    }

    private void setupRecyclerView() {
        adapter = new CocktailAdapter(cocktail -> {
            if (cocktail != null && cocktail.getId() > 0) {
                startActivity(CocktailDetailActivity.createIntent(requireContext(), cocktail.getId()));
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CocktailViewModel.class);

        viewModel.getAvailableCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (currentTabPosition == 0 && !isSearchActive) {
                updateAvailableTab();
            }
        });

        viewModel.getAlmostAvailableCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (currentTabPosition == 0 && !isSearchActive) {
                updateAvailableTab();
            }
        });

        viewModel.getAllCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (currentTabPosition == 1 && !isSearchActive) {
                adapter.setAllCocktails(cocktails != null ? cocktails : new ArrayList<>());
            }
        });

        viewModel.getSearchResults().observe(getViewLifecycleOwner(), cocktails -> {
            if (isSearchActive && cocktails != null) {
                adapter.setAllCocktails(cocktails);
            }
        });
    }

    private void updateAvailableTab() {
        List<Cocktail> available = viewModel.getAvailableCocktails().getValue();
        List<Cocktail> almost = viewModel.getAlmostAvailableCocktails().getValue();
        adapter.setAvailableCocktails(available != null ? available : new ArrayList<>());
        adapter.setAlmostAvailableCocktails(almost != null ? almost : new ArrayList<>());
    }

    private void setupListeners() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                clearSearch();
                currentTabPosition = tab.getPosition();
                if (currentTabPosition == 0) {
                    updateAvailableTab();
                } else {
                    List<Cocktail> all = viewModel.getAllCocktails().getValue();
                    adapter.setAllCocktails(all != null ? all : new ArrayList<>());
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
                performSearch(binding.searchInput.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            clearSearch();
        } else {
            isSearchActive = true;
            viewModel.searchCocktails(query);
        }
    }

    private void clearSearch() {
        isSearchActive = false;
        binding.searchInput.setText("");
        if (currentTabPosition == 0) {
            updateAvailableTab();
        } else {
            List<Cocktail> all = viewModel.getAllCocktails().getValue();
            adapter.setAllCocktails(all != null ? all : new ArrayList<>());
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