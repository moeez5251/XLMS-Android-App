package com.xlms.librarymanagement.ui.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xlms.librarymanagement.R;

public class ClientDashboardMainFragment extends Fragment {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client_dashboard_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewPager);
        bottomNavigation = getActivity().findViewById(R.id.bottomNavigation);

        setupViewPager();
    }

    private void setupViewPager() {
        ClientDashboardPagerAdapter adapter = new ClientDashboardPagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigation.setSelectedItemId(R.id.bottom_client_dashboard);
                        break;
                    case 1:
                        bottomNavigation.setSelectedItemId(R.id.bottom_client_catalog);
                        break;
                    case 2:
                        bottomNavigation.setSelectedItemId(R.id.bottom_client_account);
                        break;
                    case 3:
                        bottomNavigation.setSelectedItemId(R.id.bottom_client_help);
                        break;
                    case 4:
                        bottomNavigation.setSelectedItemId(R.id.bottom_client_notifications);
                        break;
                }
            }
        });

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.bottom_client_dashboard) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (id == R.id.bottom_client_catalog) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (id == R.id.bottom_client_account) {
                viewPager.setCurrentItem(2);
                return true;
            } else if (id == R.id.bottom_client_help) {
                viewPager.setCurrentItem(3);
                return true;
            } else if (id == R.id.bottom_client_notifications) {
                viewPager.setCurrentItem(4);
                return true;
            }
            return false;
        });
    }

    private void logout() {
        new com.xlms.librarymanagement.utils.SessionManager(requireContext()).clearSession();
        android.content.Intent intent = new android.content.Intent(requireActivity(), com.xlms.librarymanagement.ui.login.LoginActivity.class);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    public void setCurrentItem(int item) {
        if (viewPager != null) {
            viewPager.setCurrentItem(item);
        }
    }

    public int getCurrentItem() {
        if (viewPager != null) {
            return viewPager.getCurrentItem();
        }
        return 0;
    }
}