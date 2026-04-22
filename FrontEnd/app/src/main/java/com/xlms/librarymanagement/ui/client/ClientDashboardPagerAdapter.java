package com.xlms.librarymanagement.ui.client;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ClientDashboardPagerAdapter extends FragmentStateAdapter {

    public ClientDashboardPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ClientDashboardContentFragment();
            case 1:
                return new ClientCatalogFragment();
            case 2:
                return new ClientAccountFragment();
            case 3:
                return new ClientHelpFragment();
            case 4:
                return new ClientNotificationsFragment();
            default:
                return new ClientDashboardContentFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5; // Dashboard, Catalog, Account, Help, Notifications
    }
}
