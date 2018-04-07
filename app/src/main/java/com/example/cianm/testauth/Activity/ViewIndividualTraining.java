package com.example.cianm.testauth.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cianm.testauth.Entity.Attendee;
import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.Entity.Training;
import com.example.cianm.testauth.Entity.User;
import com.example.cianm.testauth.Fragment.ViewEventFragment;
import com.example.cianm.testauth.PlayerHome;
import com.example.cianm.testauth.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class ViewIndividualTraining extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mGoogleMap;

    private TextView mDate, mTime, mDescription, mLocation, mAvailInfo, mAvailabilityStatus;
    private Button mViewAttendees, mAvailable, mNotAvailable, mUpdateAvailibility;

    private DatabaseReference mDatabase, attendenceRef, mUserRef, mUserRefP, mUserRefC;
    DatabaseReference userReference;
    FirebaseAuth mAuth;
    private FirebaseUser fbUser;

    private Double mLat, mLong;
    String userType, userName, eventType, userID, availability, eventKey, time, confirmKey;
    Pattern timePattern;
    ProgressBar mProgressBar;

    User user;
    Attendee attendee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_individual_training);
        final String currentTeam = ((GlobalVariables) ViewIndividualTraining.this.getApplication()).getCurrentTeam();
        setTitle("View trainings for " + currentTeam);

        final String currentEvent = ((GlobalVariables) ViewIndividualTraining.this.getApplication()).getCurrentEvent();

        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        userID = fbUser.getUid();

        mDate = (TextView) findViewById(R.id.trainingDate);
        mTime = (TextView) findViewById(R.id.trainingTime);
        mDescription = (TextView) findViewById(R.id.trainingDescription);
        mLocation = (TextView) findViewById(R.id.trainingLocation);
        mAvailInfo = (TextView) findViewById(R.id.availabilityTextView);
        mAvailabilityStatus = (TextView) findViewById(R.id.availabilityStatusTextView);
        mViewAttendees = (Button) findViewById(R.id.viewAttendeesBtn);
        mAvailable = (Button) findViewById(R.id.availableBtn);
        mNotAvailable = (Button) findViewById(R.id.notAvailableBtn);
        mUpdateAvailibility = (Button) findViewById(R.id.updateAvailibilityBtn);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        timePattern = Pattern.compile("\\d{2}:\\d{2}");
        mProgressBar.setVisibility(View.INVISIBLE);

        mDatabase = FirebaseDatabase.getInstance().getReference("Training").child(currentTeam);
        attendenceRef = FirebaseDatabase.getInstance().getReference("Attendee").child(userID);
        mUserRefP = FirebaseDatabase.getInstance().getReference("User").child(fbUser.getUid()).child("pending").child(currentTeam).child("Training");
        mUserRefC = FirebaseDatabase.getInstance().getReference("User").child(fbUser.getUid()).child("confirmed").child(currentTeam).child("Training");
        mUserRef = FirebaseDatabase.getInstance().getReference("User").child(userID);

        mDatabase.orderByChild("date").equalTo(currentEvent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Training training = child.getValue(Training.class);
                    mDate.setText(training.getDate());
                    if (!timePattern.matcher(training.getTime().toString()).matches()){
                        time = training.getTime().toString() + "0";
                        mTime.setText(time);
                    } else {
                        mTime.setText(training.getTime());
                    }
                    mDescription.setText(training.getDescription());
                    mLocation.setText(training.getLocation());
                    eventType = training.getType();
                    eventKey = child.getKey();

                    // Get location latitude
                    String latLongA = training.getLatlong();
                    latLongA = latLongA.replace("lat/lng:", "");
                    latLongA = latLongA.substring(latLongA.indexOf("(") + 1);
                    latLongA = latLongA.substring(0, latLongA.indexOf(","));
                    mLat = Double.parseDouble(latLongA);

                    // Get location longitude
                    String latLongB = training.getLatlong();
                    latLongB = latLongB.replace("lat/lng:", "");
                    latLongB = latLongB.substring(latLongB.indexOf(",") + 1);
                    latLongB = latLongB.substring(0, latLongB.indexOf(")"));
                    mLong = Double.parseDouble(latLongB);

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(ViewIndividualTraining.this);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // if availibility is 'YES' change to 'NO' and vice versa
        mUpdateAvailibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAvailabilityStatus.setVisibility(View.INVISIBLE);
                mUpdateAvailibility.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                attendenceRef.orderByChild("eventDate").equalTo(currentEvent).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Attendee attendee = ds.getValue(Attendee.class);
                            String key = ds.getKey();
                            String availabilityCheck = attendee.getAvailability();
                            if (availabilityCheck.equalsIgnoreCase("Going")) {
                                // Change availibility to unattending
                                final String newAvailability = "Not going";
                                attendenceRef.child(key).child("availability").setValue(newAvailability);
                                mDatabase.child(eventKey).child("attenedee").child("attending").child(userID).removeValue();
                                mDatabase.child(eventKey).child("attenedee").child("notSaved").child(userID).removeValue();
                                mDatabase.child(eventKey).child("attenedee").child("notAttending").child(userID).setValue(userName);
                                mUserRefC.orderByChild("eventDate").equalTo(currentEvent).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                                            confirmKey = ds.getKey();
                                            mUserRefC.child(confirmKey).child("availability").setValue(newAvailability);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                Toast.makeText(ViewIndividualTraining.this, "Availability updated: Not Attending", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ViewIndividualTraining.this, PlayerHome.class));

                            } else if (availabilityCheck.equalsIgnoreCase("Not going")) {
                                // Change availibility to attending
                                final String newAvailability = "Going";
                                userID = fbUser.getUid();
                                attendenceRef.child(key).child("availability").setValue(newAvailability);
                                mDatabase.child(eventKey).child("attenedee").child("notAttending").child(userID).removeValue();
                                mDatabase.child(eventKey).child("attenedee").child("attending").child(userID).setValue(userName);
                                mDatabase.child(eventKey).child("attenedee").child("notSaved").child(userID).setValue(userName);
                                mUserRefC.orderByChild("eventDate").equalTo(currentEvent).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                                            confirmKey = ds.getKey();
                                            mUserRefC.child(confirmKey).child("availability").setValue(newAvailability);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                Toast.makeText(ViewIndividualTraining.this, "Availability updated: Attending", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ViewIndividualTraining.this, PlayerHome.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        // When clicked user say they ARE attending the event
        mAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAvailInfo.setVisibility(View.INVISIBLE);
                mAvailable.setVisibility(View.INVISIBLE);
                mNotAvailable.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                String attendeeID = attendenceRef.push().getKey();
                availability = "Going";
                userID = fbUser.getUid();
                attendee = new Attendee(currentEvent, userName, availability, eventType, userType, currentTeam);
                changePendingTraining();
                mDatabase.child(eventKey).child("attenedee").child("attending").child(userID).setValue(userName);
                mDatabase.child(eventKey).child("attenedee").child("notSaved").child(userID).setValue(userName);
                attendenceRef.child(attendeeID).setValue(attendee);
                mAvailable.setVisibility(View.INVISIBLE);
                mNotAvailable.setVisibility(View.INVISIBLE);
                Toast.makeText(ViewIndividualTraining.this, "Availability updated: Attending", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewIndividualTraining.this, PlayerHome.class));
            }
        });

        // When clicked the user says that they are NOT attending the event
        mNotAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAvailInfo.setVisibility(View.INVISIBLE);
                mAvailable.setVisibility(View.INVISIBLE);
                mNotAvailable.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                String attendeeID = attendenceRef.push().getKey();
                availability = "Not going";
                attendee = new Attendee(currentEvent, userName, availability, eventType, userType, currentTeam);
                changePendingTraining();
                attendenceRef.child(attendeeID).setValue(attendee);
                mDatabase.child(eventKey).child("attenedee").child("notAttending").child(userID).setValue(userName);
                mAvailable.setVisibility(View.INVISIBLE);
                mNotAvailable.setVisibility(View.INVISIBLE);
                Toast.makeText(ViewIndividualTraining.this, "Availability updated: Not Attending", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewIndividualTraining.this, PlayerHome.class));
            }
        });

        userReference = FirebaseDatabase.getInstance().getReference("User");
        userReference.child(fbUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                userType = user.getType();
                userName = user.getName();
                if (userType.equalsIgnoreCase("Manager")) {
                    mViewAttendees.setVisibility(View.VISIBLE);
                    mAvailInfo.setVisibility(View.INVISIBLE);
                    mAvailable.setVisibility(View.INVISIBLE);
                    mNotAvailable.setVisibility(View.INVISIBLE);
                    mAvailabilityStatus.setVisibility(View.INVISIBLE);
                    mUpdateAvailibility.setVisibility(View.INVISIBLE);
                } else if (userType.equalsIgnoreCase("Player")) {
                    mViewAttendees.setVisibility(View.INVISIBLE);
                    mAvailInfo.setVisibility(View.VISIBLE);
                    mAvailable.setVisibility(View.VISIBLE);
                    mNotAvailable.setVisibility(View.VISIBLE);
                    mAvailabilityStatus.setVisibility(View.INVISIBLE);
                    mUpdateAvailibility.setVisibility(View.INVISIBLE);
                    attendenceRef.orderByChild("eventDate").equalTo(currentEvent).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Attendee attendee = ds.getValue(Attendee.class);
                                String availabilityCheck = attendee.getAvailability();
                                String attendeeType = attendee.getUserType();
                                if (attendeeType.equalsIgnoreCase("Player")) {
                                    if (availabilityCheck.equalsIgnoreCase("Going")) {
                                        mAvailInfo.setVisibility(View.INVISIBLE);
                                        mAvailable.setVisibility(View.INVISIBLE);
                                        mNotAvailable.setVisibility(View.INVISIBLE);
                                        mUpdateAvailibility.setVisibility(View.VISIBLE);
                                        mAvailabilityStatus.setVisibility(View.VISIBLE);
                                        mAvailabilityStatus.setText("Availability confirmed, You are attending this event");
                                    } else if (availabilityCheck.equalsIgnoreCase("Not going")) {
                                        mAvailInfo.setVisibility(View.INVISIBLE);
                                        mAvailable.setVisibility(View.INVISIBLE);
                                        mNotAvailable.setVisibility(View.INVISIBLE);
                                        mUpdateAvailibility.setVisibility(View.VISIBLE);
                                        mAvailabilityStatus.setVisibility(View.VISIBLE);
                                        mAvailabilityStatus.setText("Availability confirmed, You are not attending this event");
                                    }
                                }
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


        mViewAttendees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                mViewAttendees.setVisibility(View.INVISIBLE);
                startActivity(new Intent(ViewIndividualTraining.this, ViewAttendeesTraining.class));
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            if (googleMap != null) {
                mGoogleMap = googleMap;
                LatLng location = new LatLng(mLat, mLong);
                setupGoogleMapScreenSettings(googleMap);
                mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(location).title("Training Location"));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GOOGLE MAPS NOT LOCATED");
        }
    }

    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    public void changePendingTraining(){
        final String currentEvent = ((GlobalVariables) ViewIndividualTraining.this.getApplication()).getCurrentEvent();
        final String currentTeam = ((GlobalVariables) ViewIndividualTraining.this.getApplication()).getCurrentTeam();
        String confirmedID = mUserRef.push().getKey();
        mUserRefP.orderByChild("date").equalTo(currentEvent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String pendingKey = dataSnapshot.getKey();
                mUserRefP.child(pendingKey).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUserRef.child("confirmed").child(currentTeam).child("Training").child(confirmedID).setValue(attendee);
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.INVISIBLE);
        mViewAttendees.setVisibility(View.VISIBLE);
    }
}





