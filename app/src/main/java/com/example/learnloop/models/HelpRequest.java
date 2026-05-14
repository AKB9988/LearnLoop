package com.example.learnloop.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model representing a student help request on the LearnLoop platform.
 * Students post requests for specific topics they need help with,
 * and peer tutors can accept them to earn Knowledge Credits.
 */
public class HelpRequest {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("subject")
    private String subject;

    @SerializedName("topic")
    private String topic;

    @SerializedName("poster_name")
    private String posterName;

    @SerializedName("poster_avatar_url")
    private String posterAvatarUrl;

    @SerializedName("credits_offered")
    private int creditsOffered;

    @SerializedName("urgency")
    private String urgency; // "normal", "high", "urgent"

    @SerializedName("bounty_multiplier")
    private double bountyMultiplier; // 1.0, 1.5, 2.0

    @SerializedName("status")
    private String status; // "active", "accepted", "in_session", "completed"

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("tags")
    private String[] tags;

    @SerializedName("session_type")
    private String sessionType; // "chat", "video", "whiteboard"

    @SerializedName("preferred_language")
    private String preferredLanguage;

    @SerializedName("estimated_duration")
    private int estimatedDuration; // minutes

    @SerializedName("ai_match_score")
    private int aiMatchScore; // 0-100 AI compatibility score

    @SerializedName("responses_count")
    private int responsesCount; // number of tutors who responded

    // --- Constructors ---
    public HelpRequest() {}

    public HelpRequest(String id, String title, String description, String subject,
                       String posterName, String posterAvatarUrl, int creditsOffered,
                       String urgency, double bountyMultiplier, String status,
                       String createdAt, String[] tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.posterName = posterName;
        this.posterAvatarUrl = posterAvatarUrl;
        this.creditsOffered = creditsOffered;
        this.urgency = urgency;
        this.bountyMultiplier = bountyMultiplier;
        this.status = status;
        this.createdAt = createdAt;
        this.tags = tags;
    }

    // Extended constructor with new fields
    public HelpRequest(String id, String title, String description, String subject,
                       String posterName, String posterAvatarUrl, int creditsOffered,
                       String urgency, double bountyMultiplier, String status,
                       String createdAt, String[] tags, String sessionType,
                       String preferredLanguage, int estimatedDuration,
                       int aiMatchScore, int responsesCount) {
        this(id, title, description, subject, posterName, posterAvatarUrl,
             creditsOffered, urgency, bountyMultiplier, status, createdAt, tags);
        this.sessionType = sessionType;
        this.preferredLanguage = preferredLanguage;
        this.estimatedDuration = estimatedDuration;
        this.aiMatchScore = aiMatchScore;
        this.responsesCount = responsesCount;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSubject() { return subject; }
    public String getTopic() { return topic; }
    public String getPosterName() { return posterName; }
    public String getPosterAvatarUrl() { return posterAvatarUrl; }
    public int getCreditsOffered() { return creditsOffered; }
    public String getUrgency() { return urgency; }
    public double getBountyMultiplier() { return bountyMultiplier; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String[] getTags() { return tags; }
    public String getSessionType() { return sessionType; }
    public String getPreferredLanguage() { return preferredLanguage; }
    public int getEstimatedDuration() { return estimatedDuration; }
    public int getAiMatchScore() { return aiMatchScore; }
    public int getResponsesCount() { return responsesCount; }

    // --- Setters ---
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setTopic(String topic) { this.topic = topic; }
    public void setPosterName(String posterName) { this.posterName = posterName; }
    public void setPosterAvatarUrl(String posterAvatarUrl) { this.posterAvatarUrl = posterAvatarUrl; }
    public void setCreditsOffered(int creditsOffered) { this.creditsOffered = creditsOffered; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    public void setBountyMultiplier(double bountyMultiplier) { this.bountyMultiplier = bountyMultiplier; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setTags(String[] tags) { this.tags = tags; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }
    public void setEstimatedDuration(int estimatedDuration) { this.estimatedDuration = estimatedDuration; }
    public void setAiMatchScore(int aiMatchScore) { this.aiMatchScore = aiMatchScore; }
    public void setResponsesCount(int responsesCount) { this.responsesCount = responsesCount; }

    // --- Helpers ---
    public boolean isHighUrgency() {
        return "high".equalsIgnoreCase(urgency) || "urgent".equalsIgnoreCase(urgency)
                || "critical".equalsIgnoreCase(urgency);
    }

    public int getEffectiveCredits() {
        return (int) Math.round(creditsOffered * bountyMultiplier);
    }

    public String getSessionTypeEmoji() {
        if (sessionType == null) return "💬";
        switch (sessionType.toLowerCase()) {
            case "video": return "🎥";
            case "whiteboard": return "📝";
            default: return "💬";
        }
    }

    public String getSessionTypeLabel() {
        if (sessionType == null) return "Chat";
        switch (sessionType.toLowerCase()) {
            case "video": return "Video";
            case "whiteboard": return "Whiteboard";
            default: return "Chat";
        }
    }

    public String getTimeAgo() {
        // Simplified for demo — returns relative time
        if (createdAt == null) return "";
        return "2h ago"; // Mock for now
    }
}
