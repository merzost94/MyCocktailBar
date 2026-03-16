package com.example.mycocktailbar;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.mycocktailbar.adapters.ViewPagerAdapter;
import com.example.mycocktailbar.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViewPager();
        setupBottomNavigation();
    }

    private void setupViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(this);
        binding.viewPager.setAdapter(viewPagerAdapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        binding.bottomNavigation.setSelectedItemId(R.id.navigation_cocktails);
                        break;
                    case 1:
                        binding.bottomNavigation.setSelectedItemId(R.id.navigation_ingredients);
                        break;
                }
            }
        });
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_cocktails) {
                binding.viewPager.setCurrentItem(0, true);
                return true;
            } else if (itemId == R.id.navigation_ingredients) {
                binding.viewPager.setCurrentItem(1, true);
                return true;
            }
            return false;
        });
    }
}