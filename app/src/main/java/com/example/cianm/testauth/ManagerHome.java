package com.example.cianm.testauth;

import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cianm.testauth.Activity.MainActivity;
import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManagerHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    FirebaseUser fbUser;
    Button btnSignOut, mCreateTeam, mJoinTeam, mCreateEvent, mViewEvent;
    TextView txtName, txtEmail;
    View navHeader;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);
        String currentTeam = ((GlobalVariables) ManagerHome.this.getApplication()).getCurrentTeam();
        setTitle("Manager Home Page: " + currentTeam);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        contentItems();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.mhNameTextView);
        txtEmail = (TextView) navHeader.findViewById(R.id.mhEmailTextView);

        loadNavHeader();
    }

    public void contentItems(){
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

    private void loadNavHeader() {
        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("User");
        mDatabase.child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                txtName.setText(user.getName());
                txtEmail.setText(user.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.navM_home) {

        } else if (id == R.id.navM_create_team) {
            startActivity(new Intent(ManagerHome.this, CreateTeam.class));
        } else if (id == R.id.navM_join_team) {

        } else if (id == R.id.navM_create_event) {

        } else if (id == R.id.navM_view_event) {

        } else if (id == R.id.navM_view_ratings) {

        } else if (id == R.id.navM_settings) {

        } else if (id == R.id.navM_sign_out) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}