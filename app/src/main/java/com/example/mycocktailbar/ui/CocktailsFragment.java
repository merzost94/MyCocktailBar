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
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class CocktailsFragment extends Fragment implements Searchable {
    private FragmentCocktailsBinding binding;
    private CocktailViewModel viewModel;
    private CocktailAdapter adapter;
    private int currentTabPosition = 0; // 0 - доступные, 1 - все коктейли
    private boolean isSearchActive = false;

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

        // Наблюдаем за списком доступных коктейлей
        viewModel.getAvailableCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (currentTabPosition == 0 && !isSearchActive) {
                updateAdapterWithCocktails(cocktails);
            }
        });

        // Наблюдаем за списком почти доступных коктейлей
        viewModel.getAlmostAvailableCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (currentTabPosition == 0 && !isSearchActive) {
                // Этот список автоматически обновится через updateAdapterWithCocktails
                // так как он вызывается из getAvailableCocktails
            }
        });

        // Наблюдаем за списком всех коктейлей
        viewModel.getAllCocktails().observe(getViewLifecycleOwner(), cocktails -> {
            if (currentTabPosition == 1 && !isSearchActive) {
                updateAdapterWithCocktails(cocktails);
            }
        });

        // Наблюдаем за результатами поиска
        viewModel.getSearchResults().observe(getViewLifecycleOwner(), cocktails -> {
            if (isSearchActive && cocktails != null) {
                adapter.setAllCocktails(cocktails);
            }
        });

        // Наблюдаем за текущим списком (для поиска)
        viewModel.getCurrentDisplayList().observe(getViewLifecycleOwner(), cocktails -> {
            if (isSearchActive && cocktails != null && !cocktails.isEmpty()) {
                adapter.setAllCocktails(cocktails);
            } else if (isSearchActive && cocktails != null && cocktails.isEmpty()) {
                // Ничего не найдено, показываем пустой список
                adapter.setAllCocktails(new ArrayList<>());
            }
        });
    }

    private void updateAdapterWithCocktails(List<Cocktail> cocktails) {
        if (cocktails == null) {
            adapter.setAvailableCocktails(new ArrayList<>());
            adapter.setAlmostAvailableCocktails(new ArrayList<>());
            return;
        }

        if (currentTabPosition == 0) {
            // Для вкладки "Доступные" нужно разделить на доступные и почти доступные
            List<Cocktail> available = new ArrayList<>();
            List<Cocktail> almost = new ArrayList<>();

            // Получаем актуальные списки из ViewModel
            List<Cocktail> availableFromDb = viewModel.getAvailableCocktails().getValue();
            List<Cocktail> almostFromDb = viewModel.getAlmostAvailableCocktails().getValue();

            if (availableFromDb != null) {
                available.addAll(availableFromDb);
            }
            if (almostFromDb != null) {
                almost.addAll(almostFromDb);
            }

            adapter.setAvailableCocktails(available);
            adapter.setAlmostAvailableCocktails(almost);
        } else {
            // Для вкладки "Все коктейли" просто показываем все
            adapter.setAllCocktails(cocktails);
        }
    }

    private void setupListeners() {
        if (binding.tabLayout != null) {
            binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    try {
                        // Сбрасываем поиск при переключении табов
                        clearSearch();

                        currentTabPosition = tab.getPosition();

                        if (currentTabPosition == 0) {
                            // Доступные коктейли
                            List<Cocktail> available = viewModel.getAvailableCocktails().getValue();
                            List<Cocktail> almost = viewModel.getAlmostAvailableCocktails().getValue();

                            if (available != null || almost != null) {
                                adapter.setAvailableCocktails(available != null ? available : new ArrayList<>());
                                adapter.setAlmostAvailableCocktails(almost != null ? almost : new ArrayList<>());
                            }
                        } else {
                            // Все коктейли
                            List<Cocktail> all = viewModel.getAllCocktails().getValue();
                            adapter.setAllCocktails(all != null ? all : new ArrayList<>());
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
                clearSearch();
            } else {
                isSearchActive = true;
                viewModel.searchCocktails(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearSearch() {
        isSearchActive = false;
        if (binding.searchInput != null) {
            binding.searchInput.setText("");
        }
        viewModel.clearSearch();

        // Возвращаем отображение в зависимости от текущей вкладки
        if (currentTabPosition == 0) {
            List<Cocktail> available = viewModel.getAvailableCocktails().getValue();
            List<Cocktail> almost = viewModel.getAlmostAvailableCocktails().getValue();
            adapter.setAvailableCocktails(available != null ? available : new ArrayList<>());
            adapter.setAlmostAvailableCocktails(almost != null ? almost : new ArrayList<>());
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