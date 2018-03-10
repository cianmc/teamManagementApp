    package com.example.cianm.testauth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

    public class JoinTeam extends AppCompatActivity {

    private DatabaseReference mDatabaseT, mDatabaseU;
    private FirebaseAuth mAuth;
    private FirebaseUser fbUser;
    private Query mDatabaseQuery;

    ListView lv;
    TextView mNoTeam;

    User user;
    String teamID, userName, userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);

        lv = (ListView) findViewById(R.id.teamListView);
        mNoTeam = (TextView) findViewById(R.id.noTeamTextView);

        mDatabaseT = FirebaseDatabase.getInstance().getReference("Team");
        mDatabaseU = FirebaseDatabase.getInstance().getReference("User");
        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();

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
                        startActivity(new Intent(JoinTeam.this, CreateTeam.class));
                    } else if (userType.equalsIgnoreCase("Player")){
                        lv.setVisibility(View.INVISIBLE);
                        mNoTeam.setVisibility(View.VISIBLE);
                        Toast.makeText(JoinTeam.this,"No teams have been created yet" + teamID, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(JoinTeam.this, PlayerHome.class));
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

    private void collectTeamNames(Map<String, Object> teams){

        final ArrayList<String> teamNames = new ArrayList<>();

        for (Map.Entry<String, Object> entry : teams.entrySet()){

            Map singleTeam = (Map) entry.getValue();
            teamNames.add((String) singleTeam.get("name"));

        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, teamNames);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                teamID = adapterView.getItemAtPosition(i).toString();
                mDatabaseU.child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userTeamID = mDatabaseU.push().getKey();
                        user = dataSnapshot.getValue(User.class);
                        userName = user.getName();
                        if (user.getType().equalsIgnoreCase("Manager")) {
                            mDatabaseT.child(teamID).child("manager").child(fbUser.getUid()).setValue(userName);
                            startActivity(new Intent(JoinTeam.this, ManagerHome.class));
                        } else if (user.getType().equalsIgnoreCase("Player")){
                            mDatabaseT.child(teamID).child("player").child(fbUser.getUid()).setValue(userName);
                            startActivity(new Intent(JoinTeam.this, PlayerHome.class));
                        }
                        mDatabaseU.child(fbUser.getUid()).child("team").child(userTeamID).setValue(teamID);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Toast.makeText(JoinTeam.this,"Joining team: " + teamID, Toast.LENGTH_SHORT).show();
                ((GlobalVariables) JoinTeam.this.getApplication()).setCurrentTeam(teamID);
            }
        });
    }
}
