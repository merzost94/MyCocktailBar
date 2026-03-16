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

        try {
            setupRecyclerView();
            setupViewModel();
            setupListeners();

            // По умолчанию выбираем "Доступные"
            if (binding.toggleView != null) {
                binding.toggleView.check(R.id.btn_available);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка загрузки: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupRecyclerView() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupViewModel() {
        try {
            viewModel = new ViewModelProvider(requireActivity()).get(CocktailViewModel.class);

            viewModel.getAvailableCocktails().observe(getViewLifecycleOwner(), cocktails -> {
                try {
                    if (!showAllMode && adapter != null) {
                        adapter.setAvailableCocktails(cocktails != null ? cocktails : new ArrayList<>());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            viewModel.getAlmostAvailableCocktails().observe(getViewLifecycleOwner(), cocktails -> {
                try {
                    if (!showAllMode && adapter != null) {
                        adapter.setAlmostAvailableCocktails(cocktails != null ? cocktails : new ArrayList<>());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            viewModel.getAllCocktails().observe(getViewLifecycleOwner(), cocktails -> {
                try {
                    if (showAllMode && adapter != null) {
                        adapter.setAllCocktails(cocktails != null ? cocktails : new ArrayList<>());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            viewModel.getSearchResults().observe(getViewLifecycleOwner(), cocktails -> {
                try {
                    if (cocktails != null && !cocktails.isEmpty() && adapter != null) {
                        adapter.setAllCocktails(cocktails);
                    } else if (cocktails != null && cocktails.isEmpty()) {
                        Toast.makeText(getContext(), "Ничего не найдено", Toast.LENGTH_SHORT).show();
                        if (showAllMode) {
                            viewModel.loadAllCocktails();
                        } else {
                            viewModel.loadAvailableCocktails();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            viewModel.loadAvailableCocktails();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        try {
            if (binding.btnAll != null) {
                binding.btnAll.setOnClickListener(v -> {
                    try {
                        showAllMode = true;
                        if (binding.btnAvailable != null) {
                            binding.btnAvailable.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.gray_600)));
                        }
                        if (binding.btnAll != null) {
                            binding.btnAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.cocktail_gold)));
                        }
                        viewModel.loadAllCocktails();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            if (binding.btnAvailable != null) {
                binding.btnAvailable.setOnClickListener(v -> {
                    try {
                        showAllMode = false;
                        if (binding.btnAvailable != null) {
                            binding.btnAvailable.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.cocktail_purple)));
                        }
                        if (binding.btnAll != null) {
                            binding.btnAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.gray_600)));
                        }
                        viewModel.loadAvailableCocktails();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        } catch (Exception e) {
            e.printStackTrace();
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