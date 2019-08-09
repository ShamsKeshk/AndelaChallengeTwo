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

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.et_user_email_sign_up_id)
    EditText mEditTextUserEmail;

    @BindView(R.id.et_user_password_sign_up_id)
    EditText mEditTextUserPassword;

    @BindView(R.id.et_user_confirm_password_sign_up_id)
    EditText mEditTextUserConfirmPassword;

    @BindView(R.id.btn_sign_up)
    Button btnSignUp;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private String email ;
    private String password;
    private String confirmPassword;

    @Override
    protected void onStart() {
        super.onStart();
        if( FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(SignupActivity.this , HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        mEditTextUserConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    email = mEditTextUserEmail.getText().toString();
                    password = mEditTextUserPassword.getText().toString();
                    confirmPassword = mEditTextUserConfirmPassword.getText().toString();
                    signUp(email,password,confirmPassword);
                }
                return false;
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                email = mEditTextUserEmail.getText().toString();
                password = mEditTextUserPassword.getText().toString();
                confirmPassword = mEditTextUserConfirmPassword.getText().toString();
                signUp(email,password,confirmPassword);
            }
        });
    }

    private void signUp(String email,String password,String confirmPassword){
        if (email == null || TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(),"enter your email",Toast.LENGTH_LONG).show();
            return;
        }
        if (password == null || TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"enter your password",Toast.LENGTH_LONG).show();
            return;
        }

        if (confirmPassword == null || TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(getApplicationContext(),"confirm your password",Toast.LENGTH_LONG).show();
            return;
        }

        if (!confirmPassword.equals(password)){
            Toast.makeText(getApplicationContext(),"password mismatch",Toast.LENGTH_LONG).show();
            return;
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mProgressBar.setVisibility(View.GONE);

                if (task.isSuccessful()){
                    Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    mEditTextUserEmail.setError("Invalid User Name , email already found");
                    mEditTextUserEmail.requestFocus();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressBar.setVisibility(View.GONE);
                mEditTextUserEmail.setError("Invalid User Name or email already found");
                mEditTextUserEmail.requestFocus();
            }
        });
    }
}
