package com.example.mycocktailbar.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.mycocktailbar.ui.CocktailsFragment;
import com.example.mycocktailbar.ui.IngredientsFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        try {
            switch (position) {
                case 0:
                    return new CocktailsFragment();
                case 1:
                    return new IngredientsFragment();
                default:
                    return new CocktailsFragment();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new CocktailsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}