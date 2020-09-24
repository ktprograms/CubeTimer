package com.ktcuber.cubetimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SolvesAdapter extends BaseAdapter {

    LayoutInflater mInflater;

    ArrayList<String> solves;
    ArrayList<String> scrambles;
    ArrayList<String> penalties;

    public SolvesAdapter(Context c, ArrayList<String> so, ArrayList<String> sc, ArrayList<String> p) {
        solves = so;
        scrambles = sc;
        penalties = p;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return solves.size();
    }

    @Override
    public Object getItem(int position) {
        return solves.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.solves_listview_detail, null);
        TextView timeTextView = (TextView) v.findViewById(R.id.timeTextView);
        TextView scrambleTextView = (TextView) v.findViewById(R.id.tScrambleTextView);
        TextView penaltyTextView = (TextView) v.findViewById(R.id.penaltyTextView);

        String time = solves.get(position);
        String scramble = scrambles.get(position);
        String penalty = penalties.get(position);

        int maxLen = 50;

        if (scramble.length() > maxLen) {
            scramble = scramble.substring(0, maxLen).concat(" ...");
        }

        timeTextView.setText(time);
        scrambleTextView.setText(scramble);
        penaltyTextView.setText(penalty);

        return v;
    }
}
