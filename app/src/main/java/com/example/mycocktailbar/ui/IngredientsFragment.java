package com.example.mycocktailbar.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.mycocktailbar.databinding.FragmentIngredientsBinding;
import com.example.mycocktailbar.models.Ingredient;
import com.example.mycocktailbar.viewmodel.IngredientViewModel;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IngredientsFragment extends Fragment implements Searchable {
    private FragmentIngredientsBinding binding;
    private IngredientViewModel viewModel;
    private IngredientAdapter adapter;
    private int currentTabPosition = 0; // 0 - все, 1 - мой бар
    private List<Ingredient> originalAll = new ArrayList<>();
    private List<Ingredient> originalMyBar = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIngredientsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupViewModel();
        setupListeners();
        setupSearch();
        if (binding.tabLayout != null && binding.tabLayout.getTabCount() > 0) {
            binding.tabLayout.getTabAt(0).select();
        }
    }

    private void setupRecyclerView() {
        adapter = new IngredientAdapter((ingredient, isChecked) -> {
            // Обновляем статус ингредиента
            viewModel.updateIngredient(ingredient.getId(), isChecked);
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(IngredientViewModel.class);

        viewModel.getAllIngredients().observe(getViewLifecycleOwner(), ingredients -> {
            if (ingredients != null) {
                originalAll = ingredients;
                if (currentTabPosition == 0) {
                    adapter.setIngredients(originalAll);
                }
            }
        });

        viewModel.getMyBarIngredients().observe(getViewLifecycleOwner(), ingredients -> {
            if (ingredients != null) {
                originalMyBar = ingredients;
                if (currentTabPosition == 1) {
                    adapter.setIngredients(originalMyBar);
                }
            }
        });
    }

    private void setupListeners() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                binding.searchInput.setText("");
                if (currentTabPosition == 0) {
                    adapter.setIngredients(originalAll);
                } else {
                    adapter.setIngredients(originalMyBar);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupSearch() {
        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString().toLowerCase(Locale.getDefault()));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.searchButton.setOnClickListener(v -> filter(binding.searchInput.getText().toString().toLowerCase(Locale.getDefault())));
        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filter(binding.searchInput.getText().toString().toLowerCase(Locale.getDefault()));
                return true;
            }
            return false;
        });
    }

    private void filter(String query) {
        if (query.isEmpty()) {
            if (currentTabPosition == 0) {
                adapter.setIngredients(originalAll);
            } else {
                adapter.setIngredients(originalMyBar);
            }
            return;
        }

        List<Ingredient> source = (currentTabPosition == 0) ? originalAll : originalMyBar;
        List<Ingredient> filtered = new ArrayList<>();

        for (Ingredient i : source) {
            if (i.getName().toLowerCase().contains(query) ||
                    i.getCategory().toLowerCase().contains(query)) {
                filtered.add(i);
            }
        }

        adapter.setIngredients(filtered);
        if (filtered.isEmpty()) {
            Toast.makeText(getContext(), "Ничего не найдено", Toast.LENGTH_SHORT).show();
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
        filter(query.toLowerCase(Locale.getDefault()));
    }
}