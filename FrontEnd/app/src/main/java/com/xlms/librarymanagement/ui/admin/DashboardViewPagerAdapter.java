package com.xlms.librarymanagement.ui.admin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DashboardViewPagerAdapter extends FragmentStateAdapter {

    public DashboardViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DashboardContentFragment();
            case 1:
                return new ManageBooksFragment();
            case 2:
                return PlaceholderFragment.newInstance("Members");
            case 3:
                return PlaceholderFragment.newInstance("Alerts");
            case 4:
                return PlaceholderFragment.newInstance("Profile");
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
