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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ViewEvent extends AppCompatActivity {

    DatabaseReference rootRef, trainingRef, fixtureRef;
    private Query mDatabaseQueryT, mDatabaseQueryF;

    ListView mTrainingLV, mFixtureLV;
    String cEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        String currentTeam = ((GlobalVariables) ViewEvent.this.getApplication()).getCurrentTeam();
        setTitle("All events for " + currentTeam);

//        final ArrayList<String> trainings = new ArrayList<>();
//        final ArrayList<String> fixtures = new ArrayList<>();

        mTrainingLV = (ListView) findViewById(R.id.trainingListView);
        mFixtureLV = (ListView) findViewById(R.id.fixtureListView);

        mDatabaseQueryT = FirebaseDatabase.getInstance().getReference("Training").child(currentTeam);
        mDatabaseQueryT.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                collectTrainingEvents((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseQueryF = FirebaseDatabase.getInstance().getReference("Fixture").child(currentTeam);
        mDatabaseQueryF.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                collectFixtureEvents((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void collectTrainingEvents(Map<String, Object> trainEvents){

        final ArrayList<String> trainings = new ArrayList<>();

        for (Map.Entry<String, Object> entry : trainEvents.entrySet()){

            Map singleTraining = (Map) entry.getValue();
            trainings.add((String) singleTraining.get("date"));

        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, trainings);
        mTrainingLV.setAdapter(arrayAdapter);
        mTrainingLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cEvent = adapterView.getItemAtPosition(i).toString();
                ((GlobalVariables) ViewEvent.this.getApplication()).setCurrentEvent(cEvent);
                startActivity(new Intent(ViewEvent.this, ViewIndividualTraining.class));
            }
        });
    }

    private void collectFixtureEvents(Map<String, Object> fixtureEvents){

        final ArrayList<String> fixtures = new ArrayList<>();

        for (Map.Entry<String, Object> entry : fixtureEvents.entrySet()){

            Map singleFixture = (Map) entry.getValue();
            fixtures.add((String) singleFixture.get("date"));

        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, fixtures);
        mFixtureLV.setAdapter(arrayAdapter);
        mFixtureLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cEvent = adapterView.getItemAtPosition(i).toString();
                ((GlobalVariables) ViewEvent.this.getApplication()).setCurrentEvent(cEvent);
                startActivity(new Intent(ViewEvent.this, ViewIndividualFixture.class));
            }
        });
    }
}

