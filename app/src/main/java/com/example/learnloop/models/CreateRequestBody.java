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

    @SerializedName("topic")
    private String topic;

    @SerializedName("session_type")
    private String sessionType;

    @SerializedName("urgency")
    private String urgency;

    @SerializedName("duration")
    private int duration;

    @SerializedName("credits_offered")
    private int creditsOffered;

    public CreateRequestBody() {}

    public CreateRequestBody(String title, String description, String subject, String topic,
                             String sessionType, String urgency, int duration, int creditsOffered) {
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.topic = topic;
        this.sessionType = sessionType;
        this.urgency = urgency;
        this.duration = duration;
        this.creditsOffered = creditsOffered;
    }

    // --- Getters ---
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSubject() { return subject; }
    public String getTopic() { return topic; }
    public String getSessionType() { return sessionType; }
    public String getUrgency() { return urgency; }
    public int getDuration() { return duration; }
    public int getCreditsOffered() { return creditsOffered; }

    // --- Setters ---
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setTopic(String topic) { this.topic = topic; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setCreditsOffered(int creditsOffered) { this.creditsOffered = creditsOffered; }
}
