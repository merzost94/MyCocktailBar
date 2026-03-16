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

        // По умолчанию показываем доступные
        showAllMode = false;
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

        // Наблюдаем за списком доступных коктейлей
        viewModel.getAvailableCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (!showAllMode) {
                if (cocktails != null) {
                    adapter.setAvailableCocktails(cocktails);
                } else {
                    adapter.setAvailableCocktails(new ArrayList<>());
                }
            }
        });

        // Наблюдаем за списком почти доступных коктейлей
        viewModel.getAlmostAvailableCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (!showAllMode) {
                if (cocktails != null) {
                    adapter.setAlmostAvailableCocktails(cocktails);
                } else {
                    adapter.setAlmostAvailableCocktails(new ArrayList<>());
                }
            }
        });

        // Наблюдаем за списком всех коктейлей
        viewModel.getAllCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (showAllMode) {
                if (cocktails != null) {
                    adapter.setAllCocktails(cocktails);
                } else {
                    adapter.setAllCocktails(new ArrayList<>());
                }
            }
        });

        // Наблюдаем за результатами поиска
        viewModel.getSearchResults().observe(getViewLifecycleOwner(), cocktails -> {
            if (cocktails != null && !cocktails.isEmpty()) {
                adapter.setAllCocktails(cocktails);
            } else if (cocktails != null && cocktails.isEmpty()) {
                Toast.makeText(getContext(), "Ничего не найдено", Toast.LENGTH_SHORT).show();
                if (showAllMode) {
                    viewModel.loadAllCocktails();
                }
            }
        });

        // Загружаем начальные данные
        viewModel.loadAvailableCocktails();
    }

    private void setupListeners() {
        // Кнопка "Все коктейли"
        binding.btnAll.setOnClickListener(v -> {
            showAllMode = true;
            binding.btnAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.cocktail_gold)));
            binding.btnAvailable.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.gray_600)));
            viewModel.loadAllCocktails();
        });

        // Кнопка "Доступные"
        binding.btnAvailable.setOnClickListener(v -> {
            showAllMode = false;
            binding.btnAvailable.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.cocktail_purple)));
            binding.btnAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.gray_600)));
            viewModel.loadAvailableCocktails();
        });

        // Кнопка поиска
        binding.searchButton.setOnClickListener(v -> {
            String query = binding.searchInput.getText().toString().trim();
            performSearch(query);
        });

        // Поиск при нажатии Enter на клавиатуре
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