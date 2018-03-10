package com.example.cianm.testauth;

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

import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.Entity.Team;
import com.example.cianm.testauth.Entity.TempRatingFootball;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class FixtureRating extends AppCompatActivity {

    DatabaseReference mTeamReference, mDatabase, mAttendeeReference, mTempRating;
    FirebaseAuth mAuth;
    FirebaseUser fbUser;

    Button mPointsMinus, mPointsPlus, mGoalsMinus, mGoalsPlus, mWidesMinus, mWidesPlus, mTacklesMinus, mTacklesPlus, mTurnoversMinus, mTurnoversPlus, mYellowCardsMinus, mYellowCardsPlus, mRedCardsMinus, mRedCardsPlus, mBlackCardsMinus, mBlackCardsPlus;
    TextView mPointsValue, mGoalsValue, mWidesValue, mTacklesValue, mTurnoversValue, mYellowCardsValue, mRedCardsValue, mBlackCardsValue;
    LinearLayout mBlackCardLinearLayout;
    Spinner mChooseAttendeeSpinner;
    ScrollView mRatingsView;

    int points, goals, wides, tackles, turnovers, yellowCards, redCards, blackCards;
    String currentTeam, currentEvent, eventKey, tPoints, tGoals, tWides, tTackles, tTurnovers, tYellowCards, tRedCards, tBlackCards, position;

    ArrayList<String> attendees;
    Team team;
    TempRatingFootball tempRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixture_rating);
        currentEvent = ((GlobalVariables) FixtureRating.this.getApplication()).getCurrentEvent();
        currentTeam = ((GlobalVariables) FixtureRating.this.getApplication()).getCurrentTeam();

        attendees = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();

        mBlackCardLinearLayout = (LinearLayout) findViewById(R.id.blackCardsLinearLayout);
        mChooseAttendeeSpinner = (Spinner) findViewById(R.id.chooseAttendeeSpinner);
        mRatingsView = (ScrollView) findViewById(R.id.fixRatingScrollView);

        // Minus Buttons
        mPointsMinus = (Button) findViewById(R.id.pointsMinusButton);
        mGoalsMinus = (Button) findViewById(R.id.goalsMinusButton);
        mWidesMinus = (Button) findViewById(R.id.widesMinusButton);
        mTacklesMinus = (Button) findViewById(R.id.tacklesMinusButton);
        mTurnoversMinus = (Button) findViewById(R.id.turnoversMinusButton);
        mYellowCardsMinus = (Button) findViewById(R.id.yellowCardsMinusButton);
        mRedCardsMinus = (Button) findViewById(R.id.redCardsMinusButton);
        mBlackCardsMinus = (Button) findViewById(R.id.blackCardsMinusButton);

        // Plus Buttons
        mPointsPlus = (Button) findViewById(R.id.pointsPlusButton);
        mGoalsPlus = (Button) findViewById(R.id.goalsPlusButton);
        mWidesPlus = (Button) findViewById(R.id.widesPlusButton);
        mTacklesPlus = (Button) findViewById(R.id.tacklesPlusButton);
        mTurnoversPlus = (Button) findViewById(R.id.turnoversPlusButton);
        mYellowCardsPlus = (Button) findViewById(R.id.yellowCardsPlusButton);
        mRedCardsPlus = (Button) findViewById(R.id.redCardsPlusButton);
        mBlackCardsPlus = (Button) findViewById(R.id.blackCardsPlusButton);

        // Values Text Views
        mPointsValue = (TextView) findViewById(R.id.pointsValue);
        mGoalsValue = (TextView) findViewById(R.id.goalsValue);
        mWidesValue = (TextView) findViewById(R.id.widesValue);
        mTacklesValue = (TextView) findViewById(R.id.tacklesValue);
        mTurnoversValue = (TextView) findViewById(R.id.turnoversValue);
        mYellowCardsValue = (TextView) findViewById(R.id.yellowCardsValue);
        mRedCardsValue = (TextView) findViewById(R.id.redCardsValue);
        mBlackCardsValue = (TextView) findViewById(R.id.blackCardsValue);

        // Make all minus buttons invisible so the uses cannot input a minus value
        mPointsMinus.setVisibility(INVISIBLE);
        mGoalsMinus.setVisibility(INVISIBLE);
        mWidesMinus.setVisibility(INVISIBLE);
        mTacklesMinus.setVisibility(INVISIBLE);
        mTurnoversMinus.setVisibility(INVISIBLE);
        mYellowCardsMinus.setVisibility(INVISIBLE);
        mRedCardsMinus.setVisibility(INVISIBLE);
        mBlackCardsMinus.setVisibility(INVISIBLE);

        mTeamReference = FirebaseDatabase.getInstance().getReference("Team");
        mTeamReference.child(currentTeam).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                team = dataSnapshot.getValue(Team.class);
                if(team.getType().equalsIgnoreCase("Hurling")){
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
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    eventKey = child.getKey();
                    mAttendeeReference = mDatabase.child(eventKey).child("attenedee");
                    mAttendeeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                String attendeeName = ds.getValue(String.class);
                                attendees.add(attendeeName);
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(FixtureRating.this, android.R.layout.simple_spinner_dropdown_item, attendees);
                            mChooseAttendeeSpinner.setAdapter(new NothingSelectedSpinnerAdapter(arrayAdapter, R.layout.contact_spinner_row_nothing_selected, FixtureRating.this));
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
                points = Integer.parseInt(mPointsValue.getText().toString());
                mPointsMinus.setVisibility(VISIBLE);
                points++;
                mPointsValue.setText(Integer.toString(points));
                tPoints = mPointsValue.getText().toString();
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).child("points").setValue(tPoints);
            }
        });

        mPointsMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                points = Integer.parseInt(mPointsValue.getText().toString());
                points--;
                if(points == 0) {
                    mPointsMinus.setVisibility(INVISIBLE);
                }
                mPointsValue.setText(Integer.toString(points));
                tPoints = mPointsValue.getText().toString();
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).child("points").setValue(tPoints);
            }
        });


        mGoalsPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goals = Integer.parseInt(mGoalsValue.getText().toString());
                mGoalsMinus.setVisibility(VISIBLE);
                goals++;
                mGoalsValue.setText(Integer.toString(goals));
                tGoals = mGoalsValue.getText().toString();
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).child("goals").setValue(tGoals);
            }
        });

        mGoalsMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goals = Integer.parseInt(mGoalsValue.getText().toString());
                goals--;
                if(goals == 0) {
                    mGoalsMinus.setVisibility(INVISIBLE);
                }
                mGoalsValue.setText(Integer.toString(goals));
                tGoals = mGoalsValue.getText().toString();
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).child("goals").setValue(tGoals);
            }
        });

        mWidesPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wides = Integer.parseInt(mWidesValue.getText().toString());
                mWidesMinus.setVisibility(VISIBLE);
                wides++;
                mWidesValue.setText(Integer.toString(wides));
                tWides = mWidesValue.getText().toString();
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).child("wides").setValue(tWides);
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
                tWides = mWidesValue.getText().toString();
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).child("wides").setValue(tWides);
            }
        });

        mTacklesPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tackles = Integer.parseInt(mTacklesValue.getText().toString());
                mTacklesMinus.setVisibility(VISIBLE);
                tackles++;
                mTacklesValue.setText(Integer.toString(tackles));
                tTackles = mTacklesValue.getText().toString();
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).child("tackles").setValue(tTackles);
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
                tTackles = mTacklesValue.getText().toString();
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).child("tackles").setValue(tTackles);
            }
        });

        mTurnoversPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnovers = Integer.parseInt(mTurnoversValue.getText().toString());
                mTurnoversMinus.setVisibility(VISIBLE);
                turnovers++;
                mTurnoversValue.setText(Integer.toString(turnovers));
                tTurnovers = mTurnoversValue.getText().toString();
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).child("turnovers").setValue(tTurnovers);
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
                tTurnovers = mTurnoversValue.getText().toString();
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).child("turnovers").setValue(tTurnovers);
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
                tYellowCards = mYellowCardsValue.getText().toString();
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).child("yellowCards").setValue(tYellowCards);
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
                tYellowCards = mYellowCardsValue.getText().toString();
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).child("yellowCards").setValue(tYellowCards);
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
                tRedCards = mRedCardsValue.getText().toString();
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).child("redCards").setValue(tRedCards);
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
                tRedCards = mRedCardsValue.getText().toString();
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).child("redCards").setValue(tRedCards);
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
                tBlackCards = mBlackCardsValue.getText().toString();
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).child("blackCards").setValue(tBlackCards);
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
                tBlackCards = mBlackCardsValue.getText().toString();
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).child("blackCards").setValue(tBlackCards);
            }
        });
                addListenerOnSpinnerItemSelection();
    }

    public void addListenerOnSpinnerItemSelection(){
        mChooseAttendeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               // final String position;
                position = Integer.toString(i );
                mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                mTempRating.child(fbUser.getUid()).child(position).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            TempRatingFootball tempRat = dataSnapshot.getValue(TempRatingFootball.class);
                            mPointsValue.setText(tempRat.getPoints());
                            mGoalsValue.setText(tempRat.getGoals());
                            mWidesValue.setText(tempRat.getWides());
                            mTacklesValue.setText(tempRat.getTackles());
                            mTurnoversValue.setText(tempRat.getTurnovers());
                            mYellowCardsValue.setText(tempRat.getYellowCards());
                            mRedCardsValue.setText(tempRat.getRedCards());
                            mBlackCardsValue.setText(tempRat.getBlackCards());

                            points = Integer.parseInt(mPointsValue.getText().toString());
                            goals = Integer.parseInt(mGoalsValue.getText().toString());
                            wides = Integer.parseInt(mWidesValue.getText().toString());
                            tackles = Integer.parseInt(mTacklesValue.getText().toString());
                            turnovers = Integer.parseInt(mTurnoversValue.getText().toString());
                            yellowCards = Integer.parseInt(mYellowCardsValue.getText().toString());
                            redCards = Integer.parseInt(mRedCardsValue.getText().toString());
                            blackCards = Integer.parseInt(mBlackCardsValue.getText().toString());

                            loadButtons();

                            tPoints = mPointsValue.getText().toString();
                            tGoals = mGoalsValue.getText().toString();
                            tWides = mWidesValue.getText().toString();
                            tTackles = mTacklesValue.getText().toString();
                            tTurnovers = mTurnoversValue.getText().toString();
                            tYellowCards = mYellowCardsValue.getText().toString();
                            tRedCards = mRedCardsValue.getText().toString();
                            tBlackCards = mBlackCardsValue.getText().toString();

                            TempRatingFootball tempRate = new TempRatingFootball(tPoints, tGoals, tWides, tTackles, tTurnovers, tYellowCards, tRedCards, tBlackCards);
                            mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                            mTempRating.child(fbUser.getUid()).child(position).setValue(tempRate);

                        } else {
                            newTempRatingButtons();
                            tPoints = mPointsValue.getText().toString();
                            tGoals = mGoalsValue.getText().toString();
                            tWides = mWidesValue.getText().toString();
                            tTackles = mTacklesValue.getText().toString();
                            tTurnovers = mTurnoversValue.getText().toString();
                            tYellowCards = mYellowCardsValue.getText().toString();
                            tRedCards = mRedCardsValue.getText().toString();
                            tBlackCards = mBlackCardsValue.getText().toString();

                            tempRate = new TempRatingFootball(tPoints, tGoals, tWides, tTackles, tTurnovers, tYellowCards, tRedCards, tBlackCards);
                            mTempRating = FirebaseDatabase.getInstance().getReference("TempRating");
                            mTempRating.child(fbUser.getUid()).child(position).setValue(tempRate);
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

    public void newTempRatingButtons(){
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

    public void loadButtons(){
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
}

