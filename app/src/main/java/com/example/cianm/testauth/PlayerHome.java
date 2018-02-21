package com.example.cianm.testauth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class PlayerHome extends AppCompatActivity {

    private FirebaseAuth mAuth;

    Button mJoinTeam, mSignOut, mViewEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_home);
        setTitle("Player Home Page");

        mAuth = FirebaseAuth.getInstance();

        mJoinTeam = (Button) findViewById(R.id.joinTeamBtn);
        mSignOut = (Button) findViewById(R.id.signOutBtn);
        mViewEvent = (Button) findViewById(R.id.viewEventBtn);

        mJoinTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PlayerHome.this, JoinTeam.class));
            }
        });

        mViewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PlayerHome.this, ViewEvent.class));
            }
        });

        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "Signing out...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PlayerHome.this, MainActivity.class));
            }
        });
    }
}
