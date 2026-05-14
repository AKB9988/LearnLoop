package com.example.learnloop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learnloop.R;
import com.example.learnloop.models.HelpRequest;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * RecyclerView adapter for Help Request cards on the LearnLoop platform.
 * Handles priority-based styling, AI match scores, session type indicators.
 */
public class BountyAdapter extends RecyclerView.Adapter<BountyAdapter.BountyViewHolder> {

    public interface OnBountyClickListener {
        void onAcceptClicked(HelpRequest request);
    }

    private final List<HelpRequest> requests;
    private final OnBountyClickListener listener;

    public BountyAdapter(List<HelpRequest> requests, OnBountyClickListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BountyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bounty_card, parent, false);
        return new BountyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BountyViewHolder holder, int position) {
        HelpRequest request = requests.get(position);
        holder.bind(request);

        // Animate each card entrance
        holder.itemView.startAnimation(
                AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_up_fade));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    class BountyViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardBounty;
        TextView tvSubjectChip, tvUrgencyBadge, tvBountyTitle, tvBountyDesc;
        TextView tvPosterName, tvTimeAgo, tvCreditsOffered, tvBountyMultiplier, btnAcceptBounty;
        TextView tvSessionType, tvDuration, tvLanguage;
        TextView tvAiMatchScore;
        LinearLayout layoutAiMatch, layoutTags, layoutSessionInfo;
        CircleImageView ivPosterAvatar;

        BountyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBounty = itemView.findViewById(R.id.cardBounty);
            tvSubjectChip = itemView.findViewById(R.id.tvSubjectChip);
            tvUrgencyBadge = itemView.findViewById(R.id.tvUrgencyBadge);
            tvBountyTitle = itemView.findViewById(R.id.tvBountyTitle);
            tvBountyDesc = itemView.findViewById(R.id.tvBountyDesc);
            tvPosterName = itemView.findViewById(R.id.tvPosterName);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvCreditsOffered = itemView.findViewById(R.id.tvCreditsOffered);
            tvBountyMultiplier = itemView.findViewById(R.id.tvBountyMultiplier);
            btnAcceptBounty = itemView.findViewById(R.id.btnAcceptBounty);
            ivPosterAvatar = itemView.findViewById(R.id.ivPosterAvatar);
            layoutTags = itemView.findViewById(R.id.layoutTags);
            layoutAiMatch = itemView.findViewById(R.id.layoutAiMatch);
            tvAiMatchScore = itemView.findViewById(R.id.tvAiMatchScore);
            tvSessionType = itemView.findViewById(R.id.tvSessionType);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvLanguage = itemView.findViewById(R.id.tvLanguage);
            layoutSessionInfo = itemView.findViewById(R.id.layoutSessionInfo);
        }

        void bind(HelpRequest request) {
            tvBountyTitle.setText(request.getTitle());
            tvBountyDesc.setText(request.getDescription());
            tvPosterName.setText(request.getPosterName());
            tvCreditsOffered.setText(request.getEffectiveCredits() + " KC");

            // Subject chip
            if (request.getSubject() != null) {
                tvSubjectChip.setText(request.getSubject());
                tvSubjectChip.setVisibility(View.VISIBLE);
            } else {
                tvSubjectChip.setVisibility(View.GONE);
            }

            // Time ago
            tvTimeAgo.setText(request.getTimeAgo());

            // Session type
            tvSessionType.setText(request.getSessionTypeEmoji() + " " + request.getSessionTypeLabel());

            // Duration
            if (request.getEstimatedDuration() > 0) {
                tvDuration.setText("⏱ " + request.getEstimatedDuration() + " min");
                tvDuration.setVisibility(View.VISIBLE);
            } else {
                tvDuration.setVisibility(View.GONE);
            }

            // Language
            if (request.getPreferredLanguage() != null && !request.getPreferredLanguage().isEmpty()) {
                tvLanguage.setText("🌐 " + request.getPreferredLanguage());
                tvLanguage.setVisibility(View.VISIBLE);
            } else {
                tvLanguage.setVisibility(View.GONE);
            }

            // AI Match Score
            if (request.getAiMatchScore() > 0) {
                layoutAiMatch.setVisibility(View.VISIBLE);
                tvAiMatchScore.setText(request.getAiMatchScore() + "% match");
            } else {
                layoutAiMatch.setVisibility(View.GONE);
            }

            // Handle urgency styling
            if (request.isHighUrgency()) {
                tvUrgencyBadge.setVisibility(View.VISIBLE);
                if ("urgent".equalsIgnoreCase(request.getUrgency())
                        || "critical".equalsIgnoreCase(request.getUrgency())) {
                    tvUrgencyBadge.setText("URGENT");
                    tvUrgencyBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.urgent));
                    tvUrgencyBadge.setBackgroundResource(R.drawable.bg_priority_urgent);
                    cardBounty.setStrokeColor(ContextCompat.getColor(itemView.getContext(), R.color.urgent));
                    cardBounty.setStrokeWidth(2);
                } else {
                    tvUrgencyBadge.setText("HIGH PRIORITY");
                    tvUrgencyBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.accent));
                    tvUrgencyBadge.setBackgroundResource(R.drawable.bg_priority_high);
                    cardBounty.setStrokeColor(ContextCompat.getColor(itemView.getContext(), R.color.accent));
                    cardBounty.setStrokeWidth(2);
                }

                // Show multiplier if > 1
                if (request.getBountyMultiplier() > 1.0) {
                    tvBountyMultiplier.setVisibility(View.VISIBLE);
                    tvBountyMultiplier.setText(String.format(Locale.US, "%.1fx Priority Bonus", request.getBountyMultiplier()));
                }
            } else {
                tvUrgencyBadge.setVisibility(View.GONE);
                tvBountyMultiplier.setVisibility(View.GONE);
                cardBounty.setStrokeColor(ContextCompat.getColor(itemView.getContext(), R.color.divider));
                cardBounty.setStrokeWidth(1);
            }

            // Load poster avatar
            if (request.getPosterAvatarUrl() != null && !request.getPosterAvatarUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(request.getPosterAvatarUrl())
                        .placeholder(android.R.drawable.ic_menu_myplaces)
                        .into(ivPosterAvatar);
            } else {
                ivPosterAvatar.setImageResource(android.R.drawable.ic_menu_myplaces);
            }

            // Populate tags
            layoutTags.removeAllViews();
            if (request.getTags() != null) {
                for (String tag : request.getTags()) {
                    TextView tagView = new TextView(itemView.getContext());
                    tagView.setText(tag);
                    tagView.setTextSize(11);
                    tagView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.primary_light));
                    tagView.setBackgroundResource(R.drawable.bg_tag_chip);
                    tagView.setPadding(24, 8, 24, 8);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMarginEnd(8);
                    tagView.setLayoutParams(params);

                    layoutTags.addView(tagView);
                }
            }

            // Accept click logic
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            boolean isMyRequest = false;
            if (currentUser != null && currentUser.getDisplayName() != null) {
                isMyRequest = currentUser.getDisplayName().equals(request.getPosterName());
            }

            if (isMyRequest) {
                // Always show "Join Room" for your own requests so you can wait for helpers!
                btnAcceptBounty.setVisibility(View.VISIBLE);
                btnAcceptBounty.setText("Join Room");
                btnAcceptBounty.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.success)));
                btnAcceptBounty.setOnClickListener(v -> {
                    if (listener != null) listener.onAcceptClicked(request);
                });
            } else {
                if ("accepted".equalsIgnoreCase(request.getStatus()) || "in_session".equalsIgnoreCase(request.getStatus())) {
                    btnAcceptBounty.setVisibility(View.GONE);
                } else {
                    btnAcceptBounty.setVisibility(View.VISIBLE);
                    btnAcceptBounty.setText("Help Now");
                    btnAcceptBounty.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.primary)));
                    btnAcceptBounty.setOnClickListener(v -> {
                        if (listener != null) listener.onAcceptClicked(request);
                    });
                }
            }
        }
    }
}
