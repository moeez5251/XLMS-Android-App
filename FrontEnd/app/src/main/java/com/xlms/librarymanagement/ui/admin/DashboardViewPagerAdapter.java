package com.xlms.librarymanagement.ui.admin;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * Adapter for ViewPager2 to handle bottom navigation screens
 */
public class DashboardViewPagerAdapter extends FragmentStateAdapter {

    private static final String TAG = "ViewPagerAdapter";
    private final List<String> titles;

    public DashboardViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, 
                                     List<String> titles) {
        super(fragmentActivity);
        this.titles = titles;
        Log.d(TAG, "Adapter created with " + titles.size() + " titles");
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d(TAG, "createFragment called for position: " + position + " (" + titles.get(position) + ")");
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new DashboardContentFragment();
                Log.d(TAG, "Created DashboardContentFragment");
                break;
            case 1:
                fragment = PlaceholderFragment.newInstance("Books");
                Log.d(TAG, "Created Books Placeholder");
                break;
            case 2:
                fragment = PlaceholderFragment.newInstance("Members");
                Log.d(TAG, "Created Members Placeholder");
                break;
            case 3:
                fragment = PlaceholderFragment.newInstance("Alerts");
                Log.d(TAG, "Created Alerts Placeholder");
                break;
            case 4:
                fragment = PlaceholderFragment.newInstance("Profile");
                Log.d(TAG, "Created Profile Placeholder");
                break;
            default:
                fragment = PlaceholderFragment.newInstance("Unknown");
                Log.d(TAG, "Created Unknown Placeholder for position " + position);
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount called, returning: " + titles.size());
        return titles.size();
    }

    public String getTitle(int position) {
        return titles.get(position);
    }
}
