package com.example.cianm.testauth.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by cianm on 25/04/2018.
 */

public class ViewMembersFragment extends Fragment {

    DatabaseReference mTeamRef;
    ListView mManagerLV, mPlayerLV;
    TextView mNoDataM, mNoDataP;
    String currentTeam;
    ArrayList<String> managers, players;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_view_members, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        currentTeam = ((GlobalVariables) getActivity().getApplication()).getCurrentTeam();
        getActivity().setTitle("Members of " + currentTeam);

        managers = new ArrayList<>();
        players = new ArrayList<>();

        mManagerLV = (ListView) getView().findViewById(R.id.viewManagerLV);
        mPlayerLV = (ListView) getView().findViewById(R.id.viewPLayerLV);
        mNoDataM = (TextView) getView().findViewById(R.id.noDataManager);
        mNoDataP = (TextView) getView().findViewById(R.id.noDataPlayer);

        getPlayers();
        getManagers();
    }

    public void getPlayers() {
        mTeamRef = FirebaseDatabase.getInstance().getReference("Team");
        mTeamRef.child(currentTeam).child("player").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mNoDataP.setVisibility(View.VISIBLE);
                    mPlayerLV.setVisibility(View.INVISIBLE);
                } else {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String player = ds.getValue(String.class);
                        players.add(player);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, players);
                    mPlayerLV.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getManagers(){
        mTeamRef = FirebaseDatabase.getInstance().getReference("Team");
        mTeamRef.child(currentTeam).child("manager").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    mNoDataM.setVisibility(View.VISIBLE);
                    mManagerLV.setVisibility(View.INVISIBLE);
                } else {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        String manager = ds.getValue(String.class);
                        managers.add(manager);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, managers);
                    mManagerLV.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
