package com.xlms.librarymanagement.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.adapter.MemberAdapter;
import com.xlms.librarymanagement.model.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MembersFragment extends Fragment {

    private RecyclerView recyclerViewMembers;
    private MemberAdapter memberAdapter;
    private List<Member> masterMemberList;
    private TextView textViewTotalUsers;
    private EditText editTextSearch;
    private LinearLayout skeletonContainer;
    
    private Button buttonAllUsers, buttonActiveUsers, buttonDeactivatedUsers;
    private Button buttonAddUser;

    private String currentFilter = "All";
    private String currentSearch = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_members, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        fetchMembers();
        setupSearch();
        setupFilterButtons();
    }

    private void initViews(View view) {
        recyclerViewMembers = view.findViewById(R.id.recyclerViewMembers);
        textViewTotalUsers = view.findViewById(R.id.textViewTotalUsers);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        buttonAllUsers = view.findViewById(R.id.buttonAllUsers);
        buttonActiveUsers = view.findViewById(R.id.buttonActiveUsers);
        buttonDeactivatedUsers = view.findViewById(R.id.buttonDeactivatedUsers);
        buttonAddUser = view.findViewById(R.id.buttonAddUser); 
        skeletonContainer = view.findViewById(R.id.skeletonContainer);
    }

    private void showSkeleton(boolean show) {
        if (skeletonContainer == null) return;
        if (show) {
            skeletonContainer.removeAllViews();
            skeletonContainer.setVisibility(View.VISIBLE);
            recyclerViewMembers.setVisibility(View.GONE);
            
            android.view.animation.Animation shimmerAnim = android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.shimmer_animation);
            for (int i = 0; i < 5; i++) {
                View skeleton = LayoutInflater.from(requireContext()).inflate(R.layout.layout_skeleton_member_item, skeletonContainer, false);
                View shimmerView = skeleton.findViewById(R.id.shimmerView);
                if (shimmerView != null) shimmerView.startAnimation(shimmerAnim);
                skeletonContainer.addView(skeleton);
            }
        } else {
            skeletonContainer.setVisibility(View.GONE);
            recyclerViewMembers.setVisibility(View.VISIBLE);
        }
    }

    private void fetchMembers() {
        showSkeleton(true);
        com.xlms.librarymanagement.api.ApiClient.getApiService(requireContext()).getAllUsers().enqueue(new retrofit2.Callback<List<Member>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Member>> call, retrofit2.Response<List<Member>> response) {
                showSkeleton(false);
                if (response.isSuccessful() && response.body() != null) {
                    masterMemberList.clear();
                    masterMemberList.addAll(response.body());
                    applyFilters();
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch members", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Member>> call, Throwable t) {
                showSkeleton(false);
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        masterMemberList = new ArrayList<>();
        memberAdapter = new MemberAdapter(new MemberAdapter.OnMemberClickListener() {
            @Override
            public void onMemberClick(Member member) {
                openUserInfoFragment(member);
            }

            @Override
            public void onMemberLongClick(Member member) {
                openUserInfoFragment(member);
            }
        });
        recyclerViewMembers.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewMembers.setAdapter(memberAdapter);
    }

    private void setupSearch() {
        editTextSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearch = s.toString().trim().toLowerCase(Locale.ROOT);
                applyFilters();
            }
        });
    }

    private void setupFilterButtons() {
        View.OnClickListener filterListener = v -> {
            if (v == buttonAllUsers) {
                currentFilter = "All";
                updateFilterButtonStyles();
            } else if (v == buttonActiveUsers) {
                currentFilter = "Active";
                updateFilterButtonStyles();
            } else if (v == buttonDeactivatedUsers) {
                currentFilter = "Deactivated";
                updateFilterButtonStyles();
            }
            applyFilters();
        };

        buttonAllUsers.setOnClickListener(filterListener);
        buttonActiveUsers.setOnClickListener(filterListener);
        buttonDeactivatedUsers.setOnClickListener(filterListener);

        // Add User Button
        if (buttonAddUser != null) {
            buttonAddUser.setOnClickListener(v -> openAddUserFragment());
        }
    }

    private void updateFilterButtonStyles() {
        if ("All".equals(currentFilter)) {
            buttonAllUsers.setBackgroundResource(R.drawable.filter_chip_selected);
            buttonAllUsers.setTextColor(getResources().getColor(R.color.white));
            buttonActiveUsers.setBackgroundResource(R.drawable.filter_chip_unselected);
            buttonActiveUsers.setTextColor(getResources().getColor(R.color.on_secondary_fixed_variant));
            buttonDeactivatedUsers.setBackgroundResource(R.drawable.filter_chip_unselected);
            buttonDeactivatedUsers.setTextColor(getResources().getColor(R.color.on_secondary_fixed_variant));
        } else if ("Active".equals(currentFilter)) {
            buttonAllUsers.setBackgroundResource(R.drawable.filter_chip_unselected);
            buttonAllUsers.setTextColor(getResources().getColor(R.color.on_secondary_fixed_variant));
            buttonActiveUsers.setBackgroundResource(R.drawable.filter_chip_selected);
            buttonActiveUsers.setTextColor(getResources().getColor(R.color.white));
            buttonDeactivatedUsers.setBackgroundResource(R.drawable.filter_chip_unselected);
            buttonDeactivatedUsers.setTextColor(getResources().getColor(R.color.on_secondary_fixed_variant));
        } else {
            buttonAllUsers.setBackgroundResource(R.drawable.filter_chip_unselected);
            buttonAllUsers.setTextColor(getResources().getColor(R.color.on_secondary_fixed_variant));
            buttonActiveUsers.setBackgroundResource(R.drawable.filter_chip_unselected);
            buttonActiveUsers.setTextColor(getResources().getColor(R.color.on_secondary_fixed_variant));
            buttonDeactivatedUsers.setBackgroundResource(R.drawable.filter_chip_selected);
            buttonDeactivatedUsers.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void applyFilters() {
        List<Member> filteredList = new ArrayList<>();

        for (Member member : masterMemberList) {
            boolean matchSearch = true;
            boolean matchFilter = true;

            if (!currentSearch.isEmpty()) {
                String name = member.getName().toLowerCase(Locale.ROOT);
                String email = member.getEmail().toLowerCase(Locale.ROOT);
                String id = member.getUserId().toLowerCase(Locale.ROOT);

                matchSearch = name.contains(currentSearch) || 
                              email.contains(currentSearch) || 
                              id.contains(currentSearch);
            }

            if (!"All".equals(currentFilter)) {
                matchFilter = member.getStatus().equalsIgnoreCase(currentFilter);
            }

            if (matchSearch && matchFilter) {
                filteredList.add(member);
            }
        }

        memberAdapter.submitList(filteredList);
        textViewTotalUsers.setText(String.valueOf(filteredList.size()));
    }

    private void resetAllFilters() {
        currentFilter = "All";
        currentSearch = "";
        editTextSearch.setText("");
        updateFilterButtonStyles();
        applyFilters();
        Toast.makeText(requireContext(), "Filters reset", Toast.LENGTH_SHORT).show();
    }

    // Navigation Methods
    private void openAddUserFragment() {
        AddUserFragment fragment = new AddUserFragment();
        fragment.setOnUserActionListener(new AddUserFragment.OnUserActionListener() {
            @Override
            public void onUserAdded() {
                fetchMembers(); // Refresh from server
                closeDetailFragment();
            }

            @Override
            public void onCancel() {
                closeDetailFragment();
            }
        });

        openDetailFragment(fragment);
    }

    private void openUserInfoFragment(Member member) {
        UserInfoFragment fragment = UserInfoFragment.newInstance(member);
        fragment.setOnUserInfoActionListener(new UserInfoFragment.OnUserInfoActionListener() {
            @Override
            public void onUserUpdated() {
                fetchMembers(); // Refresh from server
                closeDetailFragment();
            }

            @Override
            public void onUserDeleted() {
                fetchMembers(); // Refresh from server
                closeDetailFragment();
            }

            @Override
            public void onBack() {
                closeDetailFragment();
            }
        });

        openDetailFragment(fragment);
    }

    private void openDetailFragment(Fragment fragment) {
        if (getActivity() instanceof AdminDashboardActivity) {
            ((AdminDashboardActivity) getActivity()).openDetailScreen(fragment);
        }
    }

    private void closeDetailFragment() {
        if (getActivity() instanceof AdminDashboardActivity) {
            ((AdminDashboardActivity) getActivity()).closeDetailScreen();
        }
    }
}