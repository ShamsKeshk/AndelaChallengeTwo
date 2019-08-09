package com.example.andelachallengetwo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andelachallengetwo.ui.login.LoginActivity;
import com.example.andelachallengetwo.ui.login.SignupActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;

    @BindView(R.id.sign_in_button)
    SignInButton mGoogleSignInButton;

    @BindView(R.id.btn_sign_in_with_email)
    Button mSignInWithEmail;

    @BindView(R.id.btn_sign_up)
    Button mTextViewSignUp;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    private static final int RC_SIGN_IN = 10 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        mDatabaseReference = database.getReference();

//        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        mSignInWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mTextViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            User newUser = createUserProfile(user);
                            mDatabaseReference.child("users").child(user.getUid()).setValue(newUser);
                            Toast.makeText(getApplicationContext(),"Home Opening ... " + user.getDisplayName(),Toast.LENGTH_LONG).show();
                            redirectUser(newUser.isAdmin());
                        } else {

                        }

                    }
                });
    }

    private void redirectUser(boolean isAdmin){
        Intent intent ;
        if (isAdmin){
            intent = new Intent(MainActivity.this,AdminActivity.class);
        }else {
            intent = new Intent(MainActivity.this,HomeActivity.class);
        }
        startActivity(intent);
    }

    public User createUserProfile(FirebaseUser user) {

            String userName = user.getDisplayName();
            String userEmail = user.getEmail();
            String userId = user.getUid();
            String userImage = Uri.parse("" + user.getPhotoUrl()).buildUpon()
                    .appendQueryParameter("type", "large")
                    .build().toString();
            boolean isAdmin;
            if (userEmail.equalsIgnoreCase("shams.keshk.firebase@gmail.com")
            ||userEmail.equalsIgnoreCase("shams.keshk@gmail.com" )){
                isAdmin = true;
            }else {
                isAdmin = false;
            }


            return new User(userId,userName,userEmail,userImage,isAdmin);
        }


}
