package com.example.learnloop.models;

import com.google.gson.annotations.SerializedName;

/**
 * Request body for accepting a help request match.
 */
public class MatchAcceptRequest {

    @SerializedName("request_id")
    private String requestId;

    @SerializedName("mentor_id")
    private String mentorId;

    public MatchAcceptRequest() {}

    public MatchAcceptRequest(String requestId, String mentorId) {
        this.requestId = requestId;
        this.mentorId = mentorId;
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getMentorId() { return mentorId; }
    public void setMentorId(String mentorId) { this.mentorId = mentorId; }
}
