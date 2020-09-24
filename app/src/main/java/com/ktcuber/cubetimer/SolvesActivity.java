package com.ktcuber.cubetimer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SolvesActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference userRef;
    DatabaseReference solvesRef;

    String uid;
    String puzzleString;
    long t;

    ArrayList<String> times;
    ArrayList<String> scrambles;
    ArrayList<String> penalties;
    ArrayList<String> ids;
    ArrayList<String> ts;

    FirebaseCallback fc;

    ListView solvesListView;
    Spinner sPuzzleSpinner;

    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solves);
        getSupportActionBar().hide();

        fc = new FirebaseCallback() {
            @Override
            public void onCallback(List<String> l1, List<String> l2, List<String> l3) {
                SolvesAdapter solvesAdapter = new SolvesAdapter(getApplication(), times, scrambles, penalties);
                solvesListView.setAdapter(solvesAdapter);
            }
        };

        uid = currentUser.getUid();
        userRef = firebaseDatabase.getReference(uid);

        solvesListView = (ListView) findViewById(R.id.solvesListView);
        sPuzzleSpinner = (Spinner) findViewById(R.id.sPuzzleSpinner);

        solvesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent solvesSolveIntent = new Intent(getApplicationContext(), SolveActivity.class);
                solvesSolveIntent.putExtra("com.example.timerapp.TIME", ts.get(position));
                solvesSolveIntent.putExtra("com.example.timerapp.SCRAMBLE", scrambles.get(position));
                solvesSolveIntent.putExtra("com.example.timerapp.PENALTY", penalties.get(position));
                solvesSolveIntent.putExtra("com.example.timerapp.PUZZLE", puzzleString);
                solvesSolveIntent.putExtra("com.example.timerapp.ID", ids.get(position));
                startActivity(solvesSolveIntent);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.puzzle_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sPuzzleSpinner.setAdapter(adapter);

        sPuzzleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("HI");

                if (position == 0) {
                    puzzleString = "Two";
                } else if (position == 1) {
                    puzzleString = "Three";
                } else if (position == 2) {
                    puzzleString = "Four";
                } else if (position == 3) {
                    puzzleString = "Five";
                } else if (position == 4) {
                    puzzleString = "Six";
                } else if (position == 5) {
                    puzzleString = "Seven";
                } else if (position == 6) {
                    puzzleString = "Pyra";
                } else if (position == 7) {
                    puzzleString = "Squan";
                } else if (position == 8) {
                    puzzleString = "Mega";
                } else if (position == 9) {
                    puzzleString = "Clock";
                } else if (position == 10) {
                    puzzleString = "Skewb";
                }

                solvesRef = userRef.child(puzzleString);

                solvesRef.addListenerForSingleValueEvent(valueEventListener);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        puzzleString = "Two";

        solvesRef = userRef.child(puzzleString);

        times = new ArrayList<>();
        scrambles = new ArrayList<>();
        penalties = new ArrayList<>();
        ids = new ArrayList<>();
        ts = new ArrayList<>();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                times.clear();
                scrambles.clear();
                penalties.clear();
                ids.clear();
                ts.clear();
                String p;
                for (DataSnapshot solveSnapshot: snapshot.getChildren()) {
                    t = Long.parseLong(solveSnapshot.child("Time").getValue().toString());
                    ts.add(solveSnapshot.child("Time").getValue().toString());
                    scrambles.add(solveSnapshot.child("Scramble").getValue().toString());
                    penalties.add(p = solveSnapshot.child("Penalty").getValue().toString());
                    ids.add(solveSnapshot.getKey());
                    String time = formatTime(t);
                    if (p.equals("DNF")) {
                        times.add("DNF(" + time + ")");
                    } else if (p.equals("+2")) {
                        times.add(formatTimePT(time));
                    } else {
                        times.add(time);
                    }
                }

                fc.onCallback(times, scrambles, penalties);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        solvesRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private interface FirebaseCallback {
        void onCallback(List<String> l1, List<String> l2, List<String> l3);
    }

    public String formatTime(long t) {
        long seconds = (t / 1000) % 60;
        final long minutes = ((t - (1000 * seconds)) / 1000) / 60;
        long milliseconds = t % 1000;
        String formatSeconds = String.format("%2s", seconds).replace(' ', '0');
        String formatMilliseconds = String.format("%3s", milliseconds).replace(' ', '0');
        String tDisplay = minutes + ":" + formatSeconds + "." + formatMilliseconds;
        return tDisplay;
    }

    public String formatTimePT(String time) {
        /*long seconds = ((time / 1000) % 60) + 2;
        final long minutes = ((time - (1000 * seconds)) / 1000) / 60;
        long milliseconds = time % 1000;
        String formatSeconds = String.format("%2s", seconds).replace(' ', '0');
        String formatMilliseconds = String.format("%3s", milliseconds).replace(' ', '0');
        String tDisplay = minutes + ":" + formatSeconds + "." + formatMilliseconds + "+";
        return tDisplay;*/

        String milliseconds = time.split(Pattern.quote("."))[1];
        String leftOfDot = time.split(Pattern.quote("."))[0];
        String minutes = leftOfDot.split(":")[0];
        String seconds = leftOfDot.split(":")[1];
        int secs = Integer.parseInt(seconds);
        int secsPt = secs + 2;
        String secsPtString = String.format("%2s", secsPt).replace(' ', '0');

        String tDisplay = minutes + ":" + secsPtString + "." + milliseconds + "+";

        return tDisplay;
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}
