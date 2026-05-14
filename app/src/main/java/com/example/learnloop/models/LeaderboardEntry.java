package com.example.learnloop.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model representing a single entry on the Campus Leaderboard.
 */
public class LeaderboardEntry {

    @SerializedName("rank")
    private int rank;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("knowledge_credits")
    private int knowledgeCredits;

    @SerializedName("sessions_completed")
    private int sessionsCompleted;

    @SerializedName("skill_level")
    private String skillLevel;

    // --- Constructors ---
    public LeaderboardEntry() {}

    public LeaderboardEntry(int rank, String userId, String displayName,
                            String avatarUrl, int knowledgeCredits,
                            int sessionsCompleted, String skillLevel) {
        this.rank = rank;
        this.userId = userId;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.knowledgeCredits = knowledgeCredits;
        this.sessionsCompleted = sessionsCompleted;
        this.skillLevel = skillLevel;
    }

    // --- Getters ---
    public int getRank() { return rank; }
    public String getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public String getAvatarUrl() { return avatarUrl; }
    public int getKnowledgeCredits() { return knowledgeCredits; }
    public int getSessionsCompleted() { return sessionsCompleted; }
    public String getSkillLevel() { return skillLevel; }

    // --- Setters ---
    public void setRank(int rank) { this.rank = rank; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setKnowledgeCredits(int knowledgeCredits) { this.knowledgeCredits = knowledgeCredits; }
    public void setSessionsCompleted(int sessionsCompleted) { this.sessionsCompleted = sessionsCompleted; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }
}
