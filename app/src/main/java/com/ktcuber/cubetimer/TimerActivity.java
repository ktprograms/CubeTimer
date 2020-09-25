package com.ktcuber.cubetimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import org.worldcubeassociation.tnoodle.scrambles.PuzzleRegistry;
import org.worldcubeassociation.tnoodle.svglite.Svg;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TimerActivity extends AppCompatActivity {

    Button tTimerBtn;
    Button tScrambleBtn;
    Button tPtBtn;
    Button tDnfBtn;
    Button tOkBtn;
    Button tDeleteBtn;
    TextView tTimeTextView;
    TextView tInspectionTextView;
    TextView tScrambleTextView;
    Chronometer tChronometer;
    Spinner tPuzzleSpinner;
    ImageView tImageView;

    String status = "ready";
    String scramble;
    String currentScramble;
    String pastScramble;
    String uid;
    String puzzleString;
    String inspectionPenalty;
    String formatSeconds;
    String formatMilliseconds;
    String tDisplay;
    List<String> puzzles;
    long time;
    long startTimeMillis;
    boolean dnf = false;

    PuzzleRegistry puzzle;
    Svg image;
    CountDownTimer inspectionTimer;
    scrambleThread sT;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference userRef;
    DatabaseReference solveRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        puzzles = Arrays.asList(getResources().getStringArray(R.array.wca_puzzle_list));

        uid = user.getUid();
        userRef = firebaseDatabase.getReference(uid);
        puzzleString = "Two";

        tTimerBtn = (Button) findViewById(R.id.tTimerBtn);
        tScrambleBtn = (Button) findViewById(R.id.tScrambleBtn);
        tPtBtn = (Button) findViewById(R.id.tPtBtn);
        tDnfBtn = (Button) findViewById(R.id.tDnfBtn);
        tOkBtn = (Button) findViewById(R.id.tOkBtn);
        tDeleteBtn = (Button) findViewById(R.id.tDeleteBtn);
        tTimeTextView = (TextView) findViewById(R.id.tTimeTextView);
        tInspectionTextView = (TextView) findViewById(R.id.tInspectionTextView);
        tScrambleTextView = (TextView) findViewById(R.id.tScrambleTextView);
        tChronometer = (Chronometer) findViewById(R.id.tChronometer);
        tPuzzleSpinner = (Spinner) findViewById(R.id.tPuzzleSpinner);
        tImageView = (ImageView) findViewById(R.id.tImageView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.puzzle_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tPuzzleSpinner.setAdapter(adapter);

        tPuzzleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    puzzle = PuzzleRegistry.TWO;
                } else if (position == 1) {
                    puzzle = PuzzleRegistry.THREE;
                } else if (position == 2) {
                    puzzle = PuzzleRegistry.FOUR;
                } else if (position == 3) {
                    puzzle = PuzzleRegistry.FIVE;
                } else if (position == 4) {
                    puzzle = PuzzleRegistry.SIX;
                } else if (position == 5) {
                    puzzle = PuzzleRegistry.SEVEN;
                } else if (position == 6) {
                    puzzle = PuzzleRegistry.PYRA;
                } else if (position == 7) {
                    puzzle = PuzzleRegistry.SQ1;
                } else if (position == 8) {
                    puzzle = PuzzleRegistry.MEGA;
                } else if (position == 9) {
                    puzzle = PuzzleRegistry.CLOCK;
                } else if (position == 10) {
                    puzzle = PuzzleRegistry.SKEWB;
                }

                puzzleString = puzzles.get(position);

                hideBtns();
                scramble = puzzle.getScrambler().generateScramble();
                doStuff();
                /*scramble = puzzle.getScrambler().generateScramble();
                imageView.setImageDrawable(null);
                try {
                    image = puzzle.getScrambler().drawScramble(scramble, null);
                    SVG svg = SVGParser.getSVGFromString(image.toString());
                    Drawable d = new PictureDrawable(svg.getPicture());
                    imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    imageView.setImageDrawable(d);
                    LayoutParams params = (LayoutParams) imageView.getLayoutParams();

                    DisplayMetrics metrics = new DisplayMetrics();

                    activity = getActivity();
                    WindowManager wm = activity.getWindowManager();
                    Display display = wm.getDefaultDisplay();

                    display.getMetrics(metrics);

                    params.width = (int) (image.getSize().width * metrics.density);
                    params.height = (int) (image.getSize().height * metrics.density);
                    imageView.setLayoutParams(params);
                } catch (Exception e) {e.printStackTrace();}
                scrambleTextView.setText(scramble);*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        puzzle = PuzzleRegistry.TWO;

        hideBtns();
        scramble = puzzle.getScrambler().generateScramble();
        doStuff();
        /*scramble = puzzle.getScrambler().generateScramble();
        imageView.setImageDrawable(null);
        try {
            image = puzzle.getScrambler().drawScramble(scramble, null);
            SVG svg = SVGParser.getSVGFromString(image.toString());
            Drawable d = new PictureDrawable(svg.getPicture());
            imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            imageView.setImageDrawable(d);
            LayoutParams params = (LayoutParams) imageView.getLayoutParams();

            DisplayMetrics metrics = new DisplayMetrics();

            activity = getActivity();
            WindowManager wm = activity.getWindowManager();
            Display display = wm.getDefaultDisplay();

            display.getMetrics(metrics);

            params.width = (int) (image.getSize().width * metrics.density);
            params.height = (int) (image.getSize().height * metrics.density);
            imageView.setLayoutParams(params);
        } catch (Exception e) {e.printStackTrace();}
        scrambleTextView.setText(scramble);*/
        tTimeTextView.setVisibility(View.INVISIBLE);
        tInspectionTextView.setVisibility(View.INVISIBLE);
        tTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals("ready")) {
                    hideBtns();
                    status = "inspection";
                    tTimeTextView.setVisibility(View.INVISIBLE);
                    tInspectionTextView.setVisibility(View.VISIBLE);
                    tChronometer.setVisibility(View.INVISIBLE);
                    inspectionTimer = new CountDownTimer(18000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            long iTime = (millisUntilFinished / 1000) - 2;
                            if (iTime > 0) {
                                tInspectionTextView.setText(iTime + "");
                                inspectionPenalty = "OK";
                            }
                            else if (iTime > -2) {
                                tInspectionTextView.setText("+2");
                                inspectionPenalty = "+2";
                            }
                            else {
                                tInspectionTextView.setText("DNF");
                                tTimerBtn.setEnabled(false);
                                dnf = true;
                                inspectionPenalty = "DNF";
                            }
                        }

                        public void onFinish() {
                            tInspectionTextView.setText("");
                            tInspectionTextView.setVisibility(View.INVISIBLE);
                            tChronometer.setVisibility(View.VISIBLE);
                            if (dnf) {
                                tChronometer.setVisibility(View.INVISIBLE);
                                tTimeTextView.setVisibility(View.VISIBLE);
                                tTimeTextView.setText("DNF");
                                status = "ready";
                                dnf = false;
                            }
                            tTimerBtn.setEnabled(true);
                        }
                    }.start();
                } else if (status.equals("inspection")) {
                    inspectionTimer.onFinish();
                    inspectionTimer.cancel();
                    tChronometer.setBase(SystemClock.elapsedRealtime());
                    tChronometer.start();
                    startTimeMillis = System.currentTimeMillis();
                    tChronometer.setVisibility(View.VISIBLE);
                    tTimeTextView.setVisibility(View.INVISIBLE);
                    sT = new scrambleThread();
                    sT.start();
                    status = "solving";
                } else {
                    tPtBtn.setVisibility(View.VISIBLE);
                    tDnfBtn.setVisibility(View.VISIBLE);
                    tChronometer.stop();
                    time = System.currentTimeMillis() - startTimeMillis;
                    formatTime(time);
                    tChronometer.setVisibility(View.INVISIBLE);
                    tTimeTextView.setVisibility(View.VISIBLE);
                    tOkBtn.setVisibility(View.INVISIBLE);
                    tDnfBtn.setVisibility(View.VISIBLE);
                    tPtBtn.setVisibility(View.VISIBLE);
                    tDeleteBtn.setVisibility(View.VISIBLE);

                    solveRef = userRef.child(puzzleString).push();
                    solveRef.child("Time").setValue(time);
                    solveRef.child("Scramble").setValue(pastScramble);

                    if (inspectionPenalty.equals("OK")) {
                        doOK();
                    } else if (inspectionPenalty.equals("+2")) {
                        doPT();
                    } else {
                        doDNF();
                    }

                    tPtBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*formatTimePT(time);
                            solveRef.child("Penalty").setValue("+2");
                            ptButton.setVisibility(View.INVISIBLE);
                            dnfButton.setVisibility(View.INVISIBLE);
                            okButton.setVisibility(View.VISIBLE);*/
                            doPT();
                        }
                    });

                    tDnfBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*solveRef.child("Penalty").setValue("DNF");
                            textView.setText("DNF");
                            dnfButton.setVisibility(View.INVISIBLE);
                            ptButton.setVisibility(View.INVISIBLE);
                            okButton.setVisibility(View.VISIBLE);*/
                            doDNF();
                        }
                    });

                    tOkBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*solveRef.child("Penalty").setValue("OK");
                            formatTime(time);
                            okButton.setVisibility(View.INVISIBLE);
                            dnfButton.setVisibility(View.VISIBLE);
                            ptButton.setVisibility(View.VISIBLE);*/
                            doOK();
                        }
                    });

                    tDeleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                                builder.setTitle("Are you sure you want to delete this solve?");

                                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        tOkBtn.setVisibility(View.INVISIBLE);
                                        tPtBtn.setVisibility(View.INVISIBLE);
                                        tDnfBtn.setVisibility(View.INVISIBLE);
                                        tDeleteBtn.setVisibility(View.INVISIBLE);
                                        tTimeTextView.setText("0:00.000");
                                        solveRef.removeValue();
                                    }
                                });

                                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });

                                builder.create().show();
                            } catch (Exception e) {}
                        }
                    });

                    doStuff();
                    /*imageView.setImageDrawable(null);
                    try {
                        image = puzzle.getScrambler().drawScramble(scramble, null);
                        SVG svg = SVGParser.getSVGFromString(image.toString());
                        Drawable d = new PictureDrawable(svg.getPicture());
                        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        imageView.setImageDrawable(d);
                        LayoutParams params = (LayoutParams) imageView.getLayoutParams();

                        DisplayMetrics metrics = new DisplayMetrics();

                        activity = getActivity();
                        WindowManager wm = activity.getWindowManager();
                        Display display = wm.getDefaultDisplay();

                        display.getMetrics(metrics);

                        params.width = (int) (image.getSize().width * metrics.density);
                        params.height = (int) (image.getSize().height * metrics.density);
                        imageView.setLayoutParams(params);
                    } catch (Exception e) {e.printStackTrace();}
                    scrambleTextView.setText(scramble);*/
                    status = "ready";
                }
            }
        });
    }

    public void hideBtns() {
        tPtBtn.setVisibility(View.INVISIBLE);
        tDnfBtn.setVisibility(View.INVISIBLE);
        tOkBtn.setVisibility(View.INVISIBLE);
        tDeleteBtn.setVisibility(View.INVISIBLE);
    }

    public void doStuff() {
        if (scramble.equals(currentScramble)) {
            try {
                tScrambleTextView.setText("Generating scramble... ");
                tScrambleBtn.setText("Generating scramble... ");
                tScrambleBtn.setEnabled(false);
                tScrambleTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sT.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        stuff();
                    }
                });
            } catch (Exception e) {}
        } else
            stuff();
    }

    private void stuff() {
        //System.out.println(scramble.length());
        tImageView.setImageDrawable(null);
        try {
            currentScramble = scramble;
            pastScramble = scramble;
            image = puzzle.getScrambler().drawScramble(scramble, null);
            SVG svg = SVGParser.getSVGFromString(image.toString());
            Drawable d = new PictureDrawable(svg.getPicture());
            tImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            tImageView.setImageDrawable(d);
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) tImageView.getLayoutParams();

            /*DisplayMetrics metrics = new DisplayMetrics();

            activity = getActivity();
            WindowManager wm = activity.getWindowManager();
            Display display = wm.getDefaultDisplay();

            display.getMetrics(metrics);*/

            DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();

            params.width = (int) (image.getSize().width * metrics.density);
            params.height = (int) (image.getSize().height * metrics.density);
            tImageView.setLayoutParams(params);
        } catch (Exception e) {e.printStackTrace();}
        if (scramble.length() > 140) {
            tScrambleTextView.setVisibility(View.INVISIBLE);
            tScrambleBtn.setEnabled(true);
            tScrambleBtn.setText("Tap for scramble");
            tScrambleBtn.setVisibility(View.VISIBLE);
            tScrambleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        builder.setTitle("Scramble:").setMessage(scramble);

                        builder.create().show();
                    } catch (Exception e) {}
                }
            });
        } else {
            tScrambleBtn.setVisibility(View.INVISIBLE);
            tScrambleTextView.setVisibility(View.VISIBLE);
            tScrambleTextView.setText(scramble);
        }
    }

    public void formatTime(long time) {
        long seconds = (time / 1000) % 60;
        final long minutes = ((time - (1000 * seconds)) / 1000) / 60;
        long milliseconds = time % 1000;
        formatSeconds = String.format("%2s", seconds).replace(' ', '0');
        formatMilliseconds = String.format("%3s", milliseconds).replace(' ', '0');
        tDisplay = minutes + ":" + formatSeconds + "." + formatMilliseconds;
        tTimeTextView.setText(tDisplay);
    }

    public void formatTimePT(long time) {
        long seconds = ((time / 1000) % 60) + 2;
        final long minutes = ((time - (1000 * seconds)) / 1000) / 60;
        long milliseconds = time % 1000;
        formatSeconds = String.format("%2s", seconds).replace(' ', '0');
        formatMilliseconds = String.format("%3s", milliseconds).replace(' ', '0');
        tDisplay = minutes + ":" + formatSeconds + "." + formatMilliseconds + "+";
        tTimeTextView.setText(tDisplay);
    }

    public void doPT() {
        formatTimePT(time);
        solveRef.child("Penalty").setValue("+2");
        tPtBtn.setVisibility(View.INVISIBLE);
        tDnfBtn.setVisibility(View.INVISIBLE);
        tOkBtn.setVisibility(View.VISIBLE);
    }

    public void doDNF() {
        solveRef.child("Penalty").setValue("DNF");
        tTimeTextView.setText("DNF");
        tDnfBtn.setVisibility(View.INVISIBLE);
        tPtBtn.setVisibility(View.INVISIBLE);
        tOkBtn.setVisibility(View.VISIBLE);
    }

    public void doOK() {
        solveRef.child("Penalty").setValue("OK");
        formatTime(time);
        tOkBtn.setVisibility(View.INVISIBLE);
        tDnfBtn.setVisibility(View.VISIBLE);
        tPtBtn.setVisibility(View.VISIBLE);
    }

    public static Activity getActivity() throws Exception {
        Class activityThreadClass = Class.forName("android.app.ActivityThread");
        Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
        Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
        activitiesField.setAccessible(true);

        Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);
        if (activities == null)
            return null;

        for (Object activityRecord : activities.values()) {
            Class activityRecordClass = activityRecord.getClass();
            Field pausedField = activityRecordClass.getDeclaredField("paused");
            pausedField.setAccessible(true);
            if (!pausedField.getBoolean(activityRecord)) {
                Field activityField = activityRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                return activity;
            }
        }

        return null;
    }

    class scrambleThread extends Thread {
        @Override
        public void run() {
            scramble = puzzle.getScrambler().generateScramble();
        }
    }
}
