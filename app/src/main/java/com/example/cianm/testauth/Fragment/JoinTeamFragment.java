package com.example.cianm.testauth.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cianm.testauth.Activity.CreateTeam;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by cianm on 13/03/2018.
 */

public class JoinTeamFragment extends Fragment {

    private DatabaseReference mDatabaseT, mDatabaseU;
    private FirebaseAuth mAuth;
    private FirebaseUser fbUser;
    private Query mDatabaseQuery;

    ListView lv;
    TextView mNoTeam;

    User user;
    String teamID, userName, userType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_join_team, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Join Team");
        lv = (ListView) getView().findViewById(R.id.teamListView);
        mNoTeam = (TextView) getView().findViewById(R.id.noTeamTextView);

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
                        Toast.makeText(getActivity(),"No teams created, you need to create a team first before joining it", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getActivity(), CreateTeam.class));
                    } else if (userType.equalsIgnoreCase("Player")){
                        lv.setVisibility(View.INVISIBLE);
                        mNoTeam.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(),"No teams have been created yet" + teamID, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), PlayerHome.class));
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, teamNames);
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
                            startActivity(new Intent(getActivity(), ManagerHome.class));
                        } else if (user.getType().equalsIgnoreCase("Player")){
                            mDatabaseT.child(teamID).child("player").child(fbUser.getUid()).setValue(userName);
                            startActivity(new Intent(getActivity(), PlayerHome.class));
                        }
                        mDatabaseU.child(fbUser.getUid()).child("team").child(userTeamID).setValue(teamID);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Toast.makeText(getActivity(),"Joining team: " + teamID, Toast.LENGTH_SHORT).show();
                ((GlobalVariables) getActivity().getApplicationContext()).setCurrentTeam(teamID);
            }
        });
    }
}
