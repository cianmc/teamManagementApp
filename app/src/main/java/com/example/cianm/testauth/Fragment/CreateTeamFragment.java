package com.example.cianm.testauth.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.Entity.Team;
import com.example.cianm.testauth.ManagerHome;
import com.example.cianm.testauth.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by cianm on 11/03/2018.
 */

public class CreateTeamFragment extends Fragment {

    private DatabaseReference mFirebaseDatabase;
    Button mCreteTeam;
    EditText mTeamNumber;
    RadioButton rFootball, rHurling;
    ProgressBar mProgressBar;
    Fragment fragment = null;

    String division, code, name, type;
    Team team;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_create_team, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        String currentTeam = ((GlobalVariables) getActivity().getApplication()).getCurrentTeam();
        getActivity().setTitle("Create Team: " + currentTeam);
        mCreteTeam = (Button) getView().findViewById(R.id.createTeamBtn);
        mTeamNumber = (EditText) getView().findViewById(R.id.teamNumber);
        rFootball = (RadioButton) getView().findViewById(R.id.radioFootball);
        rHurling = (RadioButton) getView().findViewById(R.id.radioHurling);
        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("Team");

        mCreteTeam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String division = mTeamNumber.getText().toString();
                final int divisionInt = Integer.valueOf(mTeamNumber.getText().toString());
                if (divisionInt == 0) {
                    mTeamNumber.setError("Division cannot be equal to zero");
                    return;
                } else if (TextUtils.isEmpty(division)){
                    mTeamNumber.setError("Please enter in a division");
                    return;
                } else if (!rFootball.isChecked() && !rHurling.isChecked()) {
                    Toast.makeText(getActivity(), "You must select a team type", Toast.LENGTH_SHORT).show();
                } else {
                    selectType();
                    if (type.equals("Football")) {
                        code = "AFL";
                        name = code + division;
                        team = new Team(name, type, divisionInt);
                        mFirebaseDatabase.child(name).setValue(team);
                        Toast.makeText(getActivity(), "Your team has been created" + " " + team.getName(), Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.VISIBLE);
                        mCreteTeam.setVisibility(View.GONE);
                        Intent intent = new Intent (getActivity(), ManagerHome.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else if (type.equals("Hurling")) {
                        code = "AHL";
                        name = code + division;
                        team = new Team(name, type, divisionInt);
                        mFirebaseDatabase.child(name).setValue(team);
                        Toast.makeText(getActivity(), "Your team has been created" + " " + team.getName(), Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.VISIBLE);
                        mCreteTeam.setVisibility(View.GONE);
                        Intent intent = new Intent (getActivity(), ManagerHome.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                }

            }
        });
    }

    public String selectType() {
        if (rFootball.isChecked()) {
            type = "Football";
        } else if (rHurling.isChecked()) {
            type = "Hurling";
        }
        return type;
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.GONE);
    }
}


