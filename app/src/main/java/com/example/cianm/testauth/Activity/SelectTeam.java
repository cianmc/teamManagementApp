package com.example.cianm.testauth.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cianm.testauth.Entity.GlobalVariables;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectTeam extends AppCompatActivity {

    private DatabaseReference mDatabaseT, mDatabaseU;
    private FirebaseAuth mAuth;
    private FirebaseUser fbUser;

    TextView mNoTeam;
    ListView lv;
    Button mJoinTeam;
    ProgressBar mProgressBar;

    User user;
    String teamID, userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_team);
        setTitle("Select Team");

        final ArrayList<String> teamNames = new ArrayList<>();

        lv = (ListView) findViewById(R.id.selectTeamListView);
        mJoinTeam = (Button) findViewById(R.id.joinTeamBtn);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mNoTeam = (TextView) findViewById(R.id.noTeamTextView);

        mProgressBar.setVisibility(View.INVISIBLE);
        mNoTeam.setVisibility(View.INVISIBLE);
        lv.setVisibility(View.VISIBLE);
        mJoinTeam.setVisibility(View.INVISIBLE);

        mDatabaseT = FirebaseDatabase.getInstance().getReference("Team");
        mDatabaseU = FirebaseDatabase.getInstance().getReference("User");
        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();

        final String id = mDatabaseU.push().getKey();
//        mDatabaseU.child("savedDates").child("AFL2").child(id).setValue("30/9/2018");

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userTeamRef = rootRef.child("User").child(uid).child("team");

        userTeamRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mNoTeam.setVisibility(View.VISIBLE);
                    mJoinTeam.setVisibility(View.VISIBLE);
                    lv.setVisibility(View.INVISIBLE);
                } else {
                    mJoinTeam.setVisibility(View.INVISIBLE);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String teamName = ds.getValue(String.class);
                        teamNames.add(teamName);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SelectTeam.this, android.R.layout.select_dialog_item, teamNames);
                    lv.setAdapter(arrayAdapter);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            mJoinTeam.setVisibility(View.GONE);
                            teamID = adapterView.getItemAtPosition(i).toString();
                            mDatabaseU.child(fbUser.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    user = dataSnapshot.getValue(User.class);
                                    userType = user.getType();
                                    ((GlobalVariables) SelectTeam.this.getApplication()).setCurrentTeam(teamID);
                                    if (user.getType().equalsIgnoreCase("Manager")) {
                                        startActivity(new Intent(SelectTeam.this, ManagerHome.class));
                                    } else if (user.getType().equalsIgnoreCase("Player")) {
                                        startActivity(new Intent(SelectTeam.this, PlayerHome.class));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            Toast.makeText(SelectTeam.this, "Loading data for team " + teamID, Toast.LENGTH_SHORT).show();
                        }
                    });
           }

        }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        mJoinTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mJoinTeam.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(SelectTeam.this, JoinTeam.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.INVISIBLE);
        mJoinTeam.setVisibility(View.VISIBLE);
    }

}
