package com.example.cianm.testauth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.cianm.testauth.Entity.GlobalVariables;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String currentTeam = ((GlobalVariables) ViewAttendeesTraining.this.getApplication()).getCurrentTeam();
        setContentView(R.layout.activity_view_attendees_training);
        setTitle("Attendees for Training");

        final String currentEvent = ((GlobalVariables) ViewAttendeesTraining.this.getApplication()).getCurrentEvent();
        attendeeNames = new ArrayList<>();

        lvAttendees = (ListView) findViewById(R.id.attendeesListView);

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
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                String attendeeName = ds.getValue(String.class);
                                attendeeNames.add(attendeeName);
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ViewAttendeesTraining.this, android.R.layout.select_dialog_singlechoice, attendeeNames);
                            lvAttendees.setAdapter(arrayAdapter);
                            lvAttendees.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    startActivity(new Intent(ViewAttendeesTraining.this, ViewEvent.class));
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
