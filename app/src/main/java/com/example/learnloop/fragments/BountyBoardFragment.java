package com.example.learnloop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnloop.R;
import com.example.learnloop.adapters.BountyAdapter;
import com.example.learnloop.models.ApiResponse;
import com.example.learnloop.models.HelpRequest;
import com.example.learnloop.models.MatchAcceptRequest;
import com.example.learnloop.network.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Bounty Board Fragment — displays active help requests in a RecyclerView.
 * High-urgency requests get a glowing border and bounty multiplier badge.
 */
public class BountyBoardFragment extends Fragment implements BountyAdapter.OnBountyClickListener {

    private RecyclerView rvBountyBoard;
    private TextView tvEmptyState;
    private ProgressBar progressBounty;
    private BountyAdapter adapter;
    private List<HelpRequest> requestList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bounty_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvBountyBoard = view.findViewById(R.id.rvBountyBoard);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        progressBounty = view.findViewById(R.id.progressBounty);

        bindChips(view);

        // Setup RecyclerView
        adapter = new BountyAdapter(requestList, this);
        rvBountyBoard.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBountyBoard.setAdapter(adapter);

        // Load data
        loadActiveRequests();
    }

    private void loadActiveRequests() {
        showLoading(true);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            loadMockData(); // Fallback if not logged in
            return;
        }

        user.getIdToken(false).addOnSuccessListener(result -> {
            String token = "Bearer " + result.getToken();
            RetrofitClient.getInstance().getApiService()
                    .getActiveRequests(token)
                    .enqueue(new Callback<ApiResponse<List<HelpRequest>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<HelpRequest>>> call,
                                               Response<ApiResponse<List<HelpRequest>>> response) {
                            showLoading(false);
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                allRequests.clear();
                                allRequests.addAll(response.body().getData());
                                requestList.clear();
                                requestList.addAll(allRequests);
                                adapter.notifyDataSetChanged();
                                updateEmptyState();
                            } else {
                                // Fallback to mock data if server returns error
                                loadMockData();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<List<HelpRequest>>> call, Throwable t) {
                            // Backend not ready yet, fallback to mock data
                            loadMockData();
                        }
                    });
        }).addOnFailureListener(e -> {
            loadMockData();
        });
    }

    private TextView chipAll, chipForYou, chipHighPriority, chipRecent;
    private List<HelpRequest> allRequests = new ArrayList<>();

    private void bindChips(View view) {
        chipAll = view.findViewById(R.id.chipAll);
        chipForYou = view.findViewById(R.id.chipForYou);
        chipHighPriority = view.findViewById(R.id.chipHighPriority);
        chipRecent = view.findViewById(R.id.chipRecent);

        View.OnClickListener chipListener = v -> {
            chipAll.setBackgroundResource(R.drawable.bg_chip_unselected);
            chipForYou.setBackgroundResource(R.drawable.bg_chip_unselected);
            chipHighPriority.setBackgroundResource(R.drawable.bg_chip_unselected);
            chipRecent.setBackgroundResource(R.drawable.bg_chip_unselected);
            
            // Set clicked chip to selected
            v.setBackgroundResource(R.drawable.bg_chip_selected);
            
            // Apply simple filter logic
            requestList.clear();
            if (v.getId() == R.id.chipHighPriority) {
                for (HelpRequest req : allRequests) {
                    if (req.isHighUrgency()) requestList.add(req);
                }
            } else if (v.getId() == R.id.chipForYou) {
                for (HelpRequest req : allRequests) {
                    if (req.getAiMatchScore() > 85) requestList.add(req);
                }
            } else {
                requestList.addAll(allRequests);
            }
            adapter.notifyDataSetChanged();
            updateEmptyState();
        };

        chipAll.setOnClickListener(chipListener);
        chipForYou.setOnClickListener(chipListener);
        chipHighPriority.setOnClickListener(chipListener);
        chipRecent.setOnClickListener(chipListener);
    }

    /**
     * Mock data for testing the UI without a backend.
     */
    private void loadMockData() {
        allRequests.clear();

        allRequests.add(new HelpRequest(
                "1", "Need help with Monotonic Stacks in C++",
                "I'm struggling with the next greater element problem and can't figure out the stack approach.",
                "Data Structures", "Priya S.", null, 50, "high", 1.5,
                "active", "2026-05-14", new String[]{"C++", "Stacks", "DSA"},
                "video", "English", 30, 92, 0
        ));

        allRequests.add(new HelpRequest(
                "2", "Explain Backpropagation intuitively",
                "I understand forward pass but the chain rule in backprop is confusing me.",
                "Machine Learning", "Rahul K.", null, 75, "urgent", 2.0,
                "active", "2026-05-14", new String[]{"ML", "Neural Networks"},
                "whiteboard", "English", 60, 85, 1
        ));

        allRequests.add(new HelpRequest(
                "3", "Help setting up Docker for Node.js",
                "Need someone to walk me through Dockerfile and docker-compose for my Express app.",
                "DevOps", "Sneha M.", null, 30, "normal", 1.0,
                "active", "2026-05-13", new String[]{"Docker", "Node.js"},
                "chat", "English", 15, 78, 0
        ));

        allRequests.add(new HelpRequest(
                "4", "SQL JOINs and Subqueries practice",
                "I have my DBMS exam tomorrow. Need help understanding complex joins.",
                "Databases", "Amit R.", null, 40, "high", 1.5,
                "active", "2026-05-14", new String[]{"SQL", "DBMS"},
                "whiteboard", "Hindi", 30, 95, 2
        ));

        allRequests.add(new HelpRequest(
                "5", "Flutter State Management - Provider vs Riverpod",
                "Which one should I use for my final year project and why?",
                "Mobile Dev", "Kavya P.", null, 25, "normal", 1.0,
                "active", "2026-05-12", new String[]{"Flutter", "Dart"},
                "chat", "English", 15, 88, 0
        ));

        requestList.clear();
        requestList.addAll(allRequests);
        adapter.notifyDataSetChanged();
        showLoading(false);
        updateEmptyState();
    }

    private void updateEmptyState() {
        View layoutEmptyState = getView().findViewById(R.id.layoutEmptyState);
        if (requestList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvBountyBoard.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvBountyBoard.setVisibility(View.VISIBLE);
        }
    }

    private void showLoading(boolean show) {
        progressBounty.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onAcceptClicked(HelpRequest request) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "Must be logged in to help!", Toast.LENGTH_SHORT).show();
            return;
        }

        user.getIdToken(false).addOnSuccessListener(result -> {
            String token = "Bearer " + result.getToken();
            MatchAcceptRequest body = new MatchAcceptRequest(request.getId(), user.getUid());
            
            RetrofitClient.getInstance().getApiService()
                    .acceptMatch(token, body)
                    .enqueue(new Callback<ApiResponse<HelpRequest>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<HelpRequest>> call, Response<ApiResponse<HelpRequest>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(requireContext(), "Match accepted! Opening session...", Toast.LENGTH_SHORT).show();
                                // Assuming the backend returns a session URL in the updated request object, 
                                // or we can generate a mock one for now if testing:
                                String sessionUrl = "https://meet.jit.si/LearnLoop_" + request.getId();
                                android.content.Intent browserIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(sessionUrl));
                                startActivity(browserIntent);
                            } else {
                                fallbackAccept(request);
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<HelpRequest>> call, Throwable t) {
                            fallbackAccept(request);
                        }
                    });
        }).addOnFailureListener(e -> fallbackAccept(request));
    }

    private void fallbackAccept(HelpRequest request) {
        Toast.makeText(requireContext(), "Network error. Simulated Match!", Toast.LENGTH_SHORT).show();
        String sessionUrl = "https://meet.jit.si/LearnLoop_" + request.getId();
        android.content.Intent browserIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(sessionUrl));
        startActivity(browserIntent);
    }
}
