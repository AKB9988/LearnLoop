package com.example.learnloop.models;

import com.google.gson.annotations.SerializedName;

/**
 * Request body for creating a new help request.
 */
public class CreateRequestBody {

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("subject")
    private String subject;

    @SerializedName("urgency")
    private String urgency;

    @SerializedName("credits_offered")
    private int creditsOffered;

    @SerializedName("tags")
    private String[] tags;

    @SerializedName("session_type")
    private String sessionType;

    @SerializedName("duration")
    private int duration;

    public CreateRequestBody() {}

    public CreateRequestBody(String title, String description, String subject, String topic,
                             String sessionType, String urgency, int duration, int creditsOffered) {
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.urgency = urgency;
        this.creditsOffered = creditsOffered;
        this.sessionType = sessionType;
        this.duration = duration;
        this.tags = new String[]{topic}; // Mapping topic to tags for backward compatibility
    }

    // --- Getters ---
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSubject() { return subject; }
    public String getUrgency() { return urgency; }
    public int getCreditsOffered() { return creditsOffered; }
    public String[] getTags() { return tags; }
    public String getSessionType() { return sessionType; }
    public int getDuration() { return duration; }

    // --- Setters ---
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    public void setCreditsOffered(int creditsOffered) { this.creditsOffered = creditsOffered; }
    public void setTags(String[] tags) { this.tags = tags; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    public void setDuration(int duration) { this.duration = duration; }
}
