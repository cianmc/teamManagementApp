package com.example.cianm.testauth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cianm.testauth.Entity.BestPlayer;

import java.util.ArrayList;

/**
 * Created by cianm on 25/04/2018.
 */

public class HomeRatingAdapter extends ArrayAdapter<BestPlayer> {

    Context context;
    ArrayList<BestPlayer> bestPlayers;

    public static class ViewHolder{
        TextView nameTV;
        TextView attRatingTV;
        TextView gamedPlayedTV;
        TextView defRatingTV;
        TextView overRatingTV;
    }

    public HomeRatingAdapter(ArrayList<BestPlayer> bestPlayers, Context context){
        super(context, R.layout.view_best_item, bestPlayers);
        this.bestPlayers = bestPlayers;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        ViewHolder viewHolder;

        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.view_best_item, parent, false);
        viewHolder.nameTV = (TextView) convertView.findViewById(R.id.nameViewItem);
        viewHolder.gamedPlayedTV = (TextView) convertView.findViewById(R.id.gamesPlayedViewItem);
        viewHolder.attRatingTV = (TextView) convertView.findViewById(R.id.attViewItem);
        viewHolder.defRatingTV = (TextView) convertView.findViewById(R.id.defViewItem);
        viewHolder.overRatingTV = (TextView) convertView.findViewById(R.id.overViewItem);

        viewHolder.nameTV.setText("Name: " + bestPlayers.get(position).getNameBP());
        viewHolder.gamedPlayedTV.setText("Games played: " + String.valueOf(bestPlayers.get(position).getNoOfEventsPlayed()));
        viewHolder.attRatingTV.setText("Attacker Rating: " + String.valueOf(bestPlayers.get(position).getAttackerRating()));
        viewHolder.defRatingTV.setText("Defender Rating: " + String.valueOf(bestPlayers.get(position).getDefenderRating()));
        viewHolder.overRatingTV.setText("Overall Rating: " + String.valueOf(bestPlayers.get(position).getOverallRating()));

        return convertView;
    }
}
