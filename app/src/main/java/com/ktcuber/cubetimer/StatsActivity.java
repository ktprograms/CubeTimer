package com.ktcuber.cubetimer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference userRef;
    DatabaseReference solvesRef;

    String puzzleString;
    String uid;
    ArrayList<String> times;
    List<String> puzzles;

    Spinner stPuzzleSpinner;

    TextView countTextView;
    TextView bSingleTextView;
    TextView meanTextView;
    TextView cMo3TextView;
    TextView cAo5TextView;
    TextView cAo12TextView;
    TextView cMo25TextView;
    TextView cMo50TextView;
    TextView cAo5Ao5sTextView;

    ValueEventListener valueEventListener;

    FirebaseCallback fc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        getSupportActionBar().hide();

        fc = new FirebaseCallback() {
            @Override
            public void onCallback(List<String> l1) {
                showStats(times);
            }
        };

        puzzles = Arrays.asList(getResources().getStringArray(R.array.wca_puzzle_list));

        uid = currentUser.getUid();
        userRef = firebaseDatabase.getReference(uid);

        stPuzzleSpinner = (Spinner) findViewById(R.id.stPuzzleSpinner);

        countTextView = (TextView) findViewById(R.id.countTextView);
        bSingleTextView = (TextView) findViewById(R.id.bSingleTextView);
        meanTextView = (TextView) findViewById(R.id.meanTextView);
        cMo3TextView = (TextView) findViewById(R.id.cMo3TextView);
        cAo5TextView = (TextView) findViewById(R.id.cAo5TextView);
        cAo12TextView = (TextView) findViewById(R.id.cAo12TextView);
        cMo25TextView = (TextView) findViewById(R.id.cMo25TextView);
        cMo50TextView = (TextView) findViewById(R.id.cMo50TextView);
        cAo5Ao5sTextView = (TextView) findViewById(R.id.cAo5Ao5sTextView);
        hideTextViews();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.puzzle_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stPuzzleSpinner.setAdapter(adapter);

        stPuzzleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                puzzleString = puzzles.get(position);

                hideTextViews();

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

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                /*times.clear();
                scrambles.clear();
                penalties.clear();
                String p;
                for (DataSnapshot solveSnapshot: snapshot.getChildren()) {
                    String t = solveSnapshot.child("Time").getValue().toString();
                    scrambles.add(solveSnapshot.child("Scramble").getValue().toString());
                    penalties.add(p = solveSnapshot.child("Penalty").getValue().toString());
                    if (p.equals("DNF")) {
                        times.add("DNF(" + t + ")");
                    } else {
                        times.add(t);
                    }
                }

                fc.onCallback(times, scrambles, penalties);*/

                times = new ArrayList<>();

                for (DataSnapshot solveSnapshot : snapshot.getChildren()) {
                    long t = Long.parseLong(solveSnapshot.child("Time").getValue().toString());
                    if (solveSnapshot.child("Penalty").getValue().equals("+2")) {
                        times.add((t + 2000) + "");
                    } else if (solveSnapshot.child("Penalty").getValue().equals("DNF")) {
                        times.add("DNF");
                    } else {
                        times.add(t + "");
                    }
                }

                fc.onCallback(times);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        solvesRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public void hideTextViews() {
        countTextView.setVisibility(View.INVISIBLE);
        bSingleTextView.setVisibility(View.INVISIBLE);
        meanTextView.setVisibility(View.INVISIBLE);
        cMo3TextView.setVisibility(View.INVISIBLE);
        cAo5TextView.setVisibility(View.INVISIBLE);
        cAo12TextView.setVisibility(View.INVISIBLE);
        cMo25TextView.setVisibility(View.INVISIBLE);
        cMo50TextView.setVisibility(View.INVISIBLE);
        cAo5Ao5sTextView.setVisibility(View.INVISIBLE);
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

    public void showStats(ArrayList<String> ts) {
        showCount(ts);
        showBest(ts);
        showMean(ts);
        showMo3(ts);
        showAo5(ts);
        showAo12(ts);
        showMo25(ts);
        showMo50(ts);
        showAo5Ao5s(ts);
    }

    public void showCount(ArrayList<String> l) {
        countTextView.setText("Solve Count: " + l.size());
        countTextView.setVisibility(View.VISIBLE);
    }

    public void showBest(ArrayList<String> l1) {
        if (l1.size() > 0) {
            l1.removeAll(Collections.singleton("DNF"));
            if (l1.size() > 0) {
                String min = min(l1);
                bSingleTextView.setText("Best Solve: " + formatTime(Long.parseLong(min)));
                bSingleTextView.setVisibility(View.VISIBLE);
            } else {
                bSingleTextView.setText("All solves are DNF");
                bSingleTextView.setVisibility(View.VISIBLE);
            }
        } else {
            bSingleTextView.setText("No solves yet...");
            bSingleTextView.setVisibility(View.VISIBLE);
        }
    }

    public void showMean(ArrayList<String> t1) {
        if (t1.size() > 0) {
            long sum = 0L;
            int count = 0;
            for (String s : t1) {
                if (!s.equals("DNF")) {
                    sum += Long.parseLong(s);
                    count++;
                }
            }
            long mean = sum / count;
            String meanString = formatTime(mean);
            meanTextView.setText("Session Mean: " + meanString);
            meanTextView.setVisibility(View.VISIBLE);
        }
    }

    public void showMo3(ArrayList<String> t2) {
        int size = t2.size();
        if (size >= 3) {
            long sum = 0L;
            t2 = new ArrayList<>(t2.subList(size - 3, size));
            if (!t2.contains("DNF")) {
                for (String s : t2) {
                    sum += Long.parseLong(s);
                }
                long mean = sum / 3;
                String meanString = formatTime(mean);
                cMo3TextView.setText("Current Mo3: " + meanString);
                cMo3TextView.setVisibility(View.VISIBLE);
            } else {
                cMo3TextView.setText("Current Mo3: DNF");
                cMo3TextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showAo5(ArrayList<String> t3) {
        int size = t3.size();
        if (size >= 5) {
            long sum = 0L;
            t3 = new ArrayList<>(t3.subList(size - 5, size));
            if (!t3.contains("DNF")) {
                String max = max(t3);
                String min = min(t3);
                t3.remove(min);
                t3.remove(max);
                for (String s : t3) {
                    sum += Long.parseLong(s);
                }
                long avg = sum / 3;
                String avgString = formatTime(avg);
                cAo5TextView.setText("Current Ao5: " + avgString);
                cAo5TextView.setVisibility(View.VISIBLE);
            } else if (t3.indexOf("DNF") == t3.lastIndexOf("DNF")) {
                t3.remove("DNF");
                String min = min(t3);
                t3.remove(min);
                for (String s : t3) {
                    sum += Long.parseLong(s);
                }
                long avg = sum / 3;
                String avgString = formatTime(avg);
                cAo5TextView.setText("Current Ao5: " + avgString);
                cAo5TextView.setVisibility(View.VISIBLE);
            } else {
                cAo5TextView.setText("Current Ao5: DNF");
                cAo5TextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showAo12(ArrayList<String> t4) {
        int size = t4.size();
        if (size >= 12) {
            long sum = 0L;
            t4 = new ArrayList<>(t4.subList(size - 12, size));
            if (!t4.contains("DNF")) {
                String max = max(t4);
                String min = min(t4);
                t4.remove(min);
                t4.remove(max);
                for (String s : t4) {
                    sum += Long.parseLong(s);
                }
                long avg = sum / 10;
                String avgString = formatTime(avg);
                cAo12TextView.setText("Current Ao12: " + avgString);
                cAo12TextView.setVisibility(View.VISIBLE);
            } else if (t4.indexOf("DNF") == t4.lastIndexOf("DNF")) {
                t4.remove("DNF");
                String min = min(t4);
                t4.remove(min);
                for (String s : t4) {
                    sum += Long.parseLong(s);
                }
                long avg = sum / 10;
                String avgString = formatTime(avg);
                cAo12TextView.setText("Current Ao12: " + avgString);
                cAo12TextView.setVisibility(View.VISIBLE);
            } else {
                cAo12TextView.setText("Current Ao12: DNF");
                cAo12TextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showMo25(ArrayList<String> t5) {
        int size = t5.size();
        if (size >= 25) {
            long sum = 0L;
            t5 = new ArrayList<>(t5.subList(size - 25, size));
            if (!t5.contains("DNF")) {
                for (String s : t5) {
                    sum += Long.parseLong(s);
                }
                long mean = sum / 25;
                String meanString = formatTime(mean);
                cMo25TextView.setText("Current Mo25: " + meanString);
                cMo25TextView.setVisibility(View.VISIBLE);
            } else {
                cMo25TextView.setText("Current Mo25: DNF");
                cMo25TextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showAo5Ao5s(ArrayList<String> t6) {
        int size = t6.size();
        if (size >= 25) {
            ArrayList<String> list = new ArrayList<>();
            t6 = new ArrayList<>(t6.subList(size - 25, size));
            for (int i = 5; i > 0; i--) {
                list.add(Ao5(new ArrayList<>(t6.subList(25 - (i * 5), 25 - ((i * 5) - 5)))));
            }
            String Ao5Ao5s = Ao5(list);
            if (!Ao5Ao5s.equals("DNF")) {
                String result = formatTime(Long.parseLong(Ao5Ao5s));
                cAo5Ao5sTextView.setText("Current Ao5Ao5s: " + result);
                cAo5Ao5sTextView.setVisibility(View.VISIBLE);
            } else {
                cAo5Ao5sTextView.setText("Current Ao5Ao5s: DNF");
                cAo5Ao5sTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showMo50(ArrayList<String> t7) {
        int size = t7.size();
        if (size >= 50) {
            long sum = 0L;
            t7 = new ArrayList<>(t7.subList(size - 50, size));
            if (!t7.contains("DNF")) {
                for (String s : t7) {
                    sum += Long.parseLong(s);
                }
                long mean = sum / 50;
                String meanString = formatTime(mean);
                cMo50TextView.setText("Current Mo50: " + meanString);
                cMo50TextView.setVisibility(View.VISIBLE);
            } else {
                cMo50TextView.setText("Current Mo50: DNF");
                cMo50TextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public String Ao5(ArrayList<String> list1) {
        String returnString;
        if (!list1.contains("DNF")) {
            long sum = 0L;
            String min = min(list1);
            String max = max(list1);
            list1.remove(min);
            list1.remove(max);
            for (String s : list1) {
                sum += Long.parseLong(s);
            }
            long avg = sum / 3;
            returnString = avg + "";
        } else if (list1.indexOf("DNF") == list1.lastIndexOf("DNF")) {
            long sum = 0L;
            String min = min(list1);
            list1.remove(min);
            list1.remove("DNF");
            for (String s : list1) {
                sum += Long.parseLong(s);
            }
            long avg = sum / 3;
            returnString = avg + "";
        } else {
            returnString = "DNF";
        }

        return returnString;
    }

    public String min(ArrayList<String> l1) {
        long min = Long.MAX_VALUE;
        for (String s: l1) {
            long l = Long.parseLong(s);
            if (l < min) {
                min = l;
            }
        }
        return min + "";
    }

    public String max(ArrayList<String> l2) {
        long max = Long.MIN_VALUE;
        for (String s: l2) {
            long l = Long.parseLong(s);
            if (l > max) {
                max = l;
            }
        }
        return max + "";
    }

    private interface FirebaseCallback {
        void onCallback(List<String> l1);
    }
}
