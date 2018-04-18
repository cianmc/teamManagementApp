package com.example.cianm.testauth.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cianm.testauth.Entity.GlobalVariables;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;

public class ViewRatingPlayer extends AppCompatActivity {

    DatabaseReference mRatingRef, mUserRef, mAvgRating, mFixRef, mSavedPlayerRef;
    FirebaseAuth mAuth;
    FirebaseUser fbUser;

    GraphView mGraph;
    TextView mPoints, mGoals, mWides, mTackles, mTurnovers, mYellowCards, mRedCards, mBlackCards, tvBC;
    Button mHome;

    ArrayList<String> names;
    String currentPlayer, currentEvent, currentTeam, userUid, dateAlt, eventKey;
    int points, goals, wides, tackles, turnovers, yellowCards, redCards, blackCards, noOfPlayers;
    double attackerRating, defenderRating, overallRating, avgAttacker, avgDefender, avgOverall, totalAtt, totalDef, totalOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentPlayer = ((GlobalVariables) ViewRatingPlayer.this.getApplication()).getCurrentPlayer();
        currentTeam = ((GlobalVariables) ViewRatingPlayer.this.getApplication()).getCurrentTeam();
        currentEvent = ((GlobalVariables) ViewRatingPlayer.this.getApplication()).getCurrentEvent();
        setContentView(R.layout.activity_view_rating_player);

        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        dateAlt = currentEvent.replace("/", "");
        names = new ArrayList<>();

        mGraph = (GraphView) findViewById(R.id.graph);
        mPoints = (TextView) findViewById(R.id.pointsViewR);
        mGoals = (TextView) findViewById(R.id.goalsViewR);
        mWides = (TextView) findViewById(R.id.widesViewR);
        mTackles = (TextView) findViewById(R.id.tacklesViewR);
        mTurnovers = (TextView) findViewById(R.id.turnoversViewR);
        mYellowCards = (TextView) findViewById(R.id.yellowCardsViewR);
        mRedCards = (TextView) findViewById(R.id.redCardsViewR);
        mBlackCards = (TextView) findViewById(R.id.blackCardsViewR);
        tvBC = (TextView) findViewById(R.id.textView11);
        mHome = (Button) findViewById(R.id.ratingHomeBtn);

        mUserRef = FirebaseDatabase.getInstance().getReference("User");
        mRatingRef = FirebaseDatabase.getInstance().getReference("Ratings").child(currentTeam).child("Fixture");
        mAvgRating = FirebaseDatabase.getInstance().getReference("AvgRating").child(currentTeam);

        // getNames();
        mFixRef = FirebaseDatabase.getInstance().getReference("Fixture").child(currentTeam);
        mFixRef.orderByChild("date").equalTo(currentEvent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    eventKey = ds.getKey();
                    mSavedPlayerRef = mFixRef.child(eventKey).child("attenedee").child("saved");
                    mSavedPlayerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String name = ds.getValue(String.class);
                                names.add(name);
                            }
                            noOfPlayers = names.size();
                            mAvgRating.child(dateAlt).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                        double att = ds.child("attackerRating").getValue(Double.class);
                                        double def = ds.child("defenderRating").getValue(Double.class);
                                        double over = ds.child("overallRating").getValue(Double.class);
                                        totalAtt = totalAtt + att;
                                        totalDef = totalDef + def;
                                        totalOver = totalOver + over;
                                    }
                                    avgAttacker = totalAtt/noOfPlayers;
                                    avgDefender = totalDef/noOfPlayers;
                                    avgOverall = totalOver/noOfPlayers;
                                    loadRatings();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

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

        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (ViewRatingPlayer.this, PlayerHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    public void loadRatings(){
        if(currentTeam.contains("H")){
            mBlackCards.setVisibility(View.INVISIBLE);
            tvBC.setVisibility(View.INVISIBLE);
            //mRatingRef = FirebaseDatabase.getInstance().getReference("Ratings").child(currentTeam).child("Fixture");
            mRatingRef.child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        points = ds.child("points").getValue(Integer.class);
                        goals = ds.child("goals").getValue(Integer.class);
                        wides = ds.child("wides").getValue(Integer.class);
                        tackles = ds.child("tackles").getValue(Integer.class);
                        turnovers = ds.child("turnovers").getValue(Integer.class);
                        yellowCards = ds.child("yellowCards").getValue(Integer.class);
                        redCards = ds.child("redCards").getValue(Integer.class);
                        blackCards = ds.child("blackCards").getValue(Integer.class);

                        mPoints.setText(String.valueOf(points));
                        mGoals.setText(String.valueOf(goals));
                        mWides.setText(String.valueOf(wides));
                        mTackles.setText(String.valueOf(tackles));
                        mTurnovers.setText(String.valueOf(turnovers));
                        mYellowCards.setText(String.valueOf(yellowCards));
                        mRedCards.setText(String.valueOf(redCards));
                        mBlackCards.setText(String.valueOf(blackCards));

                        attackerRating = ds.child("attackerRating").getValue(Double.class);
                        defenderRating = ds.child("defenderRating").getValue(Double.class);
                        overallRating = ds.child("overallRating").getValue(Double.class);
                    }
                    BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]{
                            new DataPoint(0.5, defenderRating),
                            new DataPoint(1.5, attackerRating),
                            new DataPoint(2.5, overallRating),
                    });
                    BarGraphSeries<DataPoint> series1 = new BarGraphSeries<>(new DataPoint[]{
                            new DataPoint(0.5, avgDefender),
                            new DataPoint(1.5, avgAttacker),
                            new DataPoint(2.5, avgOverall)
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
                    series1.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                        @Override
                        public int get(DataPoint data) {
                            return Color.rgb(182,69,182);
                        }
                    });

                    series.setSpacing(10);
                    series1.setSpacing(10);
                    // draw values on top
                    series.setDrawValuesOnTop(true);
                    series.setValuesOnTopColor(Color.BLACK);
                    series.setTitle("Players rating");
                    series.setColor(Color.rgb(69,182,69));
                    series1.setTitle("Team Average");
                    series1.setColor(Color.rgb(182,69,182));
                    series1.setDrawValuesOnTop(true);
                    series1.setValuesOnTopColor(Color.BLACK);
                    mGraph.getLegendRenderer().setVisible(true);
                    mGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                    mGraph.getLegendRenderer().setBackgroundColor(Color.WHITE);
                    mGraph.getLegendRenderer().setMargin(20);
                    mGraph.addSeries(series);
                    mGraph.addSeries(series1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else {
            //mRatingRef = FirebaseDatabase.getInstance().getReference("Ratings").child(currentTeam).child("Fixture");
            mRatingRef.child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        points = ds.child("points").getValue(Integer.class);
                        goals = ds.child("goals").getValue(Integer.class);
                        wides = ds.child("wides").getValue(Integer.class);
                        tackles = ds.child("tackles").getValue(Integer.class);
                        turnovers = ds.child("turnovers").getValue(Integer.class);
                        yellowCards = ds.child("yellowCards").getValue(Integer.class);
                        redCards = ds.child("redCards").getValue(Integer.class);
                        blackCards = ds.child("blackCards").getValue(Integer.class);

                        mPoints.setText(String.valueOf(points));
                        mGoals.setText(String.valueOf(goals));
                        mWides.setText(String.valueOf(wides));
                        mTackles.setText(String.valueOf(tackles));
                        mTurnovers.setText(String.valueOf(turnovers));
                        mYellowCards.setText(String.valueOf(yellowCards));
                        mRedCards.setText(String.valueOf(redCards));
                        mBlackCards.setText(String.valueOf(blackCards));

                        attackerRating = ds.child("attackerRating").getValue(Double.class);
                        defenderRating = ds.child("defenderRating").getValue(Double.class);
                        overallRating = ds.child("overallRating").getValue(Double.class);
                    }
                    BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]{
                            new DataPoint(0.5, defenderRating),
                            new DataPoint(1.5, attackerRating),
                            new DataPoint(2.5, overallRating),
                    });
                    BarGraphSeries<DataPoint> series1 = new BarGraphSeries<>(new DataPoint[]{
                            new DataPoint(0.5, avgDefender),
                            new DataPoint(1.5, avgAttacker),
                            new DataPoint(2.5, avgOverall)
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
                    series1.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                        @Override
                        public int get(DataPoint data) {
                            return Color.rgb(182,69,182);
                        }
                    });

                    series.setSpacing(10);
                    series1.setSpacing(10);
                    // draw values on top
                    series.setDrawValuesOnTop(true);
                    series.setValuesOnTopColor(Color.BLACK);
                    series.setTitle("Players rating");
                    series.setColor(Color.rgb(69,182,69));
                    series1.setTitle("Team Average");
                    series1.setColor(Color.rgb(182,69,182));
                    series1.setDrawValuesOnTop(true);
                    series1.setValuesOnTopColor(Color.BLACK);
                    mGraph.getLegendRenderer().setVisible(true);
                    mGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                    mGraph.getLegendRenderer().setBackgroundColor(Color.WHITE);
                    mGraph.getLegendRenderer().setMargin(20);
                    mGraph.addSeries(series);
                    mGraph.addSeries(series1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}


