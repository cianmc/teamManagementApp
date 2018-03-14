package com.example.cianm.testauth.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cianm.testauth.Activity.MainActivity;
import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.Entity.User;
import com.example.cianm.testauth.ManagerHome;
import com.example.cianm.testauth.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by cianm on 14/03/2018.
 */

public class SettingsFragment extends Fragment {

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase, mTeamRef, mTrainingRef, mFixtureRef, mAttendeeRef;
    EditText mOldPass, mNewPass, mCofirmPass;
    Button mUpdatePass, mDeleteAcc;

    String oldPassDB, oldPass, newPass, cofirmPass, currentTeam, userType;

    User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        currentTeam = ((GlobalVariables) getActivity().getApplicationContext()).getCurrentTeam();
        getActivity().setTitle("Account Settings");

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mOldPass = (EditText) getView().findViewById(R.id.oldPassEditText);
        mNewPass = (EditText) getView().findViewById(R.id.newPassEditText);
        mCofirmPass = (EditText) getView().findViewById(R.id.confirmPassEditText);
        mUpdatePass = (Button) getView().findViewById(R.id.updatePasswordBtn);
        mDeleteAcc = (Button) getView().findViewById(R.id.deleteAccBtn);

        mDatabase = FirebaseDatabase.getInstance().getReference("User");
        mTeamRef = FirebaseDatabase.getInstance().getReference("Team");
        mTrainingRef = FirebaseDatabase.getInstance().getReference("Training");
        mFixtureRef = FirebaseDatabase.getInstance().getReference("Fixture");
        mAttendeeRef = FirebaseDatabase.getInstance().getReference("Attendee");

        mDatabase.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                oldPassDB = user.getPassword();
                userType = user.getType();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    updatePassword();
            }
        });

        mDeleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userType.equalsIgnoreCase("Manager")){
                    deleteAccountManager();
                } else if (userType.equalsIgnoreCase("Player")){
                    deleteAccountPlayer();
                }
            }
        });
    }

    public void updatePassword(){

        oldPass = mOldPass.getText().toString();
        newPass = mNewPass.getText().toString();
        cofirmPass = mCofirmPass.getText().toString();

        if (oldPass.equals("") || oldPass == null){
            Toast.makeText(getActivity(),"Enter in password", Toast.LENGTH_SHORT).show();
        }
        if (newPass.equals("") || newPass == null){
            Toast.makeText(getActivity(),"Enter in new password", Toast.LENGTH_SHORT).show();
        }
        if (cofirmPass.equals("") || cofirmPass == null){
            Toast.makeText(getActivity(),"Please confirm password", Toast.LENGTH_SHORT).show();
        }
        if (!oldPass.equals(oldPassDB)){
            Toast.makeText(getActivity(),"Incorrect password", Toast.LENGTH_SHORT).show();
        }
        if (!newPass.equals(cofirmPass)){
            Toast.makeText(getActivity(),"Password don't match", Toast.LENGTH_SHORT).show();
        }
        if (newPass.length() < 6){
            Toast.makeText(getActivity(),"Password much be at least 6 characters in length", Toast.LENGTH_SHORT).show();
        }

        mDatabase.child(mUser.getUid()).child("password").setValue(newPass);
        mUser.updatePassword(newPass);
        Toast.makeText(getActivity(),"Password updated", Toast.LENGTH_SHORT).show();
    }

    public void deleteAccountManager(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        mDatabase.child(mUser.getUid()).removeValue();
                        mTeamRef.child(currentTeam).child("manager").child(mUser.getUid()).removeValue();
                        mUser.delete();
                        Toast.makeText(getActivity(), "Deleting Account", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent (getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

    public void deleteAccountPlayer(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        mDatabase.child(mUser.getUid()).removeValue();
                        mTeamRef.child(currentTeam).child("player").child(mUser.getUid()).removeValue();
                        mAttendeeRef.child(mUser.getUid()).removeValue();
                        mUser.delete();
                        Toast.makeText(getActivity(), "Deleting Account", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent (getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

}
