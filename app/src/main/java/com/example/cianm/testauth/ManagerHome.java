package com.example.cianm.testauth;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.cianm.testauth.Entity.GlobalVariables;
import com.google.firebase.auth.FirebaseAuth;

public class ManagerHome extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button btnSignOut, mCreateTeam, mJoinTeam, mCreateEvent, mViewEvent;
    TextView mCurrentTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);
        String currentTeam = ((GlobalVariables) ManagerHome.this.getApplication()).getCurrentTeam();
        setTitle("Manager Home Page: " + currentTeam);

        mAuth = FirebaseAuth.getInstance();

        btnSignOut = (Button) findViewById(R.id.signoutBtn);
        mCreateTeam = (Button) findViewById(R.id.createTeamBtn);
        mJoinTeam = (Button) findViewById(R.id.joinTeamBtn);
        mCreateEvent = (Button) findViewById(R.id.createEventBtn);
        mViewEvent = (Button) findViewById(R.id.viewEventBtn);

        mCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManagerHome.this, CreateTeam.class));
            }
        });

        mJoinTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManagerHome.this, JoinTeam.class));
            }
        });

        mCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManagerHome.this, CreateEvent.class));
            }
        });

        mViewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManagerHome.this, ViewEvent.class));
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "Signing out...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ManagerHome.this, MainActivity.class));
            }
        });
    }
}
