package com.example.cianm.testauth.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.Entity.User;
import com.example.cianm.testauth.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;

/**
 * Created by cianm on 14/03/2018.
 */

public class PlayerHomeFragment extends Fragment {

    DatabaseReference mRatingsRef, mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser fbUser;

    GraphView mGraph;
    TextView mPoints, mGoals, mWides, mTackles, mTurnovers, mYellowCards, mRedCards, mBlackCards, tvBC, mNoOfGames, mNoData, mPlayerName;

    ArrayList<String> savedDates;

    int points, goals, wides, tackles, turnovers, yellowCards, redCards, blackCards, noOfSavedDates;
    double attackerRating, defenderRating, overallRating, avgAttacker, avgDefender, avgOverall;
    String currentTeam, name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_player_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        currentTeam = ((GlobalVariables) getActivity().getApplicationContext()).getCurrentTeam();
        getActivity().setTitle("Home Page: " + currentTeam);

        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        savedDates = new ArrayList<>();

        mGraph = (GraphView) getView().findViewById(R.id.graphPlayerHome);
        mPoints = (TextView) getView().findViewById(R.id.pointsPlayerHome);
        mGoals = (TextView) getView().findViewById(R.id.goalsPlayerHome);
        mWides = (TextView) getView().findViewById(R.id.widesPlayerHome);
        mTackles = (TextView) getView().findViewById(R.id.tacklesPlayerHome);
        mTurnovers = (TextView) getView().findViewById(R.id.turnoversPlayerHome);
        mYellowCards = (TextView) getView().findViewById(R.id.yellowCardsPlayerHome);
        mRedCards = (TextView) getView().findViewById(R.id.redCardsPlayerHome);
        mBlackCards = (TextView) getView().findViewById(R.id.blackCardsPlayerHome);
        tvBC = (TextView) getView().findViewById(R.id.textView11);
        mNoOfGames = (TextView) getView().findViewById(R.id.numOfGames);
        mNoData = (TextView) getView().findViewById(R.id.noDataHome);
        mPlayerName = (TextView) getView().findViewById(R.id.playerView);

        mUserRef = FirebaseDatabase.getInstance().getReference("User").child(fbUser.getUid());
        mRatingsRef = FirebaseDatabase.getInstance().getReference("Ratings").child(currentTeam).child("Fixture");

        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name = user.getName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserRef.child("savedDates").child(currentTeam).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String date = ds.getValue(String.class);
                        savedDates.add(date);
                    }
                    noOfSavedDates = savedDates.size();
                    mNoOfGames.setText(String.valueOf(noOfSavedDates));
                    getStats();
                } else {
                    mNoOfGames.setText("0");
                    mNoData.setVisibility(View.VISIBLE);
                    mGraph.setVisibility(View.INVISIBLE);
                    mPoints.setText("-");
                    mGoals.setText("-");
                    mWides.setText("-");
                    mTackles.setText("-");
                    mTurnovers.setText("-");
                    mYellowCards.setText("-");
                    mRedCards.setText("-");
                    mBlackCards.setText("-");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getStats(){
        if(currentTeam.contains("H")) {
            mRatingsRef.child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        int point = ds.child("points").getValue(Integer.class);
                        int goal = ds.child("goals").getValue(Integer.class);
                        int wide = ds.child("wides").getValue(Integer.class);
                        int tackle = ds.child("tackles").getValue(Integer.class);
                        int turnover = ds.child("turnovers").getValue(Integer.class);
                        int yellowCard = ds.child("yellowCards").getValue(Integer.class);
                        int redCard = ds.child("redCards").getValue(Integer.class);
                        //int blackCard = ds.child("blackCards").getValue(Integer.class);
                        double defender = ds.child("defenderRating").getValue(Double.class);
                        double attacker = ds.child("attackerRating").getValue(Double.class);
                        double overall = ds.child("overallRating").getValue(Double.class);

                        points = points + point;
                        goals = goals + goal;
                        wides = wides + wide;
                        tackles = tackles + tackle;
                        turnovers = turnovers + turnover;
                        yellowCards = yellowCards + yellowCard;
                        redCards = redCards + redCard;
                        //blackCards = blackCards + blackCard;
                        defenderRating = defenderRating + defender;
                        attackerRating = attackerRating + attacker;
                        overallRating = overallRating + overall;
                    }
                    // get averages for attacker, defender and overall rating
                    avgAttacker = attackerRating / noOfSavedDates;
                    avgDefender = defenderRating / noOfSavedDates;
                    avgOverall = overallRating / noOfSavedDates;

                    // set TextViews
                    mPoints.setText(String.valueOf(points));
                    mGoals.setText(String.valueOf(goals));
                    mWides.setText(String.valueOf(wides));
                    mTackles.setText(String.valueOf(tackles));
                    mTurnovers.setText(String.valueOf(turnovers));
                    mYellowCards.setText(String.valueOf(yellowCards));
                    mRedCards.setText(String.valueOf(redCards));
                    //mBlackCards.setText(String.valueOf(blackCards));
                    mPlayerName.setText(name);

                    // Graphs
                    BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]{
                            new DataPoint(0.5, defenderRating),
                            new DataPoint(1.5, attackerRating),
                            new DataPoint(2.5, overallRating),
                    });
                    mGraph.getGridLabelRenderer().setVerticalAxisTitle("Score");
                    mGraph.getGridLabelRenderer().setPadding(10);
                    mGraph.getViewport().setMinY(0);
                    mGraph.getViewport().setMaxY(100);
                    mGraph.getViewport().setMinX(0);
                    mGraph.getViewport().setMaxX(3);
                    mGraph.getViewport().setYAxisBoundsManual(true);
                    mGraph.getViewport().setXAxisBoundsManual(true);
                    mGraph.getViewport().setScrollable(true);
                    series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                        @Override
                        public int get(DataPoint data) {
                            return Color.rgb(69, 182, 69);
                        }
                    });
                    series.setSpacing(10);

                    // draw values on top
                    series.setDrawValuesOnTop(true);
                    series.setValuesOnTopColor(Color.BLACK);
                    series.setTitle("Players average");
                    series.setColor(Color.rgb(69, 182, 69));
                    mGraph.getLegendRenderer().setVisible(true);
                    mGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                    mGraph.getLegendRenderer().setBackgroundColor(Color.WHITE);
                    mGraph.getLegendRenderer().setMargin(20);
                    mGraph.addSeries(series);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            mRatingsRef.child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        int point = ds.child("points").getValue(Integer.class);
                        int goal = ds.child("goals").getValue(Integer.class);
                        int wide = ds.child("wides").getValue(Integer.class);
                        int tackle = ds.child("tackles").getValue(Integer.class);
                        int turnover = ds.child("turnovers").getValue(Integer.class);
                        int yellowCard = ds.child("yellowCards").getValue(Integer.class);
                        int redCard = ds.child("redCards").getValue(Integer.class);
                        int blackCard = ds.child("blackCards").getValue(Integer.class);
                        double defender = ds.child("defenderRating").getValue(Double.class);
                        double attacker = ds.child("attackerRating").getValue(Double.class);
                        double overall = ds.child("overallRating").getValue(Double.class);

                        points = points + point;
                        goals = goals + goal;
                        wides = wides + wide;
                        tackles = tackles + tackle;
                        turnovers = turnovers + turnover;
                        yellowCards = yellowCards + yellowCard;
                        redCards = redCards + redCard;
                        blackCards = blackCards + blackCard;
                        defenderRating = defenderRating + defender;
                        attackerRating = attackerRating + attacker;
                        overallRating = overallRating + overall;
                    }
                    // get averages for attacker, defender and overall rating
                    avgAttacker = attackerRating / noOfSavedDates;
                    avgDefender = defenderRating / noOfSavedDates;
                    avgOverall = overallRating / noOfSavedDates;

                    // set TextViews
                    mPoints.setText(String.valueOf(points));
                    mGoals.setText(String.valueOf(goals));
                    mWides.setText(String.valueOf(wides));
                    mTackles.setText(String.valueOf(tackles));
                    mTurnovers.setText(String.valueOf(turnovers));
                    mYellowCards.setText(String.valueOf(yellowCards));
                    mRedCards.setText(String.valueOf(redCards));
                    mBlackCards.setText(String.valueOf(blackCards));
                    mPlayerName.setText(name);

                    // Graphs
                    BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]{
                            new DataPoint(0.5, avgAttacker),
                            new DataPoint(1.5, avgDefender),
                            new DataPoint(2.5, avgOverall),
                    });
                    mGraph.getGridLabelRenderer().setVerticalAxisTitle("Score");
                    mGraph.getGridLabelRenderer().setPadding(10);
                    mGraph.getViewport().setMinY(0);
                    mGraph.getViewport().setMaxY(100);
                    mGraph.getViewport().setMinX(0);
                    mGraph.getViewport().setMaxX(3);
                    mGraph.getViewport().setYAxisBoundsManual(true);
                    mGraph.getViewport().setXAxisBoundsManual(true);
                    mGraph.getViewport().setScrollable(true);
                    series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                        @Override
                        public int get(DataPoint data) {
                            return Color.rgb(69, 182, 69);
                        }
                    });
                    series.setSpacing(10);

                    // draw values on top
                    series.setDrawValuesOnTop(true);
                    series.setValuesOnTopColor(Color.BLACK);
                    series.setTitle("Players average");
                    series.setColor(Color.rgb(69, 182, 69));
                    mGraph.getLegendRenderer().setVisible(true);
                    mGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                    mGraph.getLegendRenderer().setBackgroundColor(Color.WHITE);
                    mGraph.getLegendRenderer().setMargin(20);
                    mGraph.addSeries(series);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
