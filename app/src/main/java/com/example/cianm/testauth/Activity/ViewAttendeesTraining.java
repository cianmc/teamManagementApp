package com.example.cianm.testauth.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAttendeesTraining extends AppCompatActivity {

    private DatabaseReference mDatabase, mAttendeeGoing, mAttendeeNotGoing;

    ArrayList<String> goingNames, notGoingNames;

    ListView mGoingLV, mNotGoingLV;
    Button mGoing, mNotGoing;
    TextView mNoDataGoing, mNoDataNotGoing, mNoDataSaved;
    String currentTeam, currentEvent, eventKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentTeam = ((GlobalVariables) ViewAttendeesTraining.this.getApplication()).getCurrentTeam();
        setContentView(R.layout.activity_view_attendees_training);
        setTitle("Attendees for Training");

        currentEvent = ((GlobalVariables) ViewAttendeesTraining.this.getApplication()).getCurrentEvent();
        goingNames = new ArrayList<>();
        notGoingNames = new ArrayList<>();

        mGoingLV = (ListView) findViewById(R.id.goingTrainingLV);
        mNotGoingLV = (ListView) findViewById(R.id.notGoingTrainingLV);
        mNoDataGoing = (TextView) findViewById(R.id.noDataGoingTrainingTV);
        mNoDataNotGoing = (TextView) findViewById(R.id.noDataNotGoingTrainingTV);
        mNoDataSaved = (TextView) findViewById(R.id.noDataSavedTrainingTV);
        mGoing = (Button) findViewById(R.id.goingBtn);
        mNotGoing = (Button) findViewById(R.id.notGoingBtn);

        mNoDataGoing.setVisibility(View.INVISIBLE);
        mNoDataNotGoing.setVisibility(View.INVISIBLE);
        mNoDataSaved.setVisibility(View.INVISIBLE);
        mGoingLV.setVisibility(View.INVISIBLE);
        mNotGoingLV.setVisibility(View.INVISIBLE);

        goingLV();

        mGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goingLV();
            }
        });

        mNotGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notGoingLV();
            }
        });
    }

    public void goingLV(){
        goingNames.clear();
        mGoing.setBackground(ViewAttendeesTraining.this.getResources().getDrawable(R.drawable.bkg));
        mNotGoing.setBackground(ViewAttendeesTraining.this.getResources().getDrawable(R.drawable.not_selected));
        mNoDataGoing.setVisibility(View.INVISIBLE);
        mNoDataNotGoing.setVisibility(View.INVISIBLE);
        mNoDataSaved.setVisibility(View.INVISIBLE);
        mGoingLV.setVisibility(View.VISIBLE);
        mNotGoingLV.setVisibility(View.INVISIBLE);
        mDatabase = FirebaseDatabase.getInstance().getReference("Training").child(currentTeam);
        mDatabase.orderByChild("date").equalTo(currentEvent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    eventKey = child.getKey();
                    mAttendeeGoing = mDatabase.child(eventKey).child("attenedee").child("attending");
                    mAttendeeGoing.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                mGoingLV.setVisibility(View.INVISIBLE);
                                mNoDataGoing.setVisibility(View.VISIBLE);
                            } else {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String attendeeName = ds.getValue(String.class);
                                    goingNames.add(attendeeName);
                                }
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ViewAttendeesTraining.this, android.R.layout.simple_list_item_1, goingNames);
                                mGoingLV.setAdapter(arrayAdapter);
                            }
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

    public void notGoingLV(){
        notGoingNames.clear();
        mGoing.setBackground(ViewAttendeesTraining.this.getResources().getDrawable(R.drawable.not_selected));
        mNotGoing.setBackground(ViewAttendeesTraining.this.getResources().getDrawable(R.drawable.bkg));
        mNoDataGoing.setVisibility(View.INVISIBLE);
        mNoDataNotGoing.setVisibility(View.INVISIBLE);
        mNoDataSaved.setVisibility(View.INVISIBLE);
        mGoingLV.setVisibility(View.INVISIBLE);
        mNotGoingLV.setVisibility(View.VISIBLE);
        mDatabase = FirebaseDatabase.getInstance().getReference("Training").child(currentTeam);
        mDatabase.orderByChild("date").equalTo(currentEvent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    eventKey = child.getKey();
                    mAttendeeNotGoing = mDatabase.child(eventKey).child("attenedee").child("notAttending");
                    mAttendeeNotGoing.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                mNotGoingLV.setVisibility(View.INVISIBLE);
                                mNoDataNotGoing.setVisibility(View.VISIBLE);
                            } else {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String attendeeName = ds.getValue(String.class);
                                    notGoingNames.add(attendeeName);
                                }
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ViewAttendeesTraining.this, android.R.layout.simple_list_item_1, notGoingNames);
                                mNotGoingLV.setAdapter(arrayAdapter);
                            }
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

