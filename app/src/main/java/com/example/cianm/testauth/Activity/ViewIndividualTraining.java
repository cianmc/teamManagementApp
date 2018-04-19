package com.example.cianm.testauth.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cianm.testauth.Entity.Attendee;
import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.Entity.Training;
import com.example.cianm.testauth.Entity.User;
import com.example.cianm.testauth.Fragment.ViewEventFragment;
import com.example.cianm.testauth.PlayerHome;
import com.example.cianm.testauth.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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
import java.util.Calendar;
import java.util.regex.Pattern;

public class ViewIndividualTraining extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "CreateTeam";
    GoogleMap mGoogleMap;
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;

    private TextView mDate, mTime, mDescription, mLocation, mAvailInfo, mAvailabilityStatus, mEditInfo;
    private Button mViewAttendees, mAvailable, mNotAvailable, mUpdateAvailibility, mEdit, mSave;
    private EditText mDescriptionET;
    AutocompleteFilter typeFilter;

    private DatabaseReference mDatabase, attendenceRef, mUserRef, mUserRefP, mUserRefC, mCheckDates;
    DatabaseReference userReference;
    FirebaseAuth mAuth;
    private FirebaseUser fbUser;

    private Double mLat, mLong;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private int mYear, mMonth, mDay, mHour, mMinute;
    String userType, userName, eventType, userID, availability, eventKey, time, confirmKey, date, latlong, location;;
    Pattern timePattern;
    ProgressBar mProgressBar;
    private ArrayList<String> checkDates;
    Intent intent;

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

        // TextView
        mDate = (TextView) findViewById(R.id.trainingDate);
        mTime = (TextView) findViewById(R.id.trainingTime);
        mDescription = (TextView) findViewById(R.id.trainingDescription);
        mLocation = (TextView) findViewById(R.id.trainingLocation);
        mAvailInfo = (TextView) findViewById(R.id.availabilityTextView);
        mAvailabilityStatus = (TextView) findViewById(R.id.availabilityStatusTextView);
        mEditInfo = (TextView) findViewById(R.id.editInstruction);
        mDescriptionET = (EditText) findViewById(R.id.descMultiLine);

        // Button
        mViewAttendees = (Button) findViewById(R.id.viewAttendeesBtn);
        mAvailable = (Button) findViewById(R.id.availableBtn);
        mNotAvailable = (Button) findViewById(R.id.notAvailableBtn);
        mUpdateAvailibility = (Button) findViewById(R.id.updateAvailibilityBtn);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mSave = (Button) findViewById(R.id.save);
        mEdit = (Button) findViewById(R.id.edit);

        mDate.setClickable(false);
        mTime.setClickable(false);
        mDescription.setClickable(false);
        mLocation.setClickable(false);

        timePattern = Pattern.compile("\\d{2}:\\d{2}");
        mProgressBar.setVisibility(View.INVISIBLE);
        checkDates = new ArrayList<>();

        // Construct a GeoDataClient
        mGeoDataClient = Places.getGeoDataClient(ViewIndividualTraining.this, null);
        // Construct a PlaceDectectionClient
        mPlaceDetectionClient = Places.getPlaceDetectionClient(ViewIndividualTraining.this, null);
        // Filter so only location in Ireland can be picker
        typeFilter = new AutocompleteFilter.Builder().setCountry("IE").build();

        mDatabase = FirebaseDatabase.getInstance().getReference("Training").child(currentTeam);
        attendenceRef = FirebaseDatabase.getInstance().getReference("Attendee").child(userID);
        mUserRefP = FirebaseDatabase.getInstance().getReference("User").child(fbUser.getUid()).child("pending").child(currentTeam).child("Training");
        mUserRefC = FirebaseDatabase.getInstance().getReference("User").child(fbUser.getUid()).child("confirmed").child(currentTeam).child("Training");
        mUserRef = FirebaseDatabase.getInstance().getReference("User").child(userID);
        mCheckDates = FirebaseDatabase.getInstance().getReference("CheckDate");

        mCheckDates.child(currentTeam).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        String dates = ds.getValue(String.class);
                        checkDates.add(dates);
                    }
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
                    mEdit.setVisibility(View.VISIBLE);
                } else if (userType.equalsIgnoreCase("Player")) {
                    mViewAttendees.setVisibility(View.INVISIBLE);
                    mAvailInfo.setVisibility(View.VISIBLE);
                    mAvailable.setVisibility(View.VISIBLE);
                    mNotAvailable.setVisibility(View.VISIBLE);
                    mAvailabilityStatus.setVisibility(View.INVISIBLE);
                    mUpdateAvailibility.setVisibility(View.INVISIBLE);
                    mEdit.setVisibility(View.VISIBLE);
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

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDetails();
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDetails();
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
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String pendingKey = ds.getKey();
                    mUserRefP.child(pendingKey).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUserRef.child("confirmed").child(currentTeam).child("Training").child(confirmedID).setValue(attendee);
    }

    public void editDetails(){
        mViewAttendees.setVisibility(View.INVISIBLE);
        mEdit.setVisibility(View.INVISIBLE);
        mSave.setVisibility(View.VISIBLE);
        mEditInfo.setVisibility(View.VISIBLE);
        mDate.setClickable(true);
        mTime.setClickable(true);
        mDescription.setClickable(true);
        mLocation.setClickable(true);
        mDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDescription.setVisibility(View.INVISIBLE);
                mDescriptionET.setVisibility(View.VISIBLE);
            }
        });
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate();
            }
        });
        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickTime();
            }
        });
        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleMap.clear();
                pickPlace();

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(ViewIndividualTraining.this);
            }
        });
    }

    public void saveDetails(){
        if(checkDates.contains(date)){
            Toast.makeText(ViewIndividualTraining.this, "There is an event already set on that day", Toast.LENGTH_SHORT).show();
            pickDate();
        } else if (location == null) {
            Toast.makeText(ViewIndividualTraining.this, "You must enter in a location", Toast.LENGTH_SHORT).show();
            pickPlace();
        } else {
            mSave.setVisibility(View.INVISIBLE);
            mEditInfo.setVisibility(View.INVISIBLE);
            mEdit.setVisibility(View.VISIBLE);
            mDate.setClickable(false);
            mTime.setClickable(false);
            mDescription.setClickable(false);
            mLocation.setClickable(false);
            mLocation.setText(mLocation.getText().toString());
            mDescriptionET.setText(mDescription.getText().toString());
            String time = mTime.getText().toString();
            String date = mDate.getText().toString();
            String description = mDescriptionET.getText().toString();
            mLocation.setText(location);
            mDatabase.child(eventKey).child("location").setValue(location);
            mDatabase.child(eventKey).child("latlong").setValue(latlong);
            mDatabase.child(eventKey).child("time").setValue(time);
            mDatabase.child(eventKey).child("date").setValue(date);
            mDatabase.child(eventKey).child("description").setValue(description);
            mDescription.setVisibility(View.VISIBLE);
            mDescriptionET.setVisibility(View.INVISIBLE);
            mDescription.setText(description);
            mViewAttendees.setVisibility(View.VISIBLE);
        }
    }

    public void pickTime(){
        Calendar mcurrentTime = Calendar.getInstance();
        mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        mMinute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(ViewIndividualTraining.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                mTime.setText(String.format("%02d:%02d",selectedHour, selectedMinute));
                time = String.valueOf(selectedHour) + ":" + String.valueOf(selectedMinute);
            }
        }, mHour, mMinute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void pickDate(){
        mDate.setVisibility(View.VISIBLE);
        Calendar myCalendar = Calendar.getInstance();
        mYear = myCalendar.get(Calendar.YEAR);
        mMonth = myCalendar.get(Calendar.MONTH) + 1;
        mDay = myCalendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(ViewIndividualTraining.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int day, int month, int year) {
                mDate.setText(year + "/" + (month + 1) + "/" + day);
                date = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
            }
        }, mDay, mMonth, mYear);
        mDatePicker.setTitle("Select Date");
        mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() -1000);
        mDatePicker.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(ViewIndividualTraining.this, data);
                mLocation.setText(place.getName());
                location = place.getName().toString();
                latlong = place.getLatLng().toString();
                String latLongA = latlong;
                latLongA = latLongA.replace("lat/lng:", "");
                latLongA = latLongA.substring(latLongA.indexOf("(") + 1);
                latLongA = latLongA.substring(0, latLongA.indexOf(","));
                mLat = Double.parseDouble(latLongA);

                // Get location longitude
                String latLongB = latlong;
                latLongB = latLongB.replace("lat/lng:", "");
                latLongB = latLongB.substring(latLongB.indexOf(",") + 1);
                latLongB = latLongB.substring(0, latLongB.indexOf(")"));
                mLong = Double.parseDouble(latLongB);

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(ViewIndividualTraining.this);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

                Status status = PlaceAutocomplete.getStatus(ViewIndividualTraining.this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(ViewIndividualTraining.this, "You must enter in a location", Toast.LENGTH_SHORT).show();
                pickPlace();
            }
        }
    }

    public void pickPlace(){
        try {
            intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(typeFilter).build(ViewIndividualTraining.this);

        } catch (GooglePlayServicesRepairableException e) {

        } catch (GooglePlayServicesNotAvailableException e) {

        }
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.INVISIBLE);
        mViewAttendees.setVisibility(View.VISIBLE);
    }
}





