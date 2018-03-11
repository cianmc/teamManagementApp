package com.example.cianm.testauth.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

    private DatabaseReference mDatabase, mAttendeeReference;

    String eventKey;
    ArrayList<String> attendeeNames;

    ListView lvAttendees;
    Button mSubmitRating;
    TextView mNoAttendee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String currentTeam = ((GlobalVariables) ViewAttendeesTraining.this.getApplication()).getCurrentTeam();
        setContentView(R.layout.activity_view_attendees_training);
        setTitle("Attendees for Training");

        final String currentEvent = ((GlobalVariables) ViewAttendeesTraining.this.getApplication()).getCurrentEvent();
        attendeeNames = new ArrayList<>();

        lvAttendees = (ListView) findViewById(R.id.attendeesListView);
        mSubmitRating = (Button) findViewById(R.id.submitStats);
        mNoAttendee = (TextView) findViewById(R.id.noAttendeeTraining);

        mNoAttendee.setVisibility(View.INVISIBLE);

        mDatabase = FirebaseDatabase.getInstance().getReference("Training").child(currentTeam);
        mDatabase.orderByChild("date").equalTo(currentEvent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    eventKey = child.getKey();
                    mAttendeeReference = mDatabase.child(eventKey).child("attenedee");
                    mAttendeeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                lvAttendees.setVisibility(View.INVISIBLE);
                                mSubmitRating.setVisibility(View.INVISIBLE);
                                mNoAttendee.setVisibility(View.VISIBLE);
                            } else {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String attendeeName = ds.getValue(String.class);
                                    attendeeNames.add(attendeeName);
                                }
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ViewAttendeesTraining.this, android.R.layout.simple_list_item_1, attendeeNames);
                                lvAttendees.setAdapter(arrayAdapter);
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

        mSubmitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewAttendeesTraining.this, TrainingRating.class));
            }
        });
    }
}
