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
    private boolean isAllMode = false;
    private String lastSearchQuery = "";

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
            if (!isAllMode && lastSearchQuery.isEmpty()) {
                updateAvailableTab();
            }
        });

        viewModel.getAlmostAvailableCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (!isAllMode && lastSearchQuery.isEmpty()) {
                updateAvailableTab();
            }
        });

        viewModel.getAllCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (isAllMode && lastSearchQuery.isEmpty()) {
                adapter.showAllCocktails(cocktails != null ? cocktails : new ArrayList<>());
            }
        });

        viewModel.getSearchResults().observe(getViewLifecycleOwner(), cocktails -> {
            if (!lastSearchQuery.isEmpty()) {
                if (cocktails != null && !cocktails.isEmpty()) {
                    adapter.showSearchResults(cocktails);
                } else {
                    Toast.makeText(getContext(), "Ничего не найдено", Toast.LENGTH_SHORT).show();
                    clearSearch();
                }
            }
        });
    }

    private void updateAvailableTab() {
        List<Cocktail> available = viewModel.getAvailableCocktails().getValue();
        List<Cocktail> almost = viewModel.getAlmostAvailableCocktails().getValue();
        adapter.showAvailableCocktails(
                available != null ? available : new ArrayList<>(),
                almost != null ? almost : new ArrayList<>()
        );
    }

    private void setupListeners() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                clearSearch();
                isAllMode = tab.getPosition() == 1;
                if (isAllMode) {
                    viewModel.loadAllCocktails();
                } else {
                    viewModel.loadAvailableCocktails();
                    updateAvailableTab();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.searchButton.setOnClickListener(v -> performSearch());
        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String query = binding.searchInput.getText().toString().trim();
        if (query.isEmpty()) {
            clearSearch();
        } else {
            lastSearchQuery = query;
            viewModel.searchCocktails(query);
        }
    }

    private void clearSearch() {
        lastSearchQuery = "";
        binding.searchInput.setText("");
        if (isAllMode) {
            List<Cocktail> all = viewModel.getAllCocktails().getValue();
            adapter.showAllCocktails(all != null ? all : new ArrayList<>());
        } else {
            updateAvailableTab();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void search(String query) {
        binding.searchInput.setText(query);
        performSearch();
    }
}