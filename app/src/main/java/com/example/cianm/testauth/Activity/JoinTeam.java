package com.example.cianm.testauth.Activity;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cianm.testauth.Entity.Fixture;
import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.Entity.Training;
import com.example.cianm.testauth.Entity.User;
import com.example.cianm.testauth.ManagerHome;
import com.example.cianm.testauth.PlayerHome;
import com.example.cianm.testauth.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Map;

public class JoinTeam extends AppCompatActivity {

    private DatabaseReference mDatabaseT, mDatabaseU, mTeamEmails;
    private FirebaseAuth mAuth;
    private FirebaseUser fbUser;
    private Query mDatabaseQuery;
    private ProgressBar mProgressBar;

    ListView lv;
    TextView mNoTeam;

    String teamID, userName, userType, email;
    ArrayList<String> teams;

    User user;
    Fixture fixture;
    Training training;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);
        setTitle("Join Team");
        lv = (ListView) findViewById(R.id.teamListView);
        mNoTeam = (TextView) findViewById(R.id.noTeamTextView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mDatabaseT = FirebaseDatabase.getInstance().getReference("Team");
        mDatabaseU = FirebaseDatabase.getInstance().getReference("User");
        mTeamEmails = FirebaseDatabase.getInstance().getReference("TeamEmails");
        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        teams = new ArrayList<>();

        mDatabaseU.child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                userType = user.getType();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseQuery = FirebaseDatabase.getInstance().getReference("Team");
        mDatabaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    if (userType.equalsIgnoreCase("Manager")){
                        lv.setVisibility(View.INVISIBLE);
                        mNoTeam.setVisibility(View.VISIBLE);
                        Toast.makeText(JoinTeam.this,"No teams created, you need to create a team first before joining it", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent (JoinTeam.this, CreateTeam.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else if (userType.equalsIgnoreCase("Player")){
                        lv.setVisibility(View.INVISIBLE);
                        mNoTeam.setVisibility(View.VISIBLE);
                        Toast.makeText(JoinTeam.this,"No teams have been created yet" + teamID, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent (JoinTeam.this, PlayerHome.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                } else {
                    collectTeamNames((Map<String, Object>) dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void collectTeamNames(final Map<String, Object> teams){

        final ArrayList<String> teamNames = new ArrayList<>();

        for (Map.Entry<String, Object> entry : teams.entrySet()){

            Map singleTeam = (Map) entry.getValue();
            teamNames.add((String) singleTeam.get("name"));

        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, teamNames);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mProgressBar.setVisibility(View.VISIBLE);
                lv.setVisibility(View.INVISIBLE);
                teamID = adapterView.getItemAtPosition(i).toString();
                if (teams.containsValue(teamID)) {
                    Toast.makeText(JoinTeam.this,"You're already a member of this team", Toast.LENGTH_LONG).show();
                } else {
                    mDatabaseU.child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String userTeamID = mDatabaseU.push().getKey();
                            String id = mTeamEmails.push().getKey();
                            user = dataSnapshot.getValue(User.class);
                            userName = user.getName();
                            email = user.getEmail();
                            if (user.getType().equalsIgnoreCase("Manager")) {
                                mDatabaseT.child(teamID).child("manager").child(fbUser.getUid()).setValue(userName);
                                mDatabaseU.child(fbUser.getUid()).child("team").child(userTeamID).setValue(teamID);
                                Toast.makeText(JoinTeam.this, "Joining team: " + teamID, Toast.LENGTH_SHORT).show();
                                ((GlobalVariables) JoinTeam.this.getApplication()).setCurrentTeam(teamID);
                                FirebaseMessaging.getInstance().subscribeToTopic(teamID);
                                Intent intent = new Intent(JoinTeam.this, ManagerHome.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else if (user.getType().equalsIgnoreCase("Player")) {
                                addFixtures();
                                addTrainings();
                                mTeamEmails.child(teamID).child(id).setValue(email);
                                mDatabaseT.child(teamID).child("player").child(fbUser.getUid()).setValue(userName);
                                mDatabaseU.child(fbUser.getUid()).child("team").child(userTeamID).setValue(teamID);
                                Toast.makeText(JoinTeam.this, "Joining team: " + teamID, Toast.LENGTH_SHORT).show();
                                ((GlobalVariables) JoinTeam.this.getApplication()).setCurrentTeam(teamID);
                                FirebaseMessaging.getInstance().subscribeToTopic(teamID);
                                Intent intent = new Intent(JoinTeam.this, PlayerHome.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    public void addFixtures(){
        mDatabaseT.child(teamID).child("fixtures").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(JoinTeam.this,
                            "No previous trainings created",
                            Toast.LENGTH_LONG).show();
                } else {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String userTeamID = mDatabaseU.push().getKey();
                        fixture = ds.getValue(Fixture.class);
                        mDatabaseU.child(fbUser.getUid())
                                .child("pending")
                                .child(teamID)
                                .child("Fixture")
                                .child(userTeamID)
                                .setValue(fixture);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addTrainings(){
        mDatabaseT.child(teamID).child("trainings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(JoinTeam.this,"No previous fixtures created", Toast.LENGTH_LONG).show();
                } else {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String userTeamID = mDatabaseU.push().getKey();
                        training = ds.getValue(Training.class);
                        mDatabaseU.child(fbUser.getUid()).child("pending").child(teamID).child("Training").child(userTeamID).setValue(training);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getCurrentTeams(){
        mDatabaseU.child(fbUser.getUid()).child("team").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(JoinTeam.this,"No teams joined", Toast.LENGTH_LONG).show();
                } else {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        String team = ds.getValue(String.class);
                        teams.add(team);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        lv.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
