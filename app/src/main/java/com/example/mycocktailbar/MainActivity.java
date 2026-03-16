package com.example.mycocktailbar;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.mycocktailbar.adapters.ViewPagerAdapter;
import com.example.mycocktailbar.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            setupViewPager();
            setupBottomNavigation();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при запуске: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupViewPager() {
        try {
            viewPagerAdapter = new ViewPagerAdapter(this);
            binding.viewPager.setAdapter(viewPagerAdapter);

            binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    try {
                        switch (position) {
                            case 0:
                                binding.bottomNavigation.setSelectedItemId(R.id.navigation_cocktails);
                                break;
                            case 1:
                                binding.bottomNavigation.setSelectedItemId(R.id.navigation_ingredients);
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(binding.getRoot(), "Ошибка настройки ViewPager", Snackbar.LENGTH_LONG).show();
        }
    }

    private void setupBottomNavigation() {
        try {
            binding.bottomNavigation.setOnItemSelectedListener(item -> {
                try {
                    int itemId = item.getItemId();
                    if (itemId == R.id.navigation_cocktails) {
                        binding.viewPager.setCurrentItem(0, true);
                        return true;
                    } else if (itemId == R.id.navigation_ingredients) {
                        binding.viewPager.setCurrentItem(1, true);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}