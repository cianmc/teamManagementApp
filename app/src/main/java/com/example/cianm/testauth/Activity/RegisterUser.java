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
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.cianm.testauth.Entity.User;
import com.example.cianm.testauth.ManagerHome;
import com.example.cianm.testauth.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mFirebaseDatabase;

    private EditText mEmail, mPassword, mName;
    private Button mRegister;
    private RadioButton rManager, rPlayer;
    private ProgressBar mProgressbar;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mEmail = (EditText) findViewById(R.id.emailField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        mName = (EditText) findViewById(R.id.nameField);
        rManager = (RadioButton) findViewById(R.id.radioManager);
        rPlayer = (RadioButton) findViewById(R.id.radioPlayer);
        mRegister = (Button) findViewById(R.id.registerBtn);
        mProgressbar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(RegisterUser.this, CheckValidation.class));
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser fbUser = firebaseAuth.getCurrentUser();
                if (fbUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in" + fbUser.getUid());
                    mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("User");
                    mFirebaseDatabase.child(fbUser.getUid()).setValue(user);
                    Toast.makeText(getApplicationContext(), "Sucessfully signed in with: " + fbUser.getEmail(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent (RegisterUser.this, CheckValidation.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String name = mName.getText().toString();

                String type = "";
                //radio buttons
                if (rManager.isChecked()) {
                    type = "Manager";
                } else if (rPlayer.isChecked()) {
                    type = "Player";
                }

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Please enter in a email address");
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Please enter in a password");
                    return;
                } else if (TextUtils.isEmpty(name)) {
                    mName.setError("Please enter in a name");
                    return;
                } else if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!rManager.isChecked() && !rPlayer.isChecked()) {
                    Toast.makeText(RegisterUser.this, "You must select either player or manager", Toast.LENGTH_LONG).show();
                } else {

                    mProgressbar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password);
                    user = new User(name, email, password, type);
                    mRegister.setVisibility(View.INVISIBLE);
                }

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
}
