package com.xlms.librarymanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.model.Member;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<Member> displayList;
    private OnMemberClickListener listener;

    public interface OnMemberClickListener {
        void onMemberClick(Member member);
        void onMemberLongClick(Member member);
    }

    public MemberAdapter(OnMemberClickListener listener) {
        this.listener = listener;
        this.displayList = new ArrayList<>();
    }

    public void submitList(List<Member> newList) {
        this.displayList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = displayList.get(position);

        holder.textViewUserId.setText(member.getUserId());
        holder.textViewName.setText(member.getName());
        holder.textViewEmail.setText(member.getEmail());
        holder.textViewRole.setText(member.getRole());
        holder.textViewMembership.setText(member.getMembershipType());
        holder.textViewCost.setText(String.valueOf((int) member.getCost()));
        holder.textViewStatus.setText(member.getStatus());

        updateStatusBadge(holder.layoutStatus, holder.textViewStatus, member.getStatus());

        holder.itemView.setOnClickListener(v -> listener.onMemberClick(member));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onMemberLongClick(member);
            return true;
        });
    }

    private void updateStatusBadge(LinearLayout layoutStatus, TextView textViewStatus, String status) {
        int bgColor, dotColor, textColor;

        if ("Active".equals(status)) {
            bgColor = R.color.status_active_bg;
            textColor = R.color.status_active_text;
        } else {
            bgColor = R.color.status_deactivated_bg;
            textColor = R.color.status_deactivated_text;
        }

        layoutStatus.setBackgroundTintList(ContextCompat.getColorStateList(layoutStatus.getContext(), bgColor));
        textViewStatus.setTextColor(ContextCompat.getColor(textViewStatus.getContext(), textColor));
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserId, textViewName, textViewEmail, textViewRole;
        TextView textViewMembership, textViewCost, textViewStatus;
        LinearLayout layoutStatus;

        MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserId = itemView.findViewById(R.id.textViewUserId);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            textViewRole = itemView.findViewById(R.id.textViewRole);
            textViewMembership = itemView.findViewById(R.id.textViewMembership);
            textViewCost = itemView.findViewById(R.id.textViewCost);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            layoutStatus = itemView.findViewById(R.id.layoutStatus);
        }
    }
}
