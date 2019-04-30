package com.cs.cardwhere;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class ProfileFragment extends Fragment {

    private Activity mActivity;

    private View view;

    private SignInButton signInButton;
    private Button signOutButton;
    private Button revokeButton;

    private CircleImageView userIcon;
    private TextView userName;
    private TextView userEmail;

    // Firebase Auth
    private FirebaseAuth mAuth;

    // Google Sign In
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 101;

    // Local Storage
    private SharedPreferences sharedPreferences;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, gso);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_profile, container, false);

        signInButton = view.findViewById(R.id.btn_google_sign_in);
        signOutButton = view.findViewById(R.id.btn_google_sign_out);
        revokeButton = view.findViewById(R.id.btn_google_revoke);

        userIcon = view.findViewById(R.id.user_icon);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);

        // Sign In
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // Sign Out
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        // Account Revoke
        revokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revokeAccess();
            }
        });

        // return fragment to MainActivity
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                // Firebase Auth
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Update Views
                            updateViewWhenLogin(user.getDisplayName(), user.getEmail(), user.getPhotoUrl());

                            // Save user info to share preferences
                            updateUserInfoToLoacalStoreage(user.getUid(), user.getDisplayName());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {

            updateViewWhenLogin(currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getPhotoUrl());

        }else{
            updateViewWhenLogout();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener((mActivity),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Update Views
                        updateViewWhenLogout();

                        // Remove User Info To Local Storage
                        updateUserInfoToLoacalStoreage("", "");
                    }
                });

    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener((mActivity), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Update Views
                        updateViewWhenLogout();

                        // Remove User Info To Local Storage
                        updateUserInfoToLoacalStoreage("", "");
                    }
                });
    }

    private void updateViewWhenLogin(String name, String email, Uri icon){

        userName.setText(name);
        userEmail.setText(email);

        Picasso.get()
                .load(icon)
                .resize(200, 200)
                .centerCrop()
                .into(userIcon);

        signInButton.setVisibility(View.GONE);
        signOutButton.setVisibility(View.VISIBLE);
        revokeButton.setVisibility(View.VISIBLE);

        userName.setVisibility(View.VISIBLE);
        userEmail.setVisibility(View.VISIBLE);
    }

    private void updateViewWhenLogout(){
        signInButton.setVisibility(View.VISIBLE);
        signOutButton.setVisibility(View.GONE);
        revokeButton.setVisibility(View.GONE);

        userName.setVisibility(View.GONE);
        userEmail.setVisibility(View.GONE);
        int id = getResources().getIdentifier("com.cs.cardwhere:drawable/user", null, null);
        userIcon.setImageResource(id);
    }

    private void updateUserInfoToLoacalStoreage(String userId, String userName){
        sharedPreferences = mActivity.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_ID", userId);
        editor.putString("USER_NAME", userName);
        editor.commit();

        //sharedPreferences.getString(NAME_KEY, "")
    }

}
