package com.example.learnloop.network;

import com.example.learnloop.models.ApiResponse;
import com.example.learnloop.models.CreateRequestBody;
import com.example.learnloop.models.HelpRequest;
import com.example.learnloop.models.LeaderboardEntry;
import com.example.learnloop.models.MatchAcceptRequest;
import com.example.learnloop.models.UserWallet;

import com.example.learnloop.models.UserProfileModel;

import java.util.List;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Retrofit interface defining all API endpoints for the LearnLoop FastAPI backend.
 */
public interface ApiService {

    /**
     * Create a new help request on the Bounty Board.
     * POST /request/create
     */
    @POST("request/create")
    Call<ApiResponse<HelpRequest>> createRequest(
            @Header("Authorization") String authToken,
            @Body CreateRequestBody body
    );

    /**
     * Fetch all active help requests for the Bounty Board feed.
     * GET /requests/active
     */
    @GET("requests/active")
    Call<ApiResponse<List<HelpRequest>>> getActiveRequests(
            @Header("Authorization") String authToken
    );

    /**
     * Accept a help request as a mentor.
     * POST /match/accept
     */
    @POST("match/accept")
    Call<ApiResponse<HelpRequest>> acceptMatch(
            @Header("Authorization") String authToken,
            @Body MatchAcceptRequest body
    );

    /**
     * Fetch the current user's wallet, streak, and gamification data.
     * GET /user/wallet
     */
    @GET("user/wallet")
    Call<ApiResponse<UserWallet>> getUserWallet(
            @Header("Authorization") String authToken
    );

    /**
     * Fetch the campus leaderboard (top mentors).
     * GET /leaderboard/top
     */
    @GET("user/leaderboard/top")
    Call<ApiResponse<List<LeaderboardEntry>>> getLeaderboard(
            @Header("Authorization") String authToken
    );

    /**
     * Register a new user profile on the backend.
     * POST /user/register
     */
    @retrofit2.http.POST("user/register")
    Call<Void> registerUser(
            @retrofit2.http.Header("Authorization") String authToken,
            @retrofit2.http.Body UserProfileModel body
    );
}
