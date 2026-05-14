package com.example.learnloop.fragments;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.learnloop.R;
import com.example.learnloop.models.LeaderboardEntry;
import com.example.learnloop.models.UserWallet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.learnloop.network.RetrofitClient;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Gamified User Dashboard Fragment.
 * Displays: Knowledge Wallet, Teaching Streak, Skill Tree, Campus Leaderboard.
 */
public class DashboardFragment extends Fragment {

    // Wallet views
    private CardView cardWallet;
    private CircleImageView ivWalletAvatar;
    private TextView tvWalletName, tvWalletRank, tvCreditCount;
    private TextView tvTotalEarned, tvTotalSpent, tvSessionsCount;
    private LinearLayout layoutKarmaBadge;
    private TextView tvKarmaMultiplier;

    // Streak views
    private CardView cardStreak;
    private TextView tvStreakFire, tvStreakCount, tvStreakLabel, tvStreakMessage;
    private LinearLayout layoutStreakKarma;
    private TextView tvStreakKarma;

    // Skill tree views
    private LinearLayout layoutSkillTree;
    private View nodeNovice, nodeScholar, nodeExpert, nodeGrandmaster;
    private View connectorNS, connectorSE, connectorEG;
    private TextView tvPrimarySkill, tvCurrentLevel, tvXpProgress;
    private View viewSkillProgress;

    // Leaderboard views
    private CircleImageView ivPodium1, ivPodium2, ivPodium3;
    private TextView tvPodium1Name, tvPodium1Credits;
    private TextView tvPodium2Name, tvPodium2Credits;
    private TextView tvPodium3Name, tvPodium3Credits;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        loadDashboardData();
    }

    private void bindViews(View view) {
        // Wallet
        cardWallet = view.findViewById(R.id.cardWallet);
        ivWalletAvatar = view.findViewById(R.id.ivWalletAvatar);
        tvWalletName = view.findViewById(R.id.tvWalletName);
        tvWalletRank = view.findViewById(R.id.tvWalletRank);
        tvCreditCount = view.findViewById(R.id.tvCreditCount);
        tvTotalEarned = view.findViewById(R.id.tvTotalEarned);
        tvTotalSpent = view.findViewById(R.id.tvTotalSpent);
        tvSessionsCount = view.findViewById(R.id.tvSessionsCount);
        layoutKarmaBadge = view.findViewById(R.id.layoutKarmaBadge);
        tvKarmaMultiplier = view.findViewById(R.id.tvKarmaMultiplier);

        // Streak
        cardStreak = view.findViewById(R.id.cardStreak);
        tvStreakFire = view.findViewById(R.id.tvStreakFire);
        tvStreakCount = view.findViewById(R.id.tvStreakCount);
        tvStreakLabel = view.findViewById(R.id.tvStreakLabel);
        tvStreakMessage = view.findViewById(R.id.tvStreakMessage);
        layoutStreakKarma = view.findViewById(R.id.layoutStreakKarma);
        tvStreakKarma = view.findViewById(R.id.tvStreakKarma);

        // Skill Tree
        layoutSkillTree = view.findViewById(R.id.layoutSkillTree);
        nodeNovice = view.findViewById(R.id.nodeNovice);
        nodeScholar = view.findViewById(R.id.nodeScholar);
        nodeExpert = view.findViewById(R.id.nodeExpert);
        nodeGrandmaster = view.findViewById(R.id.nodeGrandmaster);
        connectorNS = view.findViewById(R.id.connectorNoviceScholar);
        connectorSE = view.findViewById(R.id.connectorScholarExpert);
        connectorEG = view.findViewById(R.id.connectorExpertGrandmaster);
        tvPrimarySkill = view.findViewById(R.id.tvPrimarySkill);
        tvCurrentLevel = view.findViewById(R.id.tvCurrentLevel);
        tvXpProgress = view.findViewById(R.id.tvXpProgress);
        viewSkillProgress = view.findViewById(R.id.viewSkillProgress);

        // Leaderboard
        ivPodium1 = view.findViewById(R.id.ivPodium1);
        ivPodium2 = view.findViewById(R.id.ivPodium2);
        ivPodium3 = view.findViewById(R.id.ivPodium3);
        tvPodium1Name = view.findViewById(R.id.tvPodium1Name);
        tvPodium1Credits = view.findViewById(R.id.tvPodium1Credits);
        tvPodium2Name = view.findViewById(R.id.tvPodium2Name);
        tvPodium2Credits = view.findViewById(R.id.tvPodium2Credits);
        tvPodium3Name = view.findViewById(R.id.tvPodium3Name);
        tvPodium3Credits = view.findViewById(R.id.tvPodium3Credits);
    }

    private void loadDashboardData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            loadMockDashboardData();
            return;
        }

        user.getIdToken(false).addOnSuccessListener(result -> {
            String token = "Bearer " + result.getToken();
            RetrofitClient.getInstance().getApiService()
                    .getUserWallet(token)
                    .enqueue(new retrofit2.Callback<com.example.learnloop.models.ApiResponse<UserWallet>>() {
                        @Override
                        public void onResponse(retrofit2.Call<com.example.learnloop.models.ApiResponse<UserWallet>> call,
                                               retrofit2.Response<com.example.learnloop.models.ApiResponse<UserWallet>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                populateWallet(response.body().getData());
                                populateStreak(response.body().getData());
                                populateSkillTree(response.body().getData());
                                populateLeaderboard(getMockLeaderboard()); // Leaderboard might need separate call
                                runEntranceAnimations();
                            } else {
                                loadMockDashboardData();
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<com.example.learnloop.models.ApiResponse<UserWallet>> call, Throwable t) {
                            loadMockDashboardData();
                        }
                    });
        }).addOnFailureListener(e -> loadMockDashboardData());
    }

    private void loadMockDashboardData() {
        UserWallet wallet = getMockWallet();
        List<LeaderboardEntry> leaderboard = getMockLeaderboard();

        populateWallet(wallet);
        populateStreak(wallet);
        populateSkillTree(wallet);
        populateLeaderboard(leaderboard);
        runEntranceAnimations();
    }

    // ========================== WALLET ==========================
    private void populateWallet(UserWallet wallet) {
        // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // if (user != null) {
        //     tvWalletName.setText(user.getDisplayName() != null ? user.getDisplayName() : "Learner");
        //     if (user.getPhotoUrl() != null) {
        //         Glide.with(this).load(user.getPhotoUrl()).into(ivWalletAvatar);
        //     }
        // } else {
            tvWalletName.setText(wallet.getDisplayName());
        // }

        tvWalletRank.setText("Campus Rank #" + wallet.getCampusRank());

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        tvCreditCount.setText(nf.format(wallet.getKnowledgeCredits()));
        tvTotalEarned.setText("↑ " + nf.format(wallet.getTotalEarned()));
        tvTotalSpent.setText("↓ " + nf.format(wallet.getTotalSpent()));
        tvSessionsCount.setText(String.valueOf(wallet.getSessionsCompleted()));

        // Karma badge
        if (wallet.hasActiveStreak()) {
            layoutKarmaBadge.setVisibility(View.VISIBLE);
            tvKarmaMultiplier.setText(String.format(Locale.US, "%.1fx Karma", wallet.getKarmaMultiplier()));
        } else {
            layoutKarmaBadge.setVisibility(View.GONE);
        }
    }

    // ========================== STREAK ==========================
    private void populateStreak(UserWallet wallet) {
        int streak = wallet.getCurrentStreak();
        tvStreakCount.setText(String.valueOf(streak));
        tvStreakLabel.setText(streak + "-day streak!");

        if (streak >= 3) {
            tvStreakMessage.setText(getString(R.string.streak_active));
            layoutStreakKarma.setVisibility(View.VISIBLE);
            tvStreakKarma.setText(String.format(Locale.US, "%.1fx", wallet.getKarmaMultiplier()));
            // Pulse the fire emoji
            tvStreakFire.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.pulse));
        } else {
            tvStreakMessage.setText(getString(R.string.streak_inactive));
            layoutStreakKarma.setVisibility(View.GONE);
        }
    }

    // ========================== SKILL TREE ==========================
    private void populateSkillTree(UserWallet wallet) {
        tvPrimarySkill.setText(wallet.getPrimarySkill().toUpperCase(Locale.US));
        tvCurrentLevel.setText(capitalizeFirst(wallet.getSkillLevel()));
        tvXpProgress.setText(String.format(Locale.US, "%d / %d XP", wallet.getSkillXp(), wallet.getSkillXpMax()));

        // Animate XP progress bar width
        float progress = wallet.getSkillProgress();
        viewSkillProgress.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewSkillProgress.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int parentWidth = ((View) viewSkillProgress.getParent()).getWidth();
                int targetWidth = (int) (parentWidth * progress);

                ViewGroup.LayoutParams params = viewSkillProgress.getLayoutParams();
                params.width = targetWidth;
                viewSkillProgress.setLayoutParams(params);
            }
        });

        // Color the nodes based on current level
        colorSkillNodes(wallet.getSkillLevel());
    }

    private void colorSkillNodes(String level) {
        int cNovice = ContextCompat.getColor(requireContext(), R.color.skill_novice);
        int cScholar = ContextCompat.getColor(requireContext(), R.color.skill_scholar);
        int cExpert = ContextCompat.getColor(requireContext(), R.color.skill_expert);
        int cGrandmaster = ContextCompat.getColor(requireContext(), R.color.skill_grandmaster);
        int cInactive = ContextCompat.getColor(requireContext(), R.color.divider);
        int cActiveConnector = ContextCompat.getColor(requireContext(), R.color.primary);

        // All start inactive
        setNodeColor(nodeNovice, cInactive);
        setNodeColor(nodeScholar, cInactive);
        setNodeColor(nodeExpert, cInactive);
        setNodeColor(nodeGrandmaster, cInactive);
        connectorNS.setBackgroundColor(cInactive);
        connectorSE.setBackgroundColor(cInactive);
        connectorEG.setBackgroundColor(cInactive);

        // Light up based on current level
        switch (level.toLowerCase(Locale.US)) {
            case "grandmaster":
                setNodeColor(nodeGrandmaster, cGrandmaster);
                connectorEG.setBackgroundColor(cActiveConnector);
                // fall through
            case "expert":
                setNodeColor(nodeExpert, cExpert);
                connectorSE.setBackgroundColor(cActiveConnector);
                // fall through
            case "scholar":
                setNodeColor(nodeScholar, cScholar);
                connectorNS.setBackgroundColor(cActiveConnector);
                // fall through
            case "novice":
                setNodeColor(nodeNovice, cNovice);
                break;
        }
    }

    private void setNodeColor(View node, int color) {
        GradientDrawable bg = (GradientDrawable) node.getBackground().mutate();
        bg.setColor(color);
    }

    // ========================== LEADERBOARD ==========================
    private void populateLeaderboard(List<LeaderboardEntry> entries) {
        if (entries.size() >= 1) {
            tvPodium1Name.setText(entries.get(0).getDisplayName());
            tvPodium1Credits.setText(NumberFormat.getNumberInstance(Locale.US).format(entries.get(0).getKnowledgeCredits()) + " KC");
        }
        if (entries.size() >= 2) {
            tvPodium2Name.setText(entries.get(1).getDisplayName());
            tvPodium2Credits.setText(NumberFormat.getNumberInstance(Locale.US).format(entries.get(1).getKnowledgeCredits()) + " KC");
        }
        if (entries.size() >= 3) {
            tvPodium3Name.setText(entries.get(2).getDisplayName());
            tvPodium3Credits.setText(NumberFormat.getNumberInstance(Locale.US).format(entries.get(2).getKnowledgeCredits()) + " KC");
        }
    }

    // ========================== ANIMATIONS ==========================
    private void runEntranceAnimations() {
        Animation scaleIn = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_in);
        Animation slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_fade);

        cardWallet.startAnimation(scaleIn);

        slideUp.setStartOffset(200);
        cardStreak.startAnimation(slideUp);

        Animation slideUp2 = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_fade);
        slideUp2.setStartOffset(400);
        layoutSkillTree.startAnimation(slideUp2);
    }

    // ========================== HELPERS ==========================
    private String capitalizeFirst(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase(Locale.US) + s.substring(1).toLowerCase(Locale.US);
    }

    // ========================== MOCK DATA ==========================
    private UserWallet getMockWallet() {
        return new UserWallet(
                "user_001", "Arjun B.", null,
                1250, 2100, 850, 24,
                7, 1.5, "scholar",
                350, 500, "Data Structures", 5
        );
    }

    private List<LeaderboardEntry> getMockLeaderboard() {
        List<LeaderboardEntry> list = new ArrayList<>();
        list.add(new LeaderboardEntry(1, "u1", "Arjun", null, 1250, 24, "scholar"));
        list.add(new LeaderboardEntry(2, "u2", "Priya", null, 980, 18, "expert"));
        list.add(new LeaderboardEntry(3, "u3", "Rahul", null, 820, 15, "novice"));
        return list;
    }
}
