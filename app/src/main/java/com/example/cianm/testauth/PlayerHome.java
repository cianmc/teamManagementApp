package com.example.cianm.testauth;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.cianm.testauth.Activity.MainActivity;
import com.example.cianm.testauth.Activity.SelectTeam;
import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.Entity.User;
import com.example.cianm.testauth.Fragment.JoinTeamFragment;
import com.example.cianm.testauth.Fragment.PlayerHomeFragment;
import com.example.cianm.testauth.Fragment.SettingsFragment;
import com.example.cianm.testauth.Fragment.ViewEventFragment;
import com.example.cianm.testauth.Fragment.ViewEventPlayerFragment;
import com.example.cianm.testauth.Fragment.ViewMembersFragment;
import com.example.cianm.testauth.Fragment.ViewRatingsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlayerHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    FirebaseUser fbUser;
    TextView txtName, txtEmail;
    View navHeader;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String currentTeam = ((GlobalVariables) PlayerHome.this.getApplication()).getCurrentTeam();
        setContentView(R.layout.activity_player_home);
        setTitle("Player Home Page:" + currentTeam);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.phNameTextView);
        txtEmail = (TextView) navHeader.findViewById(R.id.phEmailTextView);

        // load navagation headed contents
        loadNavHeader();

        // set default screen
        displaySelectedScreen(R.id.navP_home);
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
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.navP_home:
                fragment = new PlayerHomeFragment();
                break;
            case R.id.navP_join_team:
                fragment = new JoinTeamFragment();
                break;
            case R.id.navP_view_members:
                fragment = new ViewMembersFragment();
                break;
            case R.id.navP_view_event:
                fragment = new ViewEventPlayerFragment();
                break;
            case R.id.navP_view_ratings:
                fragment = new ViewRatingsFragment();
                break;
            case  R.id.navP_changeTeam:
                Intent intent = new Intent (PlayerHome.this, SelectTeam.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.navP_settings:
                fragment = new SettingsFragment();
                break;
            case R.id.navP_signOut:
                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "Signing out...", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent (PlayerHome.this, MainActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}
