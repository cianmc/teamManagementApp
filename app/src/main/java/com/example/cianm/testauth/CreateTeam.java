package com.example.cianm.testauth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cianm.testauth.Entity.Team;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateTeam extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabase;
    Button mCreteTeam;
    EditText mTeamNumber;
    RadioButton rFootball, rHurling;
    ProgressBar mProgressBar;

    String division, code, name, type;
    Team team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);
        setTitle("Create Team");

        mCreteTeam = (Button) findViewById(R.id.createTeamBtn);
        mTeamNumber = (EditText) findViewById(R.id.teamNumber);
        rFootball = (RadioButton) findViewById(R.id.radioFootball);
        rHurling = (RadioButton) findViewById(R.id.radioHurling);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("Team");

        mCreteTeam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final int value = Integer.valueOf(mTeamNumber.getText().toString());
                if (value == 0) {
                    Toast.makeText(CreateTeam.this, "Division has to be between 1 and 9", Toast.LENGTH_SHORT).show();
                } else if (!rFootball.isChecked() && !rHurling.isChecked()) {
                    Toast.makeText(CreateTeam.this, "You must select a team type", Toast.LENGTH_SHORT).show();
                } else {
                    division = Integer.toString(value);
                    selectType();
                    if (type.equals("Football")) {
                        code = "AFL";
                        name = code + division;
                        team = new Team(name, type, division);
                        mFirebaseDatabase.child(name).setValue(team);
                        Toast.makeText(CreateTeam.this, "Your team has been created" + " " + team.getName(), Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.VISIBLE);
                        mCreteTeam.setVisibility(View.GONE);
                        startActivity(new Intent(CreateTeam.this, ManagerHome.class));
                    } else if (type.equals("Hurling")) {
                        code = "AHL";
                        name = code + division;
                        team = new Team(name, type, division);
                        mFirebaseDatabase.child(name).setValue(team);
                        Toast.makeText(CreateTeam.this, "Your team has been created" + " " + team.getName(), Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.VISIBLE);
                        mCreteTeam.setVisibility(View.GONE);
                        startActivity(new Intent(CreateTeam.this, ManagerHome.class));
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
    protected void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.GONE);
    }
}



