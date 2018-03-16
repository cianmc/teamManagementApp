package com.example.cianm.testauth.Fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cianm.testauth.Entity.Fixture;
import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.Entity.Training;
import com.example.cianm.testauth.ManagerHome;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by cianm on 13/03/2018.
 */

public class CreateEventFragment extends Fragment {

    private static final String TAG = "CreateTeam";

    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    private DatabaseReference mFirebaseDatabaseT, mFirebaseDatabaseF, mFirebaseDatabaseTeam;

    AutocompleteFilter typeFilter;
    AutoCompleteTextView mOppositionTextView;
    Button mPickLocation, mPickTime, mPickDate, mCreateEvent;
    TextView mViewPlace, mViewTime, mViewDate, mOppositionView, mDescriptionView;
    EditText mDescription;
    RadioButton rTraining, rFixture;
    RadioGroup rEvent;
    ProgressBar mProgressBar;
    Intent intent;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private int mYear, mMonth, mDay, mHour, mMinute;
    String time, date, location, opposition, description, latlong, currentTeam;
    Fixture fixture;
    Training training;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Create Event");

        currentTeam = ((GlobalVariables) getActivity().getApplication()).getCurrentTeam();

        mFirebaseDatabaseF = FirebaseDatabase.getInstance().getReference("Fixture");
        mFirebaseDatabaseT = FirebaseDatabase.getInstance().getReference("Training");
        mFirebaseDatabaseTeam = FirebaseDatabase.getInstance().getReference("Team").child(currentTeam);

        // Progress bar
        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);

        // Radio Group
        rEvent = (RadioGroup) getView().findViewById(R.id.createEventRadioGroup);

        // Buttons
        mPickLocation = (Button) getView().findViewById(R.id.pickLocationBtn);
        mPickTime = (Button) getView().findViewById(R.id.pickTimeBtn);
        mPickDate = (Button) getView().findViewById(R.id.pickDateBtn);
        mCreateEvent = (Button) getView().findViewById(R.id.createEventBtn);

        // Radio Buttons
        rFixture = (RadioButton) getView().findViewById(R.id.fixtureRadioButton);
        rTraining = (RadioButton) getView().findViewById(R.id.trainingRadioButton);

        // Edit Texts
        mDescription = (EditText) getView().findViewById(R.id.descriptionEditText);

        // Text Views
        mViewPlace = (TextView) getView().findViewById(R.id.showLocationView);
        mViewTime = (TextView) getView().findViewById(R.id.showTimeView);
        mViewDate = (TextView) getView().findViewById(R.id.showDateView);
        mOppositionTextView = (AutoCompleteTextView) getView().findViewById(R.id.oppositionAutoTextView);
        mOppositionView = (TextView) getView().findViewById(R.id.oppositionTextView);
        mDescriptionView = (TextView) getView().findViewById(R.id.descriptionTextView);

        mViewPlace.setVisibility(View.GONE);
        mViewTime.setVisibility(View.GONE);
        mViewDate.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        mDescription.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mDescription.setRawInputType(InputType.TYPE_CLASS_TEXT);

        rEvent.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(rTraining.isChecked()){

                    mDescription.setVisibility(View.VISIBLE);
                    mDescriptionView.setVisibility(View.VISIBLE);
                    mOppositionTextView.setVisibility(View.GONE);
                    mOppositionView.setVisibility(View.GONE);

                } else if (rFixture.isChecked()){

                    mDescription.setVisibility(View.GONE);
                    mDescriptionView.setVisibility(View.GONE);
                    mOppositionTextView.setVisibility(View.VISIBLE);
                    mOppositionView.setVisibility(View.VISIBLE);

                }
            }
        });

        // Set values for AutoCompleteTextView
        String [] clubs = getResources().getStringArray(R.array.dublinClubs);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, clubs);
        mOppositionTextView.setAdapter(adapter);

        // Construct a GeoDataClient
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        // Construct a PlaceDectectionClient
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);

        // Filter so only location in Ireland can be picker
        typeFilter = new AutocompleteFilter.Builder().setCountry("IE").build();

        // Start location intent
        mPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(typeFilter).build(getActivity());

                } catch (GooglePlayServicesRepairableException e) {

                } catch (GooglePlayServicesNotAvailableException e) {

                }
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                mPickLocation.setVisibility(View.GONE);
            }
        });

        // Time picker dialog
        mPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPickTime.setVisibility(View.GONE);
                mViewTime.setVisibility(View.VISIBLE);
                Calendar mcurrentTime = Calendar.getInstance();
                mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                mMinute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mViewTime.setText(String.format("%02d:%02d",selectedHour, selectedMinute));
                        time = String.valueOf(selectedHour) + ":" + String.valueOf(selectedMinute);
                    }
                }, mHour, mMinute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        mPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPickDate.setVisibility(View.GONE);
                mViewDate.setVisibility(View.VISIBLE);
                Calendar myCalendar = Calendar.getInstance();
                mYear = myCalendar.get(Calendar.YEAR);
                mMonth = myCalendar.get(Calendar.MONTH) + 1;
                mDay = myCalendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int day, int month, int year) {
                        mViewDate.setText(year + "/" + (month + 1) + "/" + day);
                        date = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(day);
                    }
                }, mDay, mMonth, mYear);
                mDatePicker.setTitle("Select Date");
                mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() -1000);
                mDatePicker.show();
            }
        });

        mCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!rFixture.isChecked() && !rTraining.isChecked()){
                    Toast.makeText(getActivity(), "You must select a type of event", Toast.LENGTH_SHORT).show();
                }
                if (location.isEmpty()){
                    Toast.makeText(getActivity(), "Please select a location", Toast.LENGTH_SHORT).show();
                }
                if (date.isEmpty()){
                    Toast.makeText(getActivity(), "Please select a date for the event", Toast.LENGTH_SHORT).show();
                }
                if (time.isEmpty()){
                    Toast.makeText(getActivity(), "Please select a time for the event", Toast.LENGTH_SHORT).show();
                }

                mProgressBar.setVisibility(View.VISIBLE);
                if (rTraining.isChecked()){
                    String userID = mFirebaseDatabaseT.push().getKey();
                    String type = "Training";
                    description = mDescription.getText().toString();
                    if (description.isEmpty()){ Toast.makeText(getActivity(), "Please enter in a description", Toast.LENGTH_SHORT).show(); }
                    training = new Training(date, description, location, time, latlong, type);
                    mFirebaseDatabaseT.child(currentTeam).child(userID).setValue(training);
                    mFirebaseDatabaseTeam.child("trainings").child(userID).setValue(training);
                    mCreateEvent.setVisibility(View.GONE);
                    startActivity(new Intent(getActivity(), ManagerHome.class));
                    Toast.makeText(getActivity(), "Training sucessfully created", Toast.LENGTH_SHORT).show();
                } else if (rFixture.isChecked()){
                    String userID = mFirebaseDatabaseF.push().getKey();
                    String type = "Fixture";
                    opposition = mOppositionTextView.getText().toString();
                    if (opposition.isEmpty()){ Toast.makeText(getActivity(), "Please enter in a  opponent", Toast.LENGTH_SHORT).show();}
                    fixture = new Fixture (date, location, time, opposition, latlong, type);
                    mFirebaseDatabaseF.child(currentTeam).child(userID).setValue(fixture);
                    mFirebaseDatabaseTeam.child("fixtures").child(userID).setValue(fixture);
                    mCreateEvent.setVisibility(View.GONE);
                    startActivity(new Intent(getActivity(), ManagerHome.class));
                    Toast.makeText(getActivity(), "Fixture sucessfully created", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                mViewPlace.setVisibility(View.VISIBLE);
                mViewPlace.setText(place.getName());
                location = place.getName().toString();
                latlong = place.getLatLng().toString();
                mPickLocation.setVisibility(View.GONE);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.GONE);
        mPickDate.setVisibility(View.VISIBLE);
        mPickTime.setVisibility(View.VISIBLE);

    }

}
