package com.example.learnloop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Login screen with Google Sign-In via Firebase Auth.
 *
 * NOTE: You must add your google-services.json to the app/ directory
 * and set your Web Client ID in the GoogleSignInOptions below.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // TODO: Replace with your actual Web Client ID from Firebase Console
    private static final String WEB_CLIENT_ID = "1005339511995-sm7h4mjc4a3rdb2aetsfcfj4cg1aq89q.apps.googleusercontent.com";

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private MaterialButton btnGoogleSignIn;
    private ProgressBar progressLogin;

    private final ActivityResultLauncher<Intent> signInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                } else {
                    showLoading(false);
                    Toast.makeText(this, "Sign-in cancelled", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        com.google.firebase.FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        // Check if already signed in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToMain();
            return;
        }

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(WEB_CLIENT_ID)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Bind views
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        progressLogin = findViewById(R.id.progressLogin);
        TextView tvLogoIcon = findViewById(R.id.tvLogoIcon);

        // Animate the logo
        tvLogoIcon.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));

        // Sign in click
        btnGoogleSignIn.setOnClickListener(v -> {
            showLoading(true);
            Intent signInIntent = googleSignInClient.getSignInIntent();
            signInLauncher.launch(signInIntent);
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account.getIdToken());
            }
        } catch (ApiException e) {
            Log.e(TAG, "Google sign-in failed: " + e.getStatusCode(), e);
            showLoading(false);
            Toast.makeText(this, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        navigateToMain();
                    } else {
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        progressLogin.setVisibility(show ? View.VISIBLE : View.GONE);
        btnGoogleSignIn.setEnabled(!show);
    }
}
