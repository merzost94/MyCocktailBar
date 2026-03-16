package com.example.mycocktailbar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mycocktailbar.databinding.FragmentCocktailsBinding;

public class CocktailsFragment extends Fragment {

    private FragmentCocktailsBinding binding;
    private CocktailAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCocktailsBinding.inflate(inflater, container, false);

        setupRecyclerView();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new CocktailAdapter(cocktail -> {
            Intent intent = new Intent(getContext(), CocktailDetailActivity.class);
            intent.putExtra("COCKTAIL_ID", cocktail.getId());
            startActivity(intent);
        });

        binding.recyclerViewCocktails.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCocktails.setAdapter(adapter);
    }
}