package com.example.cianm.testauth.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectPlayer extends AppCompatActivity {

    DatabaseReference mFixRef, mSavedPlayerRef;
    ListView mSavedPlayers;

    String currentTeam, currentEvent, eventKey, cPlayer;
    ArrayList<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_player);
        setTitle("Select Player");

        currentTeam = ((GlobalVariables) SelectPlayer.this.getApplication()).getCurrentTeam();
        currentEvent = ((GlobalVariables) SelectPlayer.this.getApplication()).getCurrentEvent();
        names = new ArrayList<>();

        mSavedPlayers = (ListView) findViewById(R.id.selectPlayerLV);

        mFixRef = FirebaseDatabase.getInstance().getReference("Fixture").child(currentTeam);
        mFixRef.orderByChild("date").equalTo(currentEvent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    eventKey = ds.getKey();
                    mSavedPlayerRef = mFixRef.child(eventKey).child("attenedee").child("saved");
                    mSavedPlayerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String name = ds.getValue(String.class);
                                names.add(name);
                            }
                            ArrayAdapter adapter = new ArrayAdapter(SelectPlayer.this, android.R.layout.simple_list_item_1, names);
                            mSavedPlayers.setAdapter(adapter);
                            mSavedPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    cPlayer = adapterView.getItemAtPosition(i).toString();
                                    ((GlobalVariables) SelectPlayer.this.getApplication()).setCurrentPlayer(cPlayer);
                                    startActivity(new Intent(SelectPlayer.this, ViewRating.class));
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
