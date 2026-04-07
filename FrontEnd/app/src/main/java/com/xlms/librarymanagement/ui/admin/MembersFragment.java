package com.xlms.librarymanagement.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
    
    private Button buttonAllUsers, buttonActiveUsers, buttonDeactivatedUsers;
    private Button buttonAddUser; // Added declaration

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
        loadDummyData();
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
        
        // Initialize the button
        buttonAddUser = view.findViewById(R.id.buttonAddUser); 
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

    private void loadDummyData() {
        masterMemberList.clear();
        masterMemberList.add(new Member("Md69e1e82", "Moeez", "moeeiz5522@abc.com", "Admin", "English", 0, "Active"));
        masterMemberList.add(new Member("Lx22v9k11", "Sarah Chen", "s.chen@academy.edu", "Faculty", "Research", 120, "Active"));
        masterMemberList.add(new Member("St88m0w42", "Jameson Burke", "j.burke@student.org", "Student", "Standard", 45, "Deactivated"));
        masterMemberList.add(new Member("Ab33x7y91", "Emily Watson", "e.watson@university.edu", "Admin", "English", 0, "Active"));
        masterMemberList.add(new Member("St44n2w88", "Michael Brown", "m.brown@student.org", "Student", "Standard", 45, "Active"));
        masterMemberList.add(new Member("Fa11v5k22", "Dr. Lisa Park", "l.park@academy.edu", "Faculty", "Research", 120, "Active"));
        
        applyFilters();
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
            public void onUserAdded(Member member) {
                masterMemberList.add(0, member);
                applyFilters();
                closeDetailFragment();
                Toast.makeText(requireContext(), "User added: " + member.getName(), Toast.LENGTH_SHORT).show();
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
            public void onUserUpdated(Member updatedMember) {
                int index = masterMemberList.indexOf(member);
                if (index >= 0) {
                    masterMemberList.set(index, updatedMember);
                    applyFilters();
                }
                closeDetailFragment();
            }

            @Override
            public void onUserDeleted(Member member) {
                masterMemberList.remove(member);
                applyFilters();
                closeDetailFragment();
                Toast.makeText(requireContext(), "User deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserStatusChanged(Member member) {
                int index = masterMemberList.indexOf(member);
                if (index >= 0) {
                    masterMemberList.set(index, member);
                    applyFilters();
                }
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