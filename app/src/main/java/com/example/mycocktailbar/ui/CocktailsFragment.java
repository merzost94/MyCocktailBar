package com.example.mycocktailbar.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mycocktailbar.R;
import com.example.mycocktailbar.databinding.FragmentCocktailsBinding;
import com.example.mycocktailbar.viewmodels.CocktailViewModel;

public class CocktailsFragment extends Fragment implements Searchable {
    private FragmentCocktailsBinding binding;
    private CocktailViewModel viewModel;
    private CocktailAdapter adapter;
    private boolean showAllCocktails = false;

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
    }

    private void setupRecyclerView() {
        adapter = new CocktailAdapter(cocktail -> {
            startActivity(CocktailDetailActivity.createIntent(requireContext(), cocktail.getId()));
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CocktailViewModel.class);

        viewModel.getAvailableCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (!showAllCocktails) {
                adapter.setAvailableCocktails(cocktails);
            }
        });

        viewModel.getAlmostAvailableCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (!showAllCocktails) {
                adapter.setAlmostAvailableCocktails(cocktails);
            }
        });

        viewModel.getAllCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (showAllCocktails) {
                adapter.setAllCocktails(cocktails);
            }
        });

        viewModel.getSearchResults().observe(getViewLifecycleOwner(), cocktails -> {
            if (cocktails != null && cocktails.size() > 0) {
                adapter.setAllCocktails(cocktails);
            }
        });
    }

    private void setupListeners() {
        binding.btnAll.setOnClickListener(v -> {
            showAllCocktails = true;
            viewModel.loadAllCocktails();
        });

        binding.btnAvailable.setOnClickListener(v -> {
            showAllCocktails = false;
            adapter.setAvailableCocktails(viewModel.getAvailableCocktails().getValue());
            adapter.setAlmostAvailableCocktails(viewModel.getAlmostAvailableCocktails().getValue());
        });

        binding.searchButton.setOnClickListener(v -> {
            String query = binding.searchInput.getText().toString().trim();
            search(query);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void search(String query) {
        if (query.isEmpty()) {
            if (showAllCocktails) {
                viewModel.loadAllCocktails();
            }
        } else {
            viewModel.searchCocktails(query);
        }
    }
}