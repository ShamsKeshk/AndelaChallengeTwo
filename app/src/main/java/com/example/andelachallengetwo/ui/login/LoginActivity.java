package com.example.andelachallengetwo.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andelachallengetwo.HomeActivity;
import com.example.andelachallengetwo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_user_email_login_id)
    EditText mEditTextUserEmail;

    @BindView(R.id.et_user_password_login_id)
    EditText mEditTextUserPassword;

    @BindView(R.id.btn_login)
    Button mButtonLogin;

    @BindView(R.id.loading)
    ProgressBar loadingProgressBar;

    private String email;
    private String password;

    @Override
    protected void onStart() {
        super.onStart();
       if( FirebaseAuth.getInstance().getCurrentUser() != null){
           Intent intent = new Intent(LoginActivity.this , HomeActivity.class);
           startActivity(intent);
       }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        loadingProgressBar.setVisibility(View.GONE);
        mEditTextUserPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    login(mEditTextUserEmail.getText().toString(),mEditTextUserPassword.getText().toString());
                }
                return false;
            }
        });

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                email = mEditTextUserEmail.getText().toString();
                password = mEditTextUserPassword.getText().toString();
                login(email,password);

            }
        });
    }

    private void login(String email,String password){
        if (email == null || TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(),"enter your email",Toast.LENGTH_LONG).show();
            return;
        }
        if (password == null || TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"enter your password",Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loadingProgressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    mEditTextUserEmail.setError("Invalid User Name or password");
                    mEditTextUserEmail.requestFocus();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingProgressBar.setVisibility(View.GONE);
                mEditTextUserEmail.setError("Invalid User Name or password");
                mEditTextUserEmail.requestFocus();
            }
        });
    }


}
