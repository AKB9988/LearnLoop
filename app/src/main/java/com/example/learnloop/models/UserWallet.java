package com.example.learnloop.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model representing the user's Knowledge Credit wallet and gamification stats.
 */
public class UserWallet {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("knowledge_credits")
    private int knowledgeCredits;

    @SerializedName("total_earned")
    private int totalEarned;

    @SerializedName("total_spent")
    private int totalSpent;

    @SerializedName("sessions_completed")
    private int sessionsCompleted;

    @SerializedName("current_streak")
    private int currentStreak;

    @SerializedName("karma_multiplier")
    private double karmaMultiplier; // 1.0 normally, higher with streaks

    @SerializedName("skill_level")
    private String skillLevel; // "novice", "scholar", "expert", "grandmaster"

    @SerializedName("skill_xp")
    private int skillXp;

    @SerializedName("skill_xp_max")
    private int skillXpMax;

    @SerializedName("primary_skill")
    private String primarySkill;

    @SerializedName("campus_rank")
    private int campusRank;

    // --- Constructors ---
    public UserWallet() {}

    public UserWallet(String userId, String displayName, String avatarUrl,
                      int knowledgeCredits, int totalEarned, int totalSpent,
                      int sessionsCompleted, int currentStreak, double karmaMultiplier,
                      String skillLevel, int skillXp, int skillXpMax,
                      String primarySkill, int campusRank) {
        this.userId = userId;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.knowledgeCredits = knowledgeCredits;
        this.totalEarned = totalEarned;
        this.totalSpent = totalSpent;
        this.sessionsCompleted = sessionsCompleted;
        this.currentStreak = currentStreak;
        this.karmaMultiplier = karmaMultiplier;
        this.skillLevel = skillLevel;
        this.skillXp = skillXp;
        this.skillXpMax = skillXpMax;
        this.primarySkill = primarySkill;
        this.campusRank = campusRank;
    }

    // --- Getters ---
    public String getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public String getAvatarUrl() { return avatarUrl; }
    public int getKnowledgeCredits() { return knowledgeCredits; }
    public int getTotalEarned() { return totalEarned; }
    public int getTotalSpent() { return totalSpent; }
    public int getSessionsCompleted() { return sessionsCompleted; }
    public int getCurrentStreak() { return currentStreak; }
    public double getKarmaMultiplier() { return karmaMultiplier; }
    public String getSkillLevel() { return skillLevel; }
    public int getSkillXp() { return skillXp; }
    public int getSkillXpMax() { return skillXpMax; }
    public String getPrimarySkill() { return primarySkill; }
    public int getCampusRank() { return campusRank; }

    // --- Setters ---
    public void setUserId(String userId) { this.userId = userId; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setKnowledgeCredits(int knowledgeCredits) { this.knowledgeCredits = knowledgeCredits; }
    public void setTotalEarned(int totalEarned) { this.totalEarned = totalEarned; }
    public void setTotalSpent(int totalSpent) { this.totalSpent = totalSpent; }
    public void setSessionsCompleted(int sessionsCompleted) { this.sessionsCompleted = sessionsCompleted; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public void setKarmaMultiplier(double karmaMultiplier) { this.karmaMultiplier = karmaMultiplier; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }
    public void setSkillXp(int skillXp) { this.skillXp = skillXp; }
    public void setSkillXpMax(int skillXpMax) { this.skillXpMax = skillXpMax; }
    public void setPrimarySkill(String primarySkill) { this.primarySkill = primarySkill; }
    public void setCampusRank(int campusRank) { this.campusRank = campusRank; }

    // --- Helpers ---
    public boolean hasActiveStreak() {
        return currentStreak >= 3;
    }

    public float getSkillProgress() {
        if (skillXpMax == 0) return 0f;
        return (float) skillXp / skillXpMax;
    }
}
