package com.example.cianm.testauth.Fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.cianm.testauth.Activity.ViewIndividualFixture;
import com.example.cianm.testauth.Activity.ViewIndividualTraining;
import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cianm on 23/03/2018.
 */

public class ViewEventPlayerFragment extends Fragment {

    DatabaseReference mConfirmedTrainingsRef, mConfirmedFixturesRef, mPendingTrainingsRef, mPendingFixturesRef, mCheckPend, mCheckCon;
    FirebaseAuth mAuth;
    FirebaseUser fbUser;

    Button mResponded, mPending;
    ListView mTrainingRespondedLV, mFixtureRespondedLV, mTrainingPendingLV, mFixturePendingLV;
    TextView mNoDataRespTrain, mNoDataRespFix;
    LinearLayout mResponderLayout, mPendingLayout;

    String cEvent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_view_event_player, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        String currentTeam = ((GlobalVariables) getActivity().getApplication()).getCurrentTeam();
        getActivity().setTitle("View Event");

        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();

        mConfirmedTrainingsRef = FirebaseDatabase.getInstance().getReference("User").child(fbUser.getUid()).child("confirmed").child(currentTeam).child("Training");
        mConfirmedFixturesRef = FirebaseDatabase.getInstance().getReference("User").child(fbUser.getUid()).child("confirmed").child(currentTeam).child("Fixture");
        mPendingTrainingsRef = FirebaseDatabase.getInstance().getReference("User").child(fbUser.getUid()).child("pending").child(currentTeam).child("Training");
        mPendingFixturesRef = FirebaseDatabase.getInstance().getReference("User").child(fbUser.getUid()).child("pending").child(currentTeam).child("Fixture");

        mResponded = getView().findViewById(R.id.respondedBtn);
        mPending = getView().findViewById(R.id.pendingBtn);

        // Responded tab elements
        mResponderLayout = getView().findViewById(R.id.respondedLayout);
        mTrainingRespondedLV = getView().findViewById(R.id.trainingRespondedLV);
        mFixtureRespondedLV = getView().findViewById(R.id.fixtureResponedLV);

        // Pending tab elements
        mPendingLayout = getView().findViewById(R.id.pendingLayout);
        mTrainingPendingLV = getView().findViewById(R.id.trainingPendingLV);
        mFixturePendingLV = getView().findViewById(R.id.fixturePendingLV);

        mNoDataRespTrain = getView().findViewById(R.id.noDataTraining);
        mNoDataRespFix = getView().findViewById(R.id.noDataFixture);

        mNoDataRespTrain.setVisibility(View.INVISIBLE);
        mNoDataRespFix.setVisibility(View.INVISIBLE);
        mResponderLayout.setVisibility(View.VISIBLE);
        mPendingLayout.setVisibility(View.INVISIBLE);

        loadResponded();

        mResponded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadResponded();
            }
        });

        mPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPending();
        }
    });
}

    private void collectConfirmedTrainingEvents(Map<String, Object> trainConEvents){

        final ArrayList<String> trainings = new ArrayList<>();
        final ArrayList<String> confirmationStatus = new ArrayList<>();
        int currentItem = 0;
        final ArrayList<Map<String, String>> data2 = new ArrayList<Map<String, String>>();

        for (Map.Entry<String, Object> entry : trainConEvents.entrySet()) {

            Map singleTraining = (Map) entry.getValue();
            trainings.add((String) singleTraining.get("eventDate"));

            Map trainingStatus = (Map) entry.getValue();
            confirmationStatus.add((String) trainingStatus.get("availability"));

            Map<String, String> data = new HashMap<String, String>(2);
            data.put("date", trainings.get(currentItem));
            data.put("availability", confirmationStatus.get(currentItem));
            currentItem++;
            data2.add(data);
        }
        SimpleAdapter adapter = new SimpleAdapter(this.getActivity(), data2, android.R.layout.simple_list_item_2, new String[]{"date", "availability"}, new int[]{android.R.id.text1, android.R.id.text2});
        mTrainingRespondedLV.setAdapter(adapter);
        mTrainingRespondedLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, String> item = (HashMap<String, String>) adapterView.getItemAtPosition(i);
                cEvent = item.get("date");
                ((GlobalVariables) getActivity().getApplicationContext()).setCurrentEvent(cEvent);
                startActivity(new Intent(getActivity(), ViewIndividualTraining.class));
            }
        });

    }

    private void collectConfirmedFixtureEvents(Map<String, Object> fixConEvents){

        final ArrayList<String> fixtures = new ArrayList<>();
        final ArrayList<String> confirmationStatus = new ArrayList<>();
        int currentItem = 0;
        final ArrayList<Map<String, String>> data2 = new ArrayList<Map<String, String>>();

        for (Map.Entry<String, Object> entry : fixConEvents.entrySet()) {

            Map singleFixture = (Map) entry.getValue();
            fixtures.add((String) singleFixture.get("eventDate"));

            Map trainingStatus = (Map) entry.getValue();
            confirmationStatus.add((String) trainingStatus.get("availability"));

            Map<String, String> data = new HashMap<String, String>(2);
            data.put("date", fixtures.get(currentItem));
            data.put("availability", confirmationStatus.get(currentItem));
            currentItem++;
            data2.add(data);
        }
        SimpleAdapter adapter = new SimpleAdapter(this.getActivity(), data2, android.R.layout.simple_list_item_2, new String[]{"date", "availability"}, new int[]{android.R.id.text1, android.R.id.text2});
        mFixtureRespondedLV.setAdapter(adapter);
        mFixtureRespondedLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, String> item = (HashMap<String, String>) adapterView.getItemAtPosition(i);
                cEvent = item.get("date");
                ((GlobalVariables) getActivity().getApplicationContext()).setCurrentEvent(cEvent);
                startActivity(new Intent(getActivity(), ViewIndividualFixture.class));
            }
        });

    }

    private void collectPendingTrainingEvents(Map<String, Object> trainPendEvents){

        final ArrayList<String> trainings = new ArrayList<>();

        for (Map.Entry<String, Object> entry : trainPendEvents.entrySet()) {
            Map singleTraining = (Map) entry.getValue();
            trainings.add((String) singleTraining.get("date"));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.select_dialog_item, trainings);
        mTrainingPendingLV.setAdapter(arrayAdapter);
        mTrainingPendingLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cEvent = adapterView.getItemAtPosition(i).toString();
                ((GlobalVariables) getActivity().getApplicationContext()).setCurrentEvent(cEvent);
                startActivity(new Intent(getActivity(), ViewIndividualTraining.class));
            }
        });

    }

    private void collectPendingFixtureEvents(Map<String, Object> fixPendEvents){

        final ArrayList<String> fixtures = new ArrayList<>();

        for (Map.Entry<String, Object> entry : fixPendEvents.entrySet()) {
            Map singleTraining = (Map) entry.getValue();
            fixtures.add((String) singleTraining.get("date"));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.select_dialog_item, fixtures);
        mFixturePendingLV.setAdapter(arrayAdapter);
        mFixturePendingLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cEvent = adapterView.getItemAtPosition(i).toString();
                ((GlobalVariables) getActivity().getApplicationContext()).setCurrentEvent(cEvent);
                startActivity(new Intent(getActivity(), ViewIndividualFixture.class));
            }
        });

    }

    public void loadResponded(){
        mResponderLayout.setVisibility(View.VISIBLE);
        mPendingLayout.setVisibility(View.INVISIBLE);
        mNoDataRespFix.setVisibility(View.INVISIBLE);
        mNoDataRespTrain.setVisibility(View.INVISIBLE);
        mResponded.setBackground(ViewEventPlayerFragment.this.getResources().getDrawable(R.drawable.bkg));
        mPending.setBackground(ViewEventPlayerFragment.this.getResources().getDrawable(R.drawable.not_selected));
        mConfirmedTrainingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mTrainingRespondedLV.setVisibility(View.INVISIBLE);
                    mNoDataRespTrain.setVisibility(View.VISIBLE);
                } else {
                    collectConfirmedTrainingEvents((Map<String, Object>) dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mConfirmedFixturesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mFixtureRespondedLV.setVisibility(View.INVISIBLE);
                    mNoDataRespFix.setVisibility(View.VISIBLE);
                } else {
                    collectConfirmedFixtureEvents((Map<String, Object>) dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadPending(){
        mResponded.setBackground(ViewEventPlayerFragment.this.getResources().getDrawable(R.drawable.not_selected));
        mPending.setBackground(ViewEventPlayerFragment.this.getResources().getDrawable(R.drawable.bkg));
        mResponderLayout.setVisibility(View.INVISIBLE);
        mPendingLayout.setVisibility(View.VISIBLE);
        mNoDataRespFix.setVisibility(View.INVISIBLE);
        mNoDataRespTrain.setVisibility(View.INVISIBLE);
        mPendingTrainingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    mTrainingPendingLV.setVisibility(View.INVISIBLE);
                    mNoDataRespTrain.setVisibility(View.VISIBLE);
                } else {
                    collectPendingTrainingEvents((Map<String, Object>) dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mPendingFixturesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    mFixturePendingLV.setVisibility(View.INVISIBLE);
                    mNoDataRespFix.setVisibility(View.VISIBLE);
                } else {
                    collectPendingFixtureEvents((Map<String, Object>) dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
