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

public class ViewAttendeesFixture extends AppCompatActivity {

    private DatabaseReference mDatabase, mAttendeeReference;

    String eventKey;
    ArrayList<String> attendeeNames;

    ListView lvAttendees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String currentTeam = ((GlobalVariables) ViewAttendeesFixture.this.getApplication()).getCurrentTeam();
        setContentView(R.layout.activity_view_attendees_fixture);
        setTitle("Attendees for Fixture");

        final String currentEvent = ((GlobalVariables) ViewAttendeesFixture.this.getApplication()).getCurrentEvent();
        attendeeNames = new ArrayList<>();

        lvAttendees = (ListView) findViewById(R.id.attendeesListView);

        mDatabase = FirebaseDatabase.getInstance().getReference("Fixture").child(currentTeam);
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
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ViewAttendeesFixture.this, android.R.layout.select_dialog_singlechoice, attendeeNames);
                            lvAttendees.setAdapter(arrayAdapter);
                            lvAttendees.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    startActivity(new Intent(ViewAttendeesFixture.this, ViewEvent.class));
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
