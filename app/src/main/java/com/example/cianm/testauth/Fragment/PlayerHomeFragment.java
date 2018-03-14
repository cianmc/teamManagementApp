package com.example.cianm.testauth.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cianm.testauth.Entity.GlobalVariables;
import com.example.cianm.testauth.R;

/**
 * Created by cianm on 14/03/2018.
 */

public class PlayerHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_player_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        String currentTeam = ((GlobalVariables) getActivity().getApplicationContext()).getCurrentTeam();
        getActivity().setTitle("Home Page: " + currentTeam);
    }

}
