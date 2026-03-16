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
import com.example.mycocktailbar.viewmodels.CocktailViewModel;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

public class CocktailsFragment extends Fragment implements Searchable {
    private FragmentCocktailsBinding binding;
    private CocktailViewModel viewModel;
    private CocktailAdapter adapter;
    private boolean showAllMode = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCocktailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            setupRecyclerView();
            setupViewModel();
            setupListeners();

            // По умолчанию выбираем "Доступные"
            if (binding.tabLayout != null && binding.tabLayout.getTabCount() > 0) {
                binding.tabLayout.getTabAt(0).select();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка загрузки: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupRecyclerView() {
        adapter = new CocktailAdapter(cocktail -> {
            if (cocktail != null && cocktail.getId() > 0) {
                startActivity(CocktailDetailActivity.createIntent(requireContext(), cocktail.getId()));
            } else {
                Toast.makeText(getContext(), "Ошибка загрузки коктейля", Toast.LENGTH_SHORT).show();
            }
        });

        if (binding.recyclerView != null) {
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recyclerView.setAdapter(adapter);
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CocktailViewModel.class);

        // Загружаем данные
        viewModel.loadAvailableCocktails();

        // Наблюдаем за списком доступных коктейлей
        if (viewModel.getAvailableCocktails() != null) {
            viewModel.getAvailableCocktails().observe(getViewLifecycleOwner(), cocktails -> {
                if (!showAllMode && cocktails != null) {
                    adapter.setAvailableCocktails(cocktails);
                } else if (!showAllMode) {
                    adapter.setAvailableCocktails(new ArrayList<>());
                }
            });
        }

        // Наблюдаем за списком почти доступных коктейлей
        if (viewModel.getAlmostAvailableCocktails() != null) {
            viewModel.getAlmostAvailableCocktails().observe(getViewLifecycleOwner(), cocktails -> {
                if (!showAllMode && cocktails != null) {
                    adapter.setAlmostAvailableCocktails(cocktails);
                } else if (!showAllMode) {
                    adapter.setAlmostAvailableCocktails(new ArrayList<>());
                }
            });
        }

        // Наблюдаем за списком всех коктейлей
        if (viewModel.getAllCocktails() != null) {
            viewModel.getAllCocktails().observe(getViewLifecycleOwner(), cocktails -> {
                if (showAllMode && cocktails != null) {
                    adapter.setAllCocktails(cocktails);
                } else if (showAllMode) {
                    adapter.setAllCocktails(new ArrayList<>());
                }
            });
        }

        // Наблюдаем за результатами поиска
        if (viewModel.getSearchResults() != null) {
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
        }
    }

    private void setupListeners() {
        if (binding.tabLayout != null) {
            binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    try {
                        if (tab.getPosition() == 0) {
                            showAllMode = false;
                            viewModel.loadAvailableCocktails();
                        } else {
                            showAllMode = true;
                            viewModel.loadAllCocktails();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });
        }

        if (binding.searchButton != null) {
            binding.searchButton.setOnClickListener(v -> {
                String query = binding.searchInput != null ? binding.searchInput.getText().toString().trim() : "";
                performSearch(query);
            });
        }

        if (binding.searchInput != null) {
            binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = binding.searchInput.getText().toString().trim();
                    performSearch(query);
                    return true;
                }
                return false;
            });
        }
    }

    private void performSearch(String query) {
        try {
            if (query.isEmpty()) {
                if (showAllMode) {
                    viewModel.loadAllCocktails();
                } else {
                    viewModel.loadAvailableCocktails();
                }
            } else {
                viewModel.searchCocktails(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
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