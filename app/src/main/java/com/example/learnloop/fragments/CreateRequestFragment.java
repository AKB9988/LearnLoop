package com.example.learnloop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.learnloop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateRequestFragment extends Fragment {

    private EditText etRequestTitle, etRequestDesc, etSubject, etTopic, etCredits;
    private TextView chipChat, chipVideo, chipWhiteboard;
    private TextView chipNormal, chipHigh, chipUrgent;
    private TextView chip15min, chip30min, chip60min;
    private TextView btnPostRequest;

    private String selectedSessionType = "chat";
    private String selectedUrgency = "medium";
    private int selectedDuration = 30;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        bindViews(view);
        setupListeners();
    }

    private void bindViews(View view) {
        etRequestTitle = view.findViewById(R.id.etRequestTitle);
        etRequestDesc = view.findViewById(R.id.etRequestDesc);
        etSubject = view.findViewById(R.id.etSubject);
        etTopic = view.findViewById(R.id.etTopic);
        etCredits = view.findViewById(R.id.etCredits);

        chipChat = view.findViewById(R.id.chipChat);
        chipVideo = view.findViewById(R.id.chipVideo);
        chipWhiteboard = view.findViewById(R.id.chipWhiteboard);

        chipNormal = view.findViewById(R.id.chipNormal);
        chipHigh = view.findViewById(R.id.chipHigh);
        chipUrgent = view.findViewById(R.id.chipUrgent);

        chip15min = view.findViewById(R.id.chip15min);
        chip30min = view.findViewById(R.id.chip30min);
        chip60min = view.findViewById(R.id.chip60min);

        btnPostRequest = view.findViewById(R.id.btnPostRequest);
    }

    private void setupListeners() {
        // Session Type Selection
        chipChat.setOnClickListener(v -> setSessionType("chat", chipChat, chipVideo, chipWhiteboard));
        chipVideo.setOnClickListener(v -> setSessionType("video", chipVideo, chipChat, chipWhiteboard));
        chipWhiteboard.setOnClickListener(v -> setSessionType("whiteboard", chipWhiteboard, chipChat, chipVideo));

        // Urgency Selection
        chipNormal.setOnClickListener(v -> setUrgency("medium", chipNormal, chipHigh, chipUrgent));
        chipHigh.setOnClickListener(v -> setUrgency("high", chipHigh, chipNormal, chipUrgent));
        chipUrgent.setOnClickListener(v -> setUrgency("urgent", chipUrgent, chipNormal, chipHigh));

        // Duration Selection
        chip15min.setOnClickListener(v -> setDuration(15, chip15min, chip30min, chip60min));
        chip30min.setOnClickListener(v -> setDuration(30, chip30min, chip15min, chip60min));
        chip60min.setOnClickListener(v -> setDuration(60, chip60min, chip15min, chip30min));

        // Post Button
        btnPostRequest.setOnClickListener(v -> postRequest());
    }

    private void setSessionType(String type, TextView selected, TextView... unselected) {
        selectedSessionType = type;
        selectChip(selected);
        for (TextView tv : unselected) deselectChip(tv);
    }

    private void setUrgency(String urgency, TextView selected, TextView... unselected) {
        selectedUrgency = urgency;
        selectChip(selected);
        for (TextView tv : unselected) deselectChip(tv);
    }

    private void setDuration(int duration, TextView selected, TextView... unselected) {
        selectedDuration = duration;
        selectChip(selected);
        for (TextView tv : unselected) deselectChip(tv);
    }

    private void selectChip(TextView chip) {
        chip.setBackgroundResource(R.drawable.bg_chip_selected);
        // We could also dynamically change text color here if needed, 
        // but currently relying on background. Let's explicitly set text color:
        chip.setTextColor(getResources().getColor(R.color.primary_light, null));
    }

    private void deselectChip(TextView chip) {
        chip.setBackgroundResource(R.drawable.bg_chip_unselected);
        chip.setTextColor(getResources().getColor(R.color.text_secondary, null));
    }

    private void postRequest() {
        String title = etRequestTitle.getText().toString().trim();
        String desc = etRequestDesc.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String topic = etTopic.getText().toString().trim();
        String creditsStr = etCredits.getText().toString().trim();
        
        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        int credits = creditsStr.isEmpty() ? 30 : Integer.parseInt(creditsStr);

        com.example.learnloop.models.CreateRequestBody body = new com.example.learnloop.models.CreateRequestBody(
            title, desc, subject, topic, selectedSessionType, selectedUrgency, selectedDuration, credits
        );

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            simulateSuccess(null, selectedSessionType);
            return;
        }

        btnPostRequest.setEnabled(false);
        user.getIdToken(false).addOnSuccessListener(result -> {
            String token = "Bearer " + result.getToken();
            com.example.learnloop.network.RetrofitClient.getInstance().getApiService()
                    .createRequest(token, body)
                    .enqueue(new retrofit2.Callback<com.example.learnloop.models.ApiResponse<com.example.learnloop.models.HelpRequest>>() {
                        @Override
                        public void onResponse(retrofit2.Call<com.example.learnloop.models.ApiResponse<com.example.learnloop.models.HelpRequest>> call,
                                               retrofit2.Response<com.example.learnloop.models.ApiResponse<com.example.learnloop.models.HelpRequest>> response) {
                            btnPostRequest.setEnabled(true);
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                String realId = response.body().getData().getId();
                                simulateSuccess(realId, selectedSessionType);
                            } else {
                                String errorMsg = "Server error: " + response.code();
                                try {
                                    if (response.errorBody() != null) {
                                        errorMsg += " - " + response.errorBody().string();
                                    }
                                } catch (Exception e) {
                                    // ignore
                                }
                                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                                simulateSuccess(null, selectedSessionType);
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<com.example.learnloop.models.ApiResponse<com.example.learnloop.models.HelpRequest>> call, Throwable t) {
                            btnPostRequest.setEnabled(true);
                            Toast.makeText(getContext(), "Network error. Simulated success.", Toast.LENGTH_SHORT).show();
                            simulateSuccess(null, selectedSessionType); // Fallback for UI testing
                        }
                    });
        }).addOnFailureListener(e -> {
            btnPostRequest.setEnabled(true);
            simulateSuccess(null, selectedSessionType);
        });
    }

    private void simulateSuccess(String requestId, String sessionType) {
        Toast.makeText(getContext(), R.string.request_posted, Toast.LENGTH_SHORT).show();

        // Use real ID or fallback to mock
        final String matchId = (requestId != null && !requestId.isEmpty()) ? requestId : java.util.UUID.randomUUID().toString().substring(0, 6);
        final String type = sessionType != null ? sessionType : "video";

        // Sanitize ID for URLs. 
        // For the hackathon demo, we hardcode the room ID so both phones ALWAYS connect 
        // even if the backend drops or they click a mock request.
        final String safeId = "DemoRoom2026";

        // Schedule a mock push notification (Alert Dialog) 12 seconds later for the demo
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (getActivity() != null && !getActivity().isFinishing()) {
                new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                        .setTitle("🎉 Match Found!")
                        .setMessage("A mentor has accepted your request! They are joining the room now.")
                        .setPositiveButton("Join Session", (dialog, which) -> {
                            String sessionUrl;
                            if (type.contains("chat")) {
                                sessionUrl = "https://tlk.io/learnloop" + safeId;
                            } else if (type.contains("board") || type.contains("white")) {
                                sessionUrl = "https://wbo.ophir.dev/boards/learnloop" + safeId;
                            } else {
                                sessionUrl = "https://meet.jit.si/LearnLoop" + safeId;
                            }
                            android.content.Intent browserIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(sessionUrl));
                            startActivity(browserIntent);
                        })
                        .setNegativeButton("Close", null)
                        .setCancelable(false)
                        .show();
            }
        }, 12000); // 12 seconds delay gives them time to navigate away before pop-up

        // Reset fields
        etRequestTitle.setText("");
        etRequestDesc.setText("");
        etSubject.setText("");
        etTopic.setText("");
        etCredits.setText("30");
        setSessionType("chat", chipChat, chipVideo, chipWhiteboard);
        setUrgency("medium", chipNormal, chipHigh, chipUrgent);
        setDuration(30, chip30min, chip15min, chip60min);
    }
}
