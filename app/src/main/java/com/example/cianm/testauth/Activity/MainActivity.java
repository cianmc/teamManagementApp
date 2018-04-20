package com.example.cianm.testauth.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cianm.testauth.ManagerHome;
import com.example.cianm.testauth.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private  static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mEmail, mPassword;
    private Button mSignIn, mForgotPass, mRegister;
    private ProgressBar mProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = (EditText) findViewById(R.id.emailField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        mSignIn = (Button) findViewById(R.id.registerBtn);
        mForgotPass = (Button) findViewById(R.id.forgotPassBtn);
        mRegister = (Button) findViewById(R.id.noAccountBtn);
        mProgressbar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this, CheckValidation.class));
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in" + user.getUid());
                    Toast.makeText(getApplicationContext(), "Sucessfully signed in with: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent (MainActivity.this, SelectTeam.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Please enter in your email");
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Please enter in your password");
                    return;
                } else {
                    mProgressbar.setVisibility(View.VISIBLE);
                    mSignIn.setVisibility(View.GONE);
                    mForgotPass.setVisibility(View.GONE);
                    mRegister.setVisibility(View.GONE);
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                            } else {
                                mProgressbar.setVisibility(View.GONE);
                                mSignIn.setVisibility(View.VISIBLE);
                                mForgotPass.setVisibility(View.VISIBLE);
                                mRegister.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "Wrong email or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        mForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ResetPassword.class));
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterUser.class));

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProgressbar.setVisibility(View.GONE);
    }

    public void homePage(View v){
        Intent i = new Intent(this, CheckValidation.class );
        startActivity(i);
    }
}
