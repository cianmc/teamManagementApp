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

import com.example.cianm.testauth.Activity.SelectPlayer;
import com.example.cianm.testauth.Activity.SelectTeam;
import com.example.cianm.testauth.Activity.ViewRating;
import com.example.cianm.testauth.Activity.ViewRatingPlayer;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by cianm on 14/03/2018.
 */

public class ViewRatingsFragment extends Fragment {

    DatabaseReference mUserRef, mSavedDateRef;
    FirebaseAuth mAuth;
    FirebaseUser fbUser;

    ListView mSavedDates;
    TextView mNoData;
    ProgressBar mProgress;

    ArrayList<String> dates;
    String userType, cEvent, currentTeam;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_view_ratings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        currentTeam = ((GlobalVariables) getActivity().getApplication()).getCurrentTeam();
        getActivity().setTitle("View Ratings: " + currentTeam);

        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        dates = new ArrayList<>();
        mUserRef = FirebaseDatabase.getInstance().getReference("User");
        mSavedDateRef = FirebaseDatabase.getInstance().getReference("SavedDates");

        mSavedDates = (ListView) getView().findViewById(R.id.savedEventsLV);
        mProgress = (ProgressBar) getView().findViewById(R.id.progressBar2);
        mNoData = (TextView) getView().findViewById(R.id.noEventsSaved);

        getUserType();

    }

    public void getUserType(){
        mUserRef = FirebaseDatabase.getInstance().getReference("User");
        mUserRef.child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userType = user.getType();
                if(userType.equalsIgnoreCase("Manager")){
                    getDatesManager();
                } else {
                    getDatesPlayer();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getDatesManager(){
        mSavedDateRef.child(currentTeam).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mNoData.setVisibility(View.VISIBLE);
                    mSavedDates.setVisibility(View.INVISIBLE);
                } else {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String date = ds.getValue(String.class);
                        dates.add(date);
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, dates);
                    mSavedDates.setAdapter(arrayAdapter);
                    mSavedDates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            mSavedDates.setVisibility(View.INVISIBLE);
                            mProgress.setVisibility(View.VISIBLE);
                            cEvent = adapterView.getItemAtPosition(i).toString();
                            ((GlobalVariables) getActivity().getApplicationContext()).setCurrentEvent(cEvent);
                            startActivity(new Intent(getActivity(), SelectPlayer.class));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getDatesPlayer(){
        mUserRef.child(fbUser.getUid()).child("savedDates").child(currentTeam).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mNoData.setVisibility(View.VISIBLE);
                    mSavedDates.setVisibility(View.INVISIBLE);
                } else {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String date = ds.getValue(String.class);
                        dates.add(date);
                    }
                    ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, dates);
                    mSavedDates.setAdapter(adapter);
                    mSavedDates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            mSavedDates.setVisibility(View.INVISIBLE);
                            mProgress.setVisibility(View.VISIBLE);
                            cEvent = adapterView.getItemAtPosition(i).toString();
                            ((GlobalVariables) getActivity().getApplicationContext()).setCurrentEvent(cEvent);
                            startActivity(new Intent(getActivity(), ViewRatingPlayer.class));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        mSavedDates.setVisibility(View.VISIBLE);
        mProgress.setVisibility(View.INVISIBLE);
        mNoData.setVisibility(View.INVISIBLE);
    }
}
