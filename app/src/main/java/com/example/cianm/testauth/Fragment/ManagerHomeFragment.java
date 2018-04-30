package com.example.cianm.testauth.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cianm.testauth.Entity.BestPlayer;
import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.HomeRatingAdapter;
import com.example.cianm.testauth.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cianm on 13/03/2018.
 */

public class ManagerHomeFragment extends Fragment {

    DatabaseReference mRatingRef, mStillToSave;
    ListView mAttackersLV, mDefendersLV, mStillToSaveLV;
    TextView mNoDataA, mNoDataD, mNoneToSave;
    String playerName;
    double totalAtt, totalDef, totalOver, avgAtt, avgDef, avgOver;
    long noOfEvents, noOfPLayers;
    String eventNo, currentTeam, playerNo;
    int noofEvents, noPlayers, size;
    ArrayList<String> uids, datesToSave;
    ArrayList<BestPlayer> bestPlayersA, bestPlayersD;
    HomeRatingAdapter attackerAdapter, defenderAdapter;

    BestPlayer bestPlayer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_manager_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        currentTeam = ((GlobalVariables) getActivity().getApplication()).getCurrentTeam();
        getActivity().setTitle("Home Page: " + currentTeam);

        mAttackersLV = (ListView) getView().findViewById(R.id.bestAttackersLV);
        mDefendersLV = (ListView) getView().findViewById(R.id.bestDefendersLV);
        mStillToSaveLV = (ListView) getView().findViewById(R.id.stillToSaveLV);
        mNoDataA = (TextView) getView().findViewById(R.id.noDataAttack);
        mNoDataD = (TextView) getView().findViewById(R.id.noDataDef);
        mNoneToSave = (TextView) getView().findViewById(R.id.noneToSaveMH);

        uids = new ArrayList<>();
        datesToSave = new ArrayList<>();
        bestPlayersA = new ArrayList<>();
        bestPlayersD = new ArrayList<>();

        mStillToSave = FirebaseDatabase.getInstance().getReference("DatesToSave");
        mStillToSave.child(currentTeam).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    mStillToSaveLV.setVisibility(View.INVISIBLE);
                    mNoneToSave.setVisibility(View.VISIBLE);
                } else {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        String date = ds.child("date").getValue(String.class);
                        datesToSave.add(date);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, datesToSave);
                    mStillToSaveLV.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // get all the stats saved for each user and getting their average attacker, defender and overall
        // rating based on the number of games they have played
        mRatingRef = FirebaseDatabase.getInstance().getReference("Ratings").child(currentTeam);
        mRatingRef.child("Fixture").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()) {
                    noOfPLayers = dataSnapshot.getChildrenCount();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String key = ds.getKey();
                        mRatingRef.child("Fixture").child(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    noOfEvents = dataSnapshot.getChildrenCount();
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        double att = ds.child("attackerRating").getValue(Double.class);
                                        double def = ds.child("defenderRating").getValue(Double.class);
                                        double over = ds.child("overallRating").getValue(Double.class);
                                        playerName = ds.child("playerName").getValue(String.class);

                                        totalAtt = totalAtt + att;
                                        totalDef = totalDef + def;
                                        totalOver = totalOver + over;
                                    }
                                    eventNo = String.valueOf(noOfEvents);
                                    noofEvents = Integer.parseInt(eventNo);
                                    avgAtt = totalAtt / noofEvents;
                                    avgDef = totalDef / noofEvents;
                                    avgOver = totalOver / noofEvents;

                                    bestPlayer = new BestPlayer(playerName, avgAtt, avgDef, avgOver, noofEvents);
                                    bestPlayersA.add(bestPlayer);
                                    bestPlayersD.add(bestPlayer);
                                    totalAtt = 0;
                                    totalDef = 0;
                                    totalOver = 0;
                                    avgAtt = 0;
                                    avgDef = 0;
                                    avgOver = 0;
                                }
                                playerNo = String.valueOf(noOfPLayers);
                                noPlayers = Integer.parseInt(playerNo);
                                if (bestPlayersA.size() == noPlayers && bestPlayersD.size() == noPlayers) {
                                    getBestDefenders();
                                    getBestAttackers();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                mNoDataA.setVisibility(View.VISIBLE);
                mNoDataD.setVisibility(View.VISIBLE);
                mAttackersLV.setVisibility(View.INVISIBLE);
                mDefendersLV.setVisibility(View.INVISIBLE);
            }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getBestAttackers(){
        attackerAdapter = new HomeRatingAdapter(bestPlayersA, getActivity());
        mAttackersLV.setAdapter(attackerAdapter);
        Collections.sort(bestPlayersA, new Comparator<BestPlayer>() {
            @Override
            public int compare(BestPlayer bestPlayer, BestPlayer t1) {
                return Double.compare(t1.getAttackerRating(), bestPlayer.getAttackerRating());
            }
        });
        attackerAdapter.notifyDataSetChanged();
    }

    public void getBestDefenders(){
        defenderAdapter = new HomeRatingAdapter(bestPlayersD, getActivity());
        mDefendersLV.setAdapter(defenderAdapter);
        Collections.sort(bestPlayersD, new Comparator<BestPlayer>() {
            @Override
            public int compare(BestPlayer bestPlayer, BestPlayer t1) {
                return Double.compare(t1.getDefenderRating(), bestPlayer.getDefenderRating());
            }
        });
        defenderAdapter.notifyDataSetChanged();
    }
}

