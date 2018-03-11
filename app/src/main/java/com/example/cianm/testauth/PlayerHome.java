package com.example.cianm.testauth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.cianm.testauth.Activity.MainActivity;
import com.example.cianm.testauth.Entity.GlobalVariables;
import com.google.firebase.auth.FirebaseAuth;

public class PlayerHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;

    Button mJoinTeam, mSignOut, mViewEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String currentTeam = ((GlobalVariables) PlayerHome.this.getApplication()).getCurrentTeam();
        setContentView(R.layout.activity_player_home);
        setTitle("Player Home Page:" + currentTeam);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        contentItems();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void contentItems(){
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

        if(id == R.id.navP_home){

        } else if (id == R.id.navP_join_team) {
            // Handle the camera action
        } else if (id == R.id.navP_view_event) {

        } else if (id == R.id.navP_view_ratings) {

        } else if (id == R.id.navP_settings) {

        } else if (id == R.id.navP_signOut){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
