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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cianm.testauth.Activity.ViewIndividualFixture;
import com.example.cianm.testauth.Activity.ViewIndividualTraining;
import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by cianm on 14/03/2018.
 */

public class ViewEventFragment extends Fragment {

    DatabaseReference rootRef, trainingRef, fixtureRef;
    private Query mDatabaseQueryT, mDatabaseQueryF;

    ListView mTrainingLV, mFixtureLV;
    String cEvent;
    TextView mNoTraining, mNoFixture, mTraining, mFixture;
    ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_view_events, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        String currentTeam = ((GlobalVariables) getActivity().getApplication()).getCurrentTeam();
        getActivity().setTitle("View Events: " + currentTeam);

        mTrainingLV = (ListView) getView().findViewById(R.id.trainingListView);
        mFixtureLV = (ListView) getView().findViewById(R.id.fixtureListView);
        mNoTraining = (TextView) getView().findViewById(R.id.noTrainingsData);
        mNoFixture = (TextView) getView().findViewById(R.id.noFixturesData);
        mFixture = (TextView) getView().findViewById(R.id.fixtureTextView);
        mTraining = (TextView) getView().findViewById(R.id.trainingTextView);
        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);

        mNoTraining.setVisibility(View.INVISIBLE);
        mNoFixture.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);

        mDatabaseQueryT = FirebaseDatabase.getInstance().getReference("Training").child(currentTeam);
        mDatabaseQueryT.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    mTrainingLV.setVisibility(View.INVISIBLE);
                    mNoTraining.setVisibility(View.VISIBLE);
                } else {
                    collectTrainingEvents((Map<String, Object>) dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseQueryF = FirebaseDatabase.getInstance().getReference("Fixture").child(currentTeam);
        mDatabaseQueryF.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    mFixtureLV.setVisibility(View.INVISIBLE);
                    mNoFixture.setVisibility(View.VISIBLE);
                }else {
                    collectFixtureEvents((Map<String, Object>) dataSnapshot.getValue());
                }
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.select_dialog_item, trainings);
        mTrainingLV.setAdapter(arrayAdapter);
        mTrainingLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mTrainingLV.setVisibility(View.INVISIBLE);
                mTraining.setVisibility(View.INVISIBLE);
                mFixtureLV.setVisibility(View.INVISIBLE);
                mFixture.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                cEvent = adapterView.getItemAtPosition(i).toString();
                ((GlobalVariables) getActivity().getApplicationContext()).setCurrentEvent(cEvent);
                startActivity(new Intent(getActivity(), ViewIndividualTraining.class));
            }
        });
    }

    private void collectFixtureEvents(Map<String, Object> fixtureEvents){

        final ArrayList<String> fixtures = new ArrayList<>();

        for (Map.Entry<String, Object> entry : fixtureEvents.entrySet()){

            Map singleFixture = (Map) entry.getValue();
            fixtures.add((String) singleFixture.get("date"));

        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.select_dialog_item, fixtures);
        mFixtureLV.setAdapter(arrayAdapter);
        mFixtureLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mTrainingLV.setVisibility(View.INVISIBLE);
                mTraining.setVisibility(View.INVISIBLE);
                mFixtureLV.setVisibility(View.INVISIBLE);
                mFixture.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                cEvent = adapterView.getItemAtPosition(i).toString();
                ((GlobalVariables) getActivity().getApplicationContext()).setCurrentEvent(cEvent);
                startActivity(new Intent(getActivity(), ViewIndividualFixture.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mTrainingLV.setVisibility(View.VISIBLE);
        mTraining.setVisibility(View.VISIBLE);
        mFixtureLV.setVisibility(View.VISIBLE);
        mFixture.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
