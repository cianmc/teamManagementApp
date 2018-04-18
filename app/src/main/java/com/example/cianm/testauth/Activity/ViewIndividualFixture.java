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
import com.example.cianm.testauth.Entity.Fixture;
import com.example.cianm.testauth.Entity.GlobalVariables;
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

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ViewIndividualFixture extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mGoogleMap;

    private TextView mDate, mTime, mOpposition, mLocation, mAvailInfo, mAvailabilityStatus, mAllSaved;
    private Button mViewAttendees, mAvailable, mNotAvailable, mUpdateAvailibility;

    private DatabaseReference mDatabase, attendenceRef, mUserRef, mUserRefP, mUserRefC, mSavedDates;
    DatabaseReference userReference;
    FirebaseAuth mAuth;
    private FirebaseUser fbUser;

    private Double mLat, mLong;
    private String userType, userName, eventType, userID, availability, eventKey, time, confirmKey;
    Pattern timePattern;
    ProgressBar mProgressBar;
    private ArrayList<String> savedDates;

    private User user;
    private Attendee attendee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_individual_fixture);
        final String currentTeam = ((GlobalVariables) ViewIndividualFixture.this.getApplication()).getCurrentTeam();
        setTitle("View fixtures for " + currentTeam);

        final String currentEvent = ((GlobalVariables) ViewIndividualFixture.this.getApplication()).getCurrentEvent();

        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        userID = fbUser.getUid();

        mAllSaved = (TextView) findViewById(R.id.ratingsSaved);
        mDate = (TextView) findViewById(R.id.fixtureDate);
        mTime = (TextView) findViewById(R.id.fixtureTime);
        mOpposition = (TextView) findViewById(R.id.fixtureOpposition);
        mLocation = (TextView) findViewById(R.id.fixtureLocation);
        mAvailInfo = (TextView) findViewById(R.id.availabilityTextView);
        mAvailabilityStatus = (TextView) findViewById(R.id.availabilityStatusTextView);
        mViewAttendees = (Button) findViewById(R.id.viewAttendeesBtn);
        mAvailable = (Button) findViewById(R.id.availableBtn);
        mNotAvailable = (Button) findViewById(R.id.notAvailableBtn);
        mUpdateAvailibility = (Button) findViewById(R.id.updateAvailibilityBtn);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        timePattern = Pattern.compile("\\d{2}:\\d{2}");
        mProgressBar.setVisibility(View.INVISIBLE);
        savedDates = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference("Fixture").child(currentTeam);
        attendenceRef = FirebaseDatabase.getInstance().getReference("Attendee").child(userID);
        mUserRefP = FirebaseDatabase.getInstance().getReference("User").child(fbUser.getUid()).child("pending").child(currentTeam).child("Fixture");
        mUserRefC = FirebaseDatabase.getInstance().getReference("User").child(fbUser.getUid()).child("confirmed").child(currentTeam).child("Fixture");
        mUserRef = FirebaseDatabase.getInstance().getReference("User").child(userID);
        mSavedDates = FirebaseDatabase.getInstance().getReference("SavedDates");

        mSavedDates.child(currentTeam).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String dates = ds.getValue(String.class);
                        savedDates.add(dates);
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.orderByChild("date").equalTo(currentEvent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Fixture fixture = child.getValue(Fixture.class);
                    mDate.setText(fixture.getDate());
                    if (!timePattern.matcher(fixture.getTime().toString()).matches()){
                        time = fixture.getTime().toString() + "0";
                        mTime.setText(time);
                    } else {
                        mTime.setText(fixture.getTime());
                    }
                    mOpposition.setText(fixture.getOpposition());
                    mLocation.setText(fixture.getLocation());
                    eventType = fixture.getType();
                    eventKey = child.getKey();

                    // Get location latitude
                    String latLongA = fixture.getLatlong();
                    latLongA = latLongA.replace("lat/lng:", "");
                    latLongA = latLongA.substring(latLongA.indexOf("(") + 1);
                    latLongA = latLongA.substring(0, latLongA.indexOf(","));
                    mLat = Double.parseDouble(latLongA);

                    // Get location longitude
                    String latLongB = fixture.getLatlong();
                    latLongB = latLongB.replace("lat/lng:", "");
                    latLongB = latLongB.substring(latLongB.indexOf(",") + 1);
                    latLongB = latLongB.substring(0, latLongB.indexOf(")"));
                    mLong = Double.parseDouble(latLongB);

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(ViewIndividualFixture.this);

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
                                Toast.makeText(ViewIndividualFixture.this, "Availability updated: Not Attending", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ViewIndividualFixture.this, PlayerHome.class));

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
                                Toast.makeText(ViewIndividualFixture.this, "Availability updated: Attending", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ViewIndividualFixture.this, PlayerHome.class));
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
                changePendingFixture();
                mDatabase.child(eventKey).child("attenedee").child("attending").child(userID).setValue(userName);
                mDatabase.child(eventKey).child("attenedee").child("notSaved").child(userID).setValue(userName);
                attendenceRef.child(attendeeID).setValue(attendee);
                mAvailable.setVisibility(View.INVISIBLE);
                mNotAvailable.setVisibility(View.INVISIBLE);
                Toast.makeText(ViewIndividualFixture.this, "Availability updated: Attending", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewIndividualFixture.this, PlayerHome.class));
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
                mDatabase.child(eventKey).child("attenedee").child("notAttending").child(userID).setValue(userName);
                changePendingFixture();
                attendenceRef.child(attendeeID).setValue(attendee);
                mAvailable.setVisibility(View.INVISIBLE);
                mNotAvailable.setVisibility(View.INVISIBLE);
                Toast.makeText(ViewIndividualFixture.this, "Availability updated: Not Attending", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewIndividualFixture.this, PlayerHome.class));
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
                    mAllSaved.setVisibility(View.INVISIBLE);
                } else if (userType.equalsIgnoreCase("Player")) {
                    if (savedDates.contains(currentEvent)) {
                        mAvailabilityStatus.setVisibility(View.INVISIBLE);
                        mAvailInfo.setVisibility(View.INVISIBLE);
                        mAvailable.setVisibility(View.INVISIBLE);
                        mNotAvailable.setVisibility(View.INVISIBLE);
                        mUpdateAvailibility.setVisibility(View.INVISIBLE);
                        mAllSaved.setVisibility(View.VISIBLE);
                    } else {
                        mViewAttendees.setVisibility(View.INVISIBLE);
                        mAvailInfo.setVisibility(View.VISIBLE);
                        mAvailable.setVisibility(View.VISIBLE);
                        mNotAvailable.setVisibility(View.VISIBLE);
                        mAvailabilityStatus.setVisibility(View.INVISIBLE);
                        mUpdateAvailibility.setVisibility(View.INVISIBLE);
                        mAllSaved.setVisibility(View.INVISIBLE);
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
                startActivity(new Intent(ViewIndividualFixture.this, ViewAttendeesFixture.class));
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
                mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(location).title("Fixture Location"));
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

    public void changePendingFixture(){
        final String currentEvent = ((GlobalVariables) ViewIndividualFixture.this.getApplication()).getCurrentEvent();
        final String currentTeam = ((GlobalVariables) ViewIndividualFixture.this.getApplication()).getCurrentTeam();
        String confirmedID = mUserRef.push().getKey();
        mUserRefP.orderByChild("date").equalTo(currentEvent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String pendingKey = ds.getKey();
                    mUserRefP.child(pendingKey).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUserRef.child("confirmed").child(currentTeam).child("Fixture").child(confirmedID).setValue(attendee);
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.INVISIBLE);
        mViewAttendees.setVisibility(View.VISIBLE);
    }
}
