package com.example.cianm.testauth.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cianm.testauth.Entity.FixtureRatingFootball;
import com.example.cianm.testauth.Entity.FixtureRatingHurling;
import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.Entity.Team;
import com.example.cianm.testauth.Entity.TempRatingFootball;
import com.example.cianm.testauth.Entity.TempRatingHurling;
import com.example.cianm.testauth.ManagerHome;
import com.example.cianm.testauth.NothingSelectedSpinnerAdapter;
import com.example.cianm.testauth.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class FixtureRating extends AppCompatActivity {

    DatabaseReference mTeamReference, mDatabase, mAttendeeReferenceNS, mAttendeeReferenceS, mTempRatingF, mTempRatingH, mSaveRef, mUserRef, mSavedDates, mRatingAvg, mDatesToSave;
    FirebaseAuth mAuth;
    FirebaseUser fbUser;

    Button mPointsMinus, mPointsPlus, mGoalsMinus, mGoalsPlus, mWidesMinus, mWidesPlus, mTacklesMinus, mTacklesPlus, mTurnoversMinus, mTurnoversPlus, mYellowCardsMinus, mYellowCardsPlus, mRedCardsMinus, mRedCardsPlus, mBlackCardsMinus, mBlackCardsPlus, mSaveCurrentPlayer, mAwayPointPlus, mAwayPointMinus, mAwayGoalsPlus, mAwayGoalsMinus;
    TextView mPointsValue, mGoalsValue, mWidesValue, mTacklesValue, mTurnoversValue, mYellowCardsValue, mRedCardsValue, mBlackCardsValue, mHomePoints, mHomeGoals, mAwayGoals, mAwayPoints;
    LinearLayout mBlackCardLinearLayout;
    Spinner mChooseAttendeeSpinner;
    ScrollView mRatingsView;

    int points, goals, wides, tackles, turnovers, yellowCards, redCards, blackCards, savePoints, saveGoals, saveWides, saveTackles, saveTurnovers, saveYellowCards, saveRedCards, saveBlackCards, totalPointH, totalGoalsH, totalPointsA, totalGoalsA;
    Double attackerRating, defenderRating, overallRating;
    String currentTeam, currentEvent, eventKey, position, teamType, savedName, userUID, dateAlt;
    int playerName, positionRef, uidName, gHome, pHome;

    ArrayList<String> attendees, uids;
    Team team;
    TempRatingFootball tempRateF;
    TempRatingHurling tempRateH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixture_rating);
        currentEvent = ((GlobalVariables) FixtureRating.this.getApplication()).getCurrentEvent();
        currentTeam = ((GlobalVariables) FixtureRating.this.getApplication()).getCurrentTeam();
        setTitle("Record Stats");

        attendees = new ArrayList<>();
        uids = new ArrayList<>();

        dateAlt = currentEvent.replace("/", "");

        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();

        mBlackCardLinearLayout = (LinearLayout) findViewById(R.id.blackCardsLinearLayout);
        mChooseAttendeeSpinner = (Spinner) findViewById(R.id.chooseAttendeeSpinner);
        mRatingsView = (ScrollView) findViewById(R.id.fixRatingScrollView);
        mSaveCurrentPlayer = (Button) findViewById(R.id.saveCurrentPlayerBtn);

        // Minus Buttons
        mPointsMinus = (Button) findViewById(R.id.pointsMinusButton);
        mGoalsMinus = (Button) findViewById(R.id.goalsMinusButton);
        mWidesMinus = (Button) findViewById(R.id.widesMinusButton);
        mTacklesMinus = (Button) findViewById(R.id.tacklesMinusButton);
        mTurnoversMinus = (Button) findViewById(R.id.turnoversMinusButton);
        mYellowCardsMinus = (Button) findViewById(R.id.yellowCardsMinusButton);
        mRedCardsMinus = (Button) findViewById(R.id.redCardsMinusButton);
        mBlackCardsMinus = (Button) findViewById(R.id.blackCardsMinusButton);
        mAwayGoalsMinus = (Button) findViewById(R.id.awayGoalsMinus);
        mAwayPointMinus = (Button) findViewById(R.id.awayPointsMinus);

        // Plus Buttons
        mPointsPlus = (Button) findViewById(R.id.pointsPlusButton);
        mGoalsPlus = (Button) findViewById(R.id.goalsPlusButton);
        mWidesPlus = (Button) findViewById(R.id.widesPlusButton);
        mTacklesPlus = (Button) findViewById(R.id.tacklesPlusButton);
        mTurnoversPlus = (Button) findViewById(R.id.turnoversPlusButton);
        mYellowCardsPlus = (Button) findViewById(R.id.yellowCardsPlusButton);
        mRedCardsPlus = (Button) findViewById(R.id.redCardsPlusButton);
        mBlackCardsPlus = (Button) findViewById(R.id.blackCardsPlusButton);
        mAwayPointPlus = (Button) findViewById(R.id.awayPointsPlus);
        mAwayGoalsPlus = (Button) findViewById(R.id.awayGoalsPlus);

        // Values Text Views
        mPointsValue = (TextView) findViewById(R.id.pointsValue);
        mGoalsValue = (TextView) findViewById(R.id.goalsValue);
        mWidesValue = (TextView) findViewById(R.id.widesValue);
        mTacklesValue = (TextView) findViewById(R.id.tacklesValue);
        mTurnoversValue = (TextView) findViewById(R.id.turnoversValue);
        mYellowCardsValue = (TextView) findViewById(R.id.yellowCardsValue);
        mRedCardsValue = (TextView) findViewById(R.id.redCardsValue);
        mBlackCardsValue = (TextView) findViewById(R.id.blackCardsValue);
        mHomeGoals = (TextView) findViewById(R.id.homeScoreGoals);
        mHomePoints = (TextView) findViewById(R.id.homeScorePoints);
        mAwayPoints = (TextView) findViewById(R.id.awayScorePoints);
        mAwayGoals = (TextView) findViewById(R.id.awayScoreGoals);

        // Make all minus buttons invisible so the uses cannot input a minus value
        mPointsMinus.setVisibility(INVISIBLE);
        mGoalsMinus.setVisibility(INVISIBLE);
        mWidesMinus.setVisibility(INVISIBLE);
        mTacklesMinus.setVisibility(INVISIBLE);
        mTurnoversMinus.setVisibility(INVISIBLE);
        mYellowCardsMinus.setVisibility(INVISIBLE);
        mRedCardsMinus.setVisibility(INVISIBLE);
        mBlackCardsMinus.setVisibility(INVISIBLE);
        mAwayPointMinus.setVisibility(INVISIBLE);
        mAwayGoalsMinus.setVisibility(INVISIBLE);

        mTempRatingF = FirebaseDatabase.getInstance().getReference("TempRatingF");
        mTempRatingH = FirebaseDatabase.getInstance().getReference("TempRatingH");
        mSaveRef = FirebaseDatabase.getInstance().getReference("Ratings");
        mUserRef = FirebaseDatabase.getInstance().getReference("User");
        mSavedDates = FirebaseDatabase.getInstance().getReference("SavedDates");
        mRatingAvg = FirebaseDatabase.getInstance().getReference("AvgRating");
        mDatesToSave = FirebaseDatabase.getInstance().getReference("DatesToSave");

        mTeamReference = FirebaseDatabase.getInstance().getReference("Team");
        mTeamReference.child(currentTeam).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teamType = dataSnapshot.child("type").getValue(String.class);
                if(teamType.equalsIgnoreCase("Hurling")){
                    mBlackCardLinearLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference("Fixture").child(currentTeam);
        mDatabase.orderByChild("date").equalTo(currentEvent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                        eventKey = child.getKey();
                        mAttendeeReferenceNS = mDatabase.child(eventKey).child("attenedee").child("notSaved");
                        mAttendeeReferenceNS.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String uid = ds.getKey();
                                    String attendeeName = ds.getValue(String.class);
                                    attendees.add(attendeeName);
                                    uids.add(uid);
                                }
                                if (attendees.isEmpty()) {
                                    Toast.makeText(FixtureRating.this, "All players saved", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(FixtureRating.this, ManagerHome.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                } else {
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(FixtureRating.this, android.R.layout.simple_spinner_dropdown_item, attendees);
                                    mChooseAttendeeSpinner.setAdapter(new NothingSelectedSpinnerAdapter(arrayAdapter, R.layout.contact_spinner_row_nothing_selected, FixtureRating.this));
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

        mPointsPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalPointH = Integer.parseInt(mHomePoints.getText().toString());
                points = Integer.parseInt(mPointsValue.getText().toString());
                mPointsMinus.setVisibility(VISIBLE);
                points++;
                totalPointH++;
                mHomePoints.setText(Integer.toString(totalPointH));
                mPointsValue.setText(Integer.toString(points));
                if (teamType.equalsIgnoreCase("Football")) {
                    mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("points").setValue(points);
                } else
                    mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("points").setValue(points);
            }
        });

        mPointsMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalPointH = Integer.parseInt(mHomePoints.getText().toString());
                points = Integer.parseInt(mPointsValue.getText().toString());
                points--;
                totalPointH--;
                mHomePoints.setText(Integer.toString(totalPointH));
                if(points == 0) {
                    mPointsMinus.setVisibility(INVISIBLE);
                }
                mPointsValue.setText(Integer.toString(points));
                if (teamType.equalsIgnoreCase("Football")) {
                    mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("points").setValue(points);
                } else {
                    mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("points").setValue(points);
                }
            }
        });


        mGoalsPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalGoalsH = Integer.parseInt(mHomeGoals.getText().toString());
                goals = Integer.parseInt(mGoalsValue.getText().toString());
                mGoalsMinus.setVisibility(VISIBLE);
                goals++;
                totalGoalsH++;
                mHomeGoals.setText(Integer.toString(totalGoalsH));
                mGoalsValue.setText(Integer.toString(goals));
                if (teamType.equalsIgnoreCase("Football")) {
                    mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("goals").setValue(goals);
                } else {
                    mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("goals").setValue(goals);
                }
            }
        });

        mGoalsMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalGoalsH = Integer.parseInt(mHomeGoals.getText().toString());
                goals = Integer.parseInt(mGoalsValue.getText().toString());
                goals--;
                totalGoalsH--;
                mHomeGoals.setText(Integer.toString(totalGoalsH));
                if(goals == 0) {
                    mGoalsMinus.setVisibility(INVISIBLE);
                }
                mGoalsValue.setText(Integer.toString(goals));
                if (teamType.equalsIgnoreCase("Football")) {
                    mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("goals").setValue(goals);
                } else {
                    mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("goals").setValue(goals);
                }
            }
        });

        mWidesPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wides = Integer.parseInt(mWidesValue.getText().toString());
                mWidesMinus.setVisibility(VISIBLE);
                wides++;
                mWidesValue.setText(Integer.toString(wides));
                if (teamType.equalsIgnoreCase("Football")) {
                    mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("wides").setValue(wides);
                } else {
                    mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("wides").setValue(wides);
                }
            }
        });

        mWidesMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wides = Integer.parseInt(mWidesValue.getText().toString());
                wides--;
                if(wides == 0) {
                    mWidesMinus.setVisibility(INVISIBLE);
                }
                mWidesValue.setText(Integer.toString(wides));
                if (teamType.equalsIgnoreCase("Football")) {
                    mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("wides").setValue(wides);
                } else {
                    mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("wides").setValue(wides);
                }
            }
        });

        mTacklesPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tackles = Integer.parseInt(mTacklesValue.getText().toString());
                mTacklesMinus.setVisibility(VISIBLE);
                tackles++;
                mTacklesValue.setText(Integer.toString(tackles));
                if (teamType.equalsIgnoreCase("Football")) {
                    mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("tackles").setValue(tackles);
                } else {
                    mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("tackles").setValue(tackles);
                }
            }
        });

        mTacklesMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tackles = Integer.parseInt(mTacklesValue.getText().toString());
                tackles--;
                if(tackles == 0) {
                    mTacklesMinus.setVisibility(INVISIBLE);
                }
                mTacklesValue.setText(Integer.toString(tackles));
                if (teamType.equalsIgnoreCase("Football")) {
                    mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("tackles").setValue(tackles);
                } else {
                    mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("tackles").setValue(tackles);
                }
            }
        });

        mTurnoversPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnovers = Integer.parseInt(mTurnoversValue.getText().toString());
                mTurnoversMinus.setVisibility(VISIBLE);
                turnovers++;
                mTurnoversValue.setText(Integer.toString(turnovers));
                if (teamType.equalsIgnoreCase("Football")) {
                    mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("turnovers").setValue(turnovers);
                } else {
                    mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("turnovers").setValue(turnovers);
                }
            }
        });

        mTurnoversMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnovers = Integer.parseInt(mTurnoversValue.getText().toString());
                turnovers--;
                if(turnovers == 0) {
                    mTurnoversMinus.setVisibility(INVISIBLE);
                }
                mTurnoversValue.setText(Integer.toString(turnovers));
                if (teamType.equalsIgnoreCase("Football")) {
                    mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("turnovers").setValue(turnovers);
                } else {
                    mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("turnovers").setValue(turnovers);
                }
            }
        });

        mYellowCardsPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yellowCards = Integer.parseInt(mYellowCardsValue.getText().toString());
                mYellowCardsMinus.setVisibility(VISIBLE);
                yellowCards++;
                if (yellowCards == 2){
                    playerSentOff2Yellows();
                    mRedCardsValue.setText("1");
                }
                mYellowCardsValue.setText(Integer.toString(yellowCards));
                if (teamType.equalsIgnoreCase("Football")) {
                    mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("yellowCards").setValue(yellowCards);
                } else {
                    mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("yellowCards").setValue(yellowCards);
                }
            }
        });

        mYellowCardsMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yellowCards = Integer.parseInt(mYellowCardsValue.getText().toString());
                points = Integer.parseInt(mPointsValue.getText().toString());
                goals = Integer.parseInt(mGoalsValue.getText().toString());
                wides = Integer.parseInt(mWidesValue.getText().toString());
                tackles = Integer.parseInt(mTacklesValue.getText().toString());
                turnovers = Integer.parseInt(mTurnoversValue.getText().toString());
                playerNotSentOff();
                yellowCards--;
                if(yellowCards == 0) {
                    mYellowCardsMinus.setVisibility(INVISIBLE);
                }
                if(blackCards == 1){
                    mBlackCardsValue.setText("0");
                    mBlackCardsMinus.setVisibility(INVISIBLE);
                }
                mYellowCardsValue.setText(Integer.toString(yellowCards));
                if (teamType.equalsIgnoreCase("Football")) {
                    mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("yellowCards").setValue(yellowCards);
                } else {
                    mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("yellowCards").setValue(yellowCards);
                }
            }
        });

        mRedCardsPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redCards = Integer.parseInt(mRedCardsValue.getText().toString());
                mRedCardsMinus.setVisibility(VISIBLE);
                redCards++;
                if (redCards == 1){
                    straightRed();
                }
                if(yellowCards == 1 && redCards == 1){
                    mYellowCardsValue.setText("0");
                    mYellowCardsMinus.setVisibility(INVISIBLE);
                    mYellowCardsPlus.setVisibility(INVISIBLE);
                }
                mRedCardsValue.setText(Integer.toString(redCards));
                if (teamType.equalsIgnoreCase("Football")) {
                    mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("redCards").setValue(redCards);
                } else {
                    mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("redCards").setValue(redCards);
                }
            }
        });

        mRedCardsMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redCards = Integer.parseInt(mRedCardsValue.getText().toString());
                points = Integer.parseInt(mPointsValue.getText().toString());
                goals = Integer.parseInt(mGoalsValue.getText().toString());
                wides = Integer.parseInt(mWidesValue.getText().toString());
                tackles = Integer.parseInt(mTacklesValue.getText().toString());
                turnovers = Integer.parseInt(mTurnoversValue.getText().toString());
                playerNotSentOff();
                redCards--;
                if(redCards == 0) {
                    mRedCardsMinus.setVisibility(INVISIBLE);
                }
                if (yellowCards == 2){
                    mYellowCardsMinus.setVisibility(VISIBLE);
                    mYellowCardsValue.setText("1");
                }
                mRedCardsValue.setText(Integer.toString(redCards));
                if (teamType.equalsIgnoreCase("Football")) {
                    mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("redCards").setValue(redCards);
                } else {
                    mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("redCards").setValue(redCards);
                }
            }
        });

        mBlackCardsPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blackCards = Integer.parseInt(mBlackCardsValue.getText().toString());
                mBlackCardsMinus.setVisibility(VISIBLE);
                blackCards++;
                if (blackCards == 1){
                    blackCard();
                }
                if (yellowCards == 1){
                    yellowAndBlackCard();
                    mRedCardsValue.setText("1");
                }
                mBlackCardsValue.setText(Integer.toString(blackCards));
                mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("blackCards").setValue(blackCards);
            }
        });

        mBlackCardsMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blackCards = Integer.parseInt(mBlackCardsValue.getText().toString());
                points = Integer.parseInt(mPointsValue.getText().toString());
                goals = Integer.parseInt(mGoalsValue.getText().toString());
                wides = Integer.parseInt(mWidesValue.getText().toString());
                tackles = Integer.parseInt(mTacklesValue.getText().toString());
                turnovers = Integer.parseInt(mTurnoversValue.getText().toString());
                playerNotSentOff();
                blackCards--;
                if(blackCards == 0) {
                    mBlackCardsMinus.setVisibility(INVISIBLE);
                }
                mBlackCardsValue.setText(Integer.toString(blackCards));
                mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).child("blackCards").setValue(blackCards);
            }
        });

        mAwayPointPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalPointsA = Integer.parseInt(mAwayPoints.getText().toString());
                mAwayPointMinus.setVisibility(VISIBLE);
                totalPointsA++;
                mAwayPoints.setText(Integer.toString(totalPointsA));
            }
        });

        mAwayPointMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalPointsA = Integer.parseInt(mAwayPoints.getText().toString());
                totalPointsA--;
                if(totalPointsA == 0){
                    mAwayPointMinus.setVisibility(INVISIBLE);
                }
                mAwayPoints.setText(Integer.toString(totalPointsA));
            }
        });

        mAwayGoalsPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalGoalsA = Integer.parseInt(mAwayGoals.getText().toString());
                mAwayGoalsMinus.setVisibility(VISIBLE);
                totalGoalsA++;
                mAwayGoals.setText(Integer.toString(totalGoalsA));
            }
        });

        mAwayGoalsMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalGoalsA = Integer.parseInt(mAwayGoals.getText().toString());
                totalGoalsA--;
                if(totalGoalsA == 0){
                    mAwayGoalsMinus.setVisibility(INVISIBLE);
                }
                mAwayGoals.setText(Integer.toString(totalGoalsA));
            }
        });

        saveCheck();

        mSaveCurrentPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTeamReference = FirebaseDatabase.getInstance().getReference("Team");
                mTeamReference.child(currentTeam).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        team = dataSnapshot.getValue(Team.class);
                        teamType = team.getType();
                        if(teamType.equalsIgnoreCase("Hurling")){
                            finalSaveHurling();
                        } else if (teamType.equalsIgnoreCase("Football")){
                            finalSaveFootball();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                ;
            }
        });
    }

    public void saveTempRatingFootball(){
        loadScoreBoardF();
        mChooseAttendeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // final String position;
                position = Integer.toString(i);
                mTempRatingF = FirebaseDatabase.getInstance().getReference("TempRatingF");
                mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            TempRatingFootball tempRat = dataSnapshot.getValue(TempRatingFootball.class);
                            mPointsValue.setText(String.valueOf(tempRat.getPoints()));
                            mGoalsValue.setText(String.valueOf(tempRat.getGoals()));
                            mWidesValue.setText(String.valueOf(tempRat.getWides()));
                            mTacklesValue.setText(String.valueOf(tempRat.getTackles()));
                            mTurnoversValue.setText(String.valueOf(tempRat.getTurnovers()));
                            mYellowCardsValue.setText(String.valueOf(tempRat.getYellowCards()));
                            mRedCardsValue.setText(String.valueOf(tempRat.getRedCards()));
                            mBlackCardsValue.setText(String.valueOf(tempRat.getBlackCards()));

                            points = Integer.parseInt(mPointsValue.getText().toString());
                            goals = Integer.parseInt(mGoalsValue.getText().toString());
                            wides = Integer.parseInt(mWidesValue.getText().toString());
                            tackles = Integer.parseInt(mTacklesValue.getText().toString());
                            turnovers = Integer.parseInt(mTurnoversValue.getText().toString());
                            yellowCards = Integer.parseInt(mYellowCardsValue.getText().toString());
                            redCards = Integer.parseInt(mRedCardsValue.getText().toString());
                            blackCards = Integer.parseInt(mBlackCardsValue.getText().toString());

                            loadButtonsF();

                            tempRateF = new TempRatingFootball(points, goals, wides, tackles, turnovers, yellowCards, redCards, blackCards);
                            mTempRatingF = FirebaseDatabase.getInstance().getReference("TempRatingF");
                            mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).setValue(tempRateF);

                        } else {
                            loadFootball();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void saveTempRatingHurling(){
        loadScoreBoardH();
        mChooseAttendeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // final String position;
                position = Integer.toString(i);
                mTempRatingH = FirebaseDatabase.getInstance().getReference("TempRatingH");
                mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            TempRatingFootball tempRat = dataSnapshot.getValue(TempRatingFootball.class);
                            mPointsValue.setText(String.valueOf(tempRat.getPoints()));
                            mGoalsValue.setText(String.valueOf(tempRat.getGoals()));
                            mWidesValue.setText(String.valueOf(tempRat.getWides()));
                            mTacklesValue.setText(String.valueOf(tempRat.getTackles()));
                            mTurnoversValue.setText(String.valueOf(tempRat.getTurnovers()));
                            mYellowCardsValue.setText(String.valueOf(tempRat.getYellowCards()));
                            mRedCardsValue.setText(String.valueOf(tempRat.getRedCards()));

                            points = Integer.parseInt(mPointsValue.getText().toString());
                            goals = Integer.parseInt(mGoalsValue.getText().toString());
                            wides = Integer.parseInt(mWidesValue.getText().toString());
                            tackles = Integer.parseInt(mTacklesValue.getText().toString());
                            turnovers = Integer.parseInt(mTurnoversValue.getText().toString());
                            yellowCards = Integer.parseInt(mYellowCardsValue.getText().toString());
                            redCards = Integer.parseInt(mRedCardsValue.getText().toString());

                            loadButtonsH();

                            tempRateH = new TempRatingHurling(points, goals, wides, tackles, turnovers, yellowCards, redCards);
                            mTempRatingH = FirebaseDatabase.getInstance().getReference("TempRatingH");
                            mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(position).setValue(tempRateH);

                        } else {
                            loadHurling();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void saveCheck(){
        mTeamReference.child(currentTeam).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                team = dataSnapshot.getValue(Team.class);
                teamType = team.getType();
                if (teamType.equalsIgnoreCase("Hurling")){
                    saveTempRatingHurling();
                    loadHurling();
                } else if (teamType.equalsIgnoreCase("Football")){
                    saveTempRatingFootball();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadFootball(){
        int pos = 0;
        String id = mDatesToSave.push().getKey();
        mDatesToSave.child(currentTeam).child(id).child("date").setValue(currentEvent);
        for(int i=0; i<=attendees.size(); i++) {
            newTempRatingButtonsF();
            points = Integer.parseInt(mPointsValue.getText().toString());
            goals = Integer.parseInt(mGoalsValue.getText().toString());
            wides = Integer.parseInt(mWidesValue.getText().toString());
            tackles = Integer.parseInt(mTacklesValue.getText().toString());
            turnovers = Integer.parseInt(mTurnoversValue.getText().toString());
            yellowCards = Integer.parseInt(mYellowCardsValue.getText().toString());
            redCards = Integer.parseInt(mRedCardsValue.getText().toString());
            blackCards = Integer.parseInt(mBlackCardsValue.getText().toString());

            tempRateF = new TempRatingFootball(points, goals, wides, tackles, turnovers, yellowCards, redCards, blackCards);
            mTempRatingF = FirebaseDatabase.getInstance().getReference("TempRatingF");
            mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(Integer.toString(pos)).setValue(tempRateF);
            pos++;
        }
    }

    public void loadHurling(){
        int pos = 0;
        String id = mDatesToSave.push().getKey();
        mDatesToSave.child(currentTeam).child(id).child("date").setValue(currentEvent);
        for(int i=0; i<=attendees.size(); i++) {
            newTempRatingButtonsH();
            points = Integer.parseInt(mPointsValue.getText().toString());
            goals = Integer.parseInt(mGoalsValue.getText().toString());
            wides = Integer.parseInt(mWidesValue.getText().toString());
            tackles = Integer.parseInt(mTacklesValue.getText().toString());
            turnovers = Integer.parseInt(mTurnoversValue.getText().toString());
            yellowCards = Integer.parseInt(mYellowCardsValue.getText().toString());
            redCards = Integer.parseInt(mRedCardsValue.getText().toString());

            tempRateH = new TempRatingHurling(points, goals, wides, tackles, turnovers, yellowCards, redCards);
            mTempRatingH = FirebaseDatabase.getInstance().getReference("TempRatingH");
            mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(Integer.toString(pos)).setValue(tempRateH);
            pos++;
        }
    }

    public void newTempRatingButtonsF(){
        mPointsValue.setText("0");
        mGoalsValue.setText("0");
        mWidesValue.setText("0");
        mTacklesValue.setText("0");
        mTurnoversValue.setText("0");
        mYellowCardsValue.setText("0");
        mRedCardsValue.setText("0");
        mBlackCardsValue.setText("0");
        mPointsMinus.setVisibility(INVISIBLE);
        mGoalsMinus.setVisibility(INVISIBLE);
        mWidesMinus.setVisibility(INVISIBLE);
        mTacklesMinus.setVisibility(INVISIBLE);
        mTurnoversMinus.setVisibility(INVISIBLE);
        mYellowCardsMinus.setVisibility(INVISIBLE);
        mRedCardsMinus.setVisibility(INVISIBLE);
        mBlackCardsMinus.setVisibility(INVISIBLE);
    }

    public void loadButtonsF(){
        if (points == 0){
            mPointsMinus.setVisibility(INVISIBLE);
        } else mPointsMinus.setVisibility(VISIBLE);
        if (goals == 0){
            mGoalsMinus.setVisibility(INVISIBLE);
        } else mGoalsMinus.setVisibility(VISIBLE);
        if (wides == 0){
            mWidesMinus.setVisibility(INVISIBLE);
        } else mWidesMinus.setVisibility(VISIBLE);
        if (tackles == 0){
            mTacklesMinus.setVisibility(INVISIBLE);
        } else mTacklesMinus.setVisibility(VISIBLE);
        if (turnovers == 0){
            mTurnoversMinus.setVisibility(INVISIBLE);
        } else mTurnoversMinus.setVisibility(VISIBLE);
        if (yellowCards == 0){
            mYellowCardsMinus.setVisibility(INVISIBLE);
        } else mYellowCardsMinus.setVisibility(VISIBLE);
        if (redCards == 0){
            mRedCardsMinus.setVisibility(INVISIBLE);
        } else mRedCardsMinus.setVisibility(VISIBLE);
        if (blackCards == 0){
            mBlackCardsMinus.setVisibility(INVISIBLE);
        } else mBlackCardsMinus.setVisibility(VISIBLE);
        if (yellowCards == 2){
            playerSentOff2Yellows();
            mRedCardsValue.setText("1");
        }
        if (redCards == 1){
            straightRed();
        }
        if (blackCards == 1){
            blackCard();
        }
        if (yellowCards == 1 && blackCards == 1){
            yellowAndBlackCard();
            mRedCardsValue.setText("1");
        }
    }

    public void newTempRatingButtonsH(){
        mPointsValue.setText("0");
        mGoalsValue.setText("0");
        mWidesValue.setText("0");
        mTacklesValue.setText("0");
        mTurnoversValue.setText("0");
        mYellowCardsValue.setText("0");
        mRedCardsValue.setText("0");
        mPointsMinus.setVisibility(INVISIBLE);
        mGoalsMinus.setVisibility(INVISIBLE);
        mWidesMinus.setVisibility(INVISIBLE);
        mTacklesMinus.setVisibility(INVISIBLE);
        mTurnoversMinus.setVisibility(INVISIBLE);
        mYellowCardsMinus.setVisibility(INVISIBLE);
        mRedCardsMinus.setVisibility(INVISIBLE);
    }

    public void loadButtonsH(){
        if (points == 0){
            mPointsMinus.setVisibility(INVISIBLE);
        } else mPointsMinus.setVisibility(VISIBLE);
        if (goals == 0){
            mGoalsMinus.setVisibility(INVISIBLE);
        } else mGoalsMinus.setVisibility(VISIBLE);
        if (wides == 0){
            mWidesMinus.setVisibility(INVISIBLE);
        } else mWidesMinus.setVisibility(VISIBLE);
        if (tackles == 0){
            mTacklesMinus.setVisibility(INVISIBLE);
        } else mTacklesMinus.setVisibility(VISIBLE);
        if (turnovers == 0){
            mTurnoversMinus.setVisibility(INVISIBLE);
        } else mTurnoversMinus.setVisibility(VISIBLE);
        if (yellowCards == 0){
            mYellowCardsMinus.setVisibility(INVISIBLE);
        } else mYellowCardsMinus.setVisibility(VISIBLE);
        if (redCards == 0){
            mRedCardsMinus.setVisibility(INVISIBLE);
        } else mRedCardsMinus.setVisibility(VISIBLE);
        if (yellowCards == 2){
            playerSentOff2Yellows();
            mRedCardsValue.setText("1");
        }
        if (redCards == 1){
            straightRed();
        }
    }

    public void playerNotSentOff(){
        mPointsPlus.setVisibility(VISIBLE);
        mGoalsPlus.setVisibility(VISIBLE);
        mWidesPlus.setVisibility(VISIBLE);
        mTacklesPlus.setVisibility(VISIBLE);
        mTurnoversPlus.setVisibility(VISIBLE);
        mYellowCardsPlus.setVisibility(VISIBLE);
        mRedCardsPlus.setVisibility(VISIBLE);
        mBlackCardsPlus.setVisibility(VISIBLE);
        if (points == 0){
            mPointsMinus.setVisibility(INVISIBLE);
        } else mPointsMinus.setVisibility(VISIBLE);
        if (goals == 0){
            mGoalsMinus.setVisibility(INVISIBLE);
        } else mGoalsMinus.setVisibility(VISIBLE);
        if (wides == 0){
            mWidesMinus.setVisibility(INVISIBLE);
        } else mWidesMinus.setVisibility(VISIBLE);
        if (tackles == 0){
            mTacklesMinus.setVisibility(INVISIBLE);
        } else mTacklesMinus.setVisibility(VISIBLE);
        if (turnovers == 0){
            mTurnoversMinus.setVisibility(INVISIBLE);
        } else mTurnoversMinus.setVisibility(VISIBLE);
        mRedCardsValue.setText("0");
    }

    public void playerSentOff2Yellows(){
        mPointsPlus.setVisibility(INVISIBLE);
        mPointsMinus.setVisibility(INVISIBLE);
        mGoalsPlus.setVisibility(INVISIBLE);
        mGoalsMinus.setVisibility(INVISIBLE);
        mWidesPlus.setVisibility(INVISIBLE);
        mWidesMinus.setVisibility(INVISIBLE);
        mTacklesPlus.setVisibility(INVISIBLE);
        mTacklesMinus.setVisibility(INVISIBLE);
        mTurnoversPlus.setVisibility(INVISIBLE);
        mTurnoversMinus.setVisibility(INVISIBLE);
        mYellowCardsPlus.setVisibility(INVISIBLE);
        mYellowCardsMinus.setVisibility(VISIBLE);
        mRedCardsPlus.setVisibility(INVISIBLE);
        mRedCardsMinus.setVisibility(INVISIBLE);
        mBlackCardsPlus.setVisibility(INVISIBLE);
        mBlackCardsMinus.setVisibility(INVISIBLE);
    }

    public void straightRed(){
        mPointsPlus.setVisibility(INVISIBLE);
        mPointsMinus.setVisibility(INVISIBLE);
        mGoalsPlus.setVisibility(INVISIBLE);
        mGoalsMinus.setVisibility(INVISIBLE);
        mWidesPlus.setVisibility(INVISIBLE);
        mWidesMinus.setVisibility(INVISIBLE);
        mTacklesPlus.setVisibility(INVISIBLE);
        mTacklesMinus.setVisibility(INVISIBLE);
        mTurnoversPlus.setVisibility(INVISIBLE);
        mTurnoversMinus.setVisibility(INVISIBLE);
        mYellowCardsPlus.setVisibility(INVISIBLE);
        mYellowCardsMinus.setVisibility(INVISIBLE);
        mRedCardsPlus.setVisibility(INVISIBLE);
        mRedCardsMinus.setVisibility(VISIBLE);
        mBlackCardsPlus.setVisibility(INVISIBLE);
        mBlackCardsMinus.setVisibility(INVISIBLE);
    }

    public void blackCard(){
        mPointsPlus.setVisibility(INVISIBLE);
        mPointsMinus.setVisibility(INVISIBLE);
        mGoalsPlus.setVisibility(INVISIBLE);
        mGoalsMinus.setVisibility(INVISIBLE);
        mWidesPlus.setVisibility(INVISIBLE);
        mWidesMinus.setVisibility(INVISIBLE);
        mTacklesPlus.setVisibility(INVISIBLE);
        mTacklesMinus.setVisibility(INVISIBLE);
        mTurnoversPlus.setVisibility(INVISIBLE);
        mTurnoversMinus.setVisibility(INVISIBLE);
        mYellowCardsPlus.setVisibility(INVISIBLE);
        mYellowCardsMinus.setVisibility(INVISIBLE);
        mRedCardsPlus.setVisibility(INVISIBLE);
        mRedCardsMinus.setVisibility(INVISIBLE);
        mBlackCardsPlus.setVisibility(INVISIBLE);
        mBlackCardsMinus.setVisibility(VISIBLE);
    }

    public void yellowAndBlackCard(){
        mPointsPlus.setVisibility(INVISIBLE);
        mPointsMinus.setVisibility(INVISIBLE);
        mGoalsPlus.setVisibility(INVISIBLE);
        mGoalsMinus.setVisibility(INVISIBLE);
        mWidesPlus.setVisibility(INVISIBLE);
        mWidesMinus.setVisibility(INVISIBLE);
        mTacklesPlus.setVisibility(INVISIBLE);
        mTacklesMinus.setVisibility(INVISIBLE);
        mTurnoversPlus.setVisibility(INVISIBLE);
        mTurnoversMinus.setVisibility(INVISIBLE);
        mYellowCardsPlus.setVisibility(INVISIBLE);
        mYellowCardsMinus.setVisibility(VISIBLE);
        mRedCardsPlus.setVisibility(INVISIBLE);
        mRedCardsMinus.setVisibility(INVISIBLE);
        mBlackCardsPlus.setVisibility(INVISIBLE);
        mBlackCardsMinus.setVisibility(VISIBLE);
    }

    public void calAttackerRating(){

        attackerRating = (double)((savePoints*10) + (saveGoals*30) - (saveWides*15));
        if (attackerRating >= 100.00){
            attackerRating = 100.00;
        }
        if (attackerRating <=0.00){
            attackerRating = 0.00;
        }
    }

    public void calDefenderRating(){

        defenderRating = (double)((saveTackles*25) + (saveTurnovers*15));
        if (defenderRating >= 100.00){
            defenderRating = 100.00;
        }
        if (defenderRating <= 0.00){
            defenderRating = 0.00;
        }
    }

    public void calTotalRating(){

        double cards = 0.00;
        if (saveYellowCards == 1){ cards = 5; }
        if (saveBlackCards == 1){ cards = 10; }
        if (saveYellowCards == 2 || saveBlackCards == 1 && saveYellowCards == 1){ cards = 15; }
        if (saveRedCards == 1){ cards = 25; }

        overallRating = (defenderRating/2) + (attackerRating/2) - cards;
        if (overallRating <= 0.00){
            overallRating = 0.00;
        }
    }

    public void loadScoreBoardF(){
        mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    int p = ds.child("points").getValue(Integer.class);
                    int g = ds.child("goals").getValue(Integer.class);
                    gHome = gHome + g;
                    pHome = pHome + p;
                }
                mHomePoints.setText(Integer.toString(pHome));
                mHomeGoals.setText(Integer.toString(gHome));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadScoreBoardH(){
        mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    int p = ds.child("points").getValue(Integer.class);
                    int g = ds.child("goals").getValue(Integer.class);
                    gHome = gHome + g;
                    pHome = pHome + p;
                }
                mHomePoints.setText(Integer.toString(pHome));
                mHomeGoals.setText(Integer.toString(gHome));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void finalSaveHurling(){

        playerName = 0;
        uidName = 0;
        positionRef = 1;
        dateAlt = currentEvent.replace("/", "");
        String id = mSavedDates.push().getKey();
        mSavedDates.child(currentTeam).child(id).setValue(currentEvent);
        mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child("0").removeValue();
        mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    savePoints = ds.child("points").getValue(Integer.class);
                    saveGoals = ds.child("goals").getValue(Integer.class);
                    saveWides = ds.child("wides").getValue(Integer.class);
                    saveTackles = ds.child("tackles").getValue(Integer.class);
                    saveTurnovers = ds.child("turnovers").getValue(Integer.class);
                    saveYellowCards = ds.child("yellowCards").getValue(Integer.class);
                    saveRedCards = ds.child("redCards").getValue(Integer.class);

                    calAttackerRating();
                    calDefenderRating();
                    calTotalRating();

                    savedName = attendees.get(playerName).toString();
                    userUID = uids.get(uidName).toString();
                    final FixtureRatingHurling rating = new FixtureRatingHurling(currentEvent, savedName, savePoints, saveGoals, saveWides, saveTackles, saveTurnovers, saveYellowCards, saveRedCards, attackerRating, defenderRating, overallRating);
                    final String ratingID = mSaveRef.push().getKey();
                    final String avgID = mRatingAvg.push().getKey();
                    mSaveRef.child(currentTeam).child("Fixture").child(userUID).child(ratingID).setValue(rating);
                    mRatingAvg.child(currentTeam).child(dateAlt).child(avgID).setValue(rating);
                    mAttendeeReferenceNS.child(userUID).removeValue();
                    mAttendeeReferenceS = mDatabase.child(eventKey).child("attenedee").child("saved");
                    mAttendeeReferenceS.child(userUID).setValue(savedName);
                    mTempRatingH.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(Integer.toString(positionRef)).removeValue();
                    uidName++;
                    playerName++;
                }
                Toast.makeText(FixtureRating.this, "All players saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent (FixtureRating.this, ManagerHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                mDatesToSave.child(currentTeam).orderByChild("date").equalTo(currentEvent).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String id = dataSnapshot.getKey();
                        mDatesToSave.child(currentTeam).child(id).child("date").removeValue();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //mTempRatingH.child(fbUser.getUid()).child(dateAlt).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void finalSaveFootball() {
        playerName = 0;
        uidName = 0;
        positionRef = 1;
        dateAlt = currentEvent.replace("/", "");
        final String id = mSavedDates.push().getKey();
        mSavedDates.child(currentTeam).child(id).setValue(currentEvent);
        mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child("0").removeValue();
            mTempRatingF.child(currentTeam).child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        savePoints = ds.child("points").getValue(Integer.class);
                        saveGoals = ds.child("goals").getValue(Integer.class);
                        saveWides = ds.child("wides").getValue(Integer.class);
                        saveTackles = ds.child("tackles").getValue(Integer.class);
                        saveTurnovers = ds.child("turnovers").getValue(Integer.class);
                        saveYellowCards = ds.child("yellowCards").getValue(Integer.class);
                        saveRedCards = ds.child("redCards").getValue(Integer.class);
                        saveBlackCards = ds.child("blackCards").getValue(Integer.class);

                        calAttackerRating();
                        calDefenderRating();
                        calTotalRating();

                        savedName = attendees.get(playerName).toString();
                        userUID = uids.get(uidName).toString();
                        final FixtureRatingFootball rating = new FixtureRatingFootball(currentEvent, savedName, savePoints, saveGoals, saveWides, saveTackles, saveTurnovers, saveYellowCards, saveRedCards, saveBlackCards, attackerRating, defenderRating, overallRating);
                        final String ratingID = mSaveRef.push().getKey();
                        final String avgID = mRatingAvg.push().getKey();
                        mSaveRef.child(currentTeam).child("Fixture").child(userUID).child(ratingID).setValue(rating);
                        mRatingAvg.child(currentTeam).child(dateAlt).child(avgID).setValue(rating);
                        mUserRef.child(userUID).child("savedDates").child(currentTeam).child(id).setValue(currentEvent);
                        mAttendeeReferenceNS.child(userUID).removeValue();
                        mAttendeeReferenceS = mDatabase.child(eventKey).child("attenedee").child("saved");
                        mAttendeeReferenceS.child(userUID).setValue(savedName);
                        mTempRatingF.child(currentTeam).child(fbUser.getUid()).child(dateAlt).child(Integer.toString(positionRef)).removeValue();
                        playerName++;
                        uidName++;
                    }
                    Toast.makeText(FixtureRating.this, "All players saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent (FixtureRating.this, ManagerHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    mDatesToSave.child(currentTeam).orderByChild("date").equalTo(currentEvent).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String id = dataSnapshot.getKey();
                            mDatesToSave.child(currentTeam).child(id).child("date").removeValue();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //mTempRatingF.removeValue();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
}

