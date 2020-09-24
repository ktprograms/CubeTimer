package com.ktcuber.cubetimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Map;

public class SolveActivity extends AppCompatActivity {

    String time;
    String scramble;
    String penalty;
    String puzzleString;
    String id;
    String uid;

    TextView timeTextView1;
    TextView scrambleTextView1;
    TextView penaltyTextView1;
    ImageView scrambleImageView1;

    Button sOkBtn;
    Button sPtBtn;
    Button sDnfBtn;
    Button sDeleteBtn;

    Svg image;
    PuzzleRegistry puzzle;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference solveRef;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve);
        getSupportActionBar().hide();

        timeTextView1 = (TextView) findViewById(R.id.sTimeTextView);
        scrambleTextView1 = (TextView) findViewById(R.id.sScrambleTextView);
        penaltyTextView1 = (TextView) findViewById(R.id.sPenaltyTextView);
        scrambleImageView1 = (ImageView) findViewById(R.id.sScrambleImageView);

        sOkBtn = (Button) findViewById(R.id.sOkBtn);
        sPtBtn = (Button) findViewById(R.id.sPtBtn);
        sDnfBtn = (Button) findViewById(R.id.sDnfBtn);
        sDeleteBtn = (Button) findViewById(R.id.sDeleteBtn);

        sOkBtn.setVisibility(View.INVISIBLE);
        sPtBtn.setVisibility(View.INVISIBLE);
        sDnfBtn.setVisibility(View.INVISIBLE);

        if (getIntent().hasExtra("com.example.timerapp.TIME") &&
                getIntent().hasExtra("com.example.timerapp.SCRAMBLE") &&
                getIntent().hasExtra("com.example.timerapp.PENALTY") &&
                getIntent().hasExtra("com.example.timerapp.PUZZLE") &&
                getIntent().hasExtra("com.example.timerapp.ID")) {
            time = getIntent().getStringExtra("com.example.timerapp.TIME");
            scramble = getIntent().getStringExtra("com.example.timerapp.SCRAMBLE");
            penalty = getIntent().getStringExtra("com.example.timerapp.PENALTY");
            puzzleString = getIntent().getStringExtra("com.example.timerapp.PUZZLE");
            id = getIntent().getStringExtra("com.example.timerapp.ID");

            uid = user.getUid();

            solveRef = firebaseDatabase.getReference().child(uid).child(puzzleString).child(id);

            if (puzzleString.equals("Two")) {
                puzzle = PuzzleRegistry.TWO;
            } else if (puzzleString.equals("Three")) {
                puzzle = PuzzleRegistry.THREE;
            } else if (puzzleString.equals("Four")) {
                puzzle = PuzzleRegistry.FOUR;
            } else if (puzzleString.equals("Five")) {
                puzzle = PuzzleRegistry.FIVE;
            } else if (puzzleString.equals("Six")) {
                puzzle = PuzzleRegistry.SIX;
            } else if (puzzleString.equals("Seven")) {
                puzzle = PuzzleRegistry.SEVEN;
            } else if (puzzleString.equals("Pyra")) {
                puzzle = PuzzleRegistry.PYRA;
            } else if (puzzleString.equals("Squan")) {
                puzzle = PuzzleRegistry.SQ1;
            } else if (puzzleString.equals("Mega")) {
                puzzle = PuzzleRegistry.MEGA;
            } else if (puzzleString.equals("Clock")) {
                puzzle = PuzzleRegistry.CLOCK;
            } else if (puzzleString.equals("Skewb")) {
                puzzle = PuzzleRegistry.SKEWB;
            }

            scrambleTextView1.setText(scramble);
            penaltyTextView1.setText(penalty);

            if (penalty.equals("OK")) {
                timeTextView1.setText(formatTime(Long.parseLong(time)));
                prepOk();
            } else if (penalty.equals("+2")) {
                timeTextView1.setText(formatTimePT(Long.parseLong(time)));
                prepPt();
            } else {
                timeTextView1.setText("DNF(" + formatTime(Long.parseLong(time)) + ")");
                prepDnf();
            }

            sOkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    penaltyTextView1.setText("OK");
                    timeTextView1.setText(formatTime(Long.parseLong(time)));
                    solveRef.child("Penalty").setValue("OK");
                    prepOk();
                }
            });

            sPtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    penaltyTextView1.setText("+2");
                    timeTextView1.setText(formatTimePT(Long.parseLong(time)));
                    solveRef.child("Penalty").setValue("+2");
                    prepPt();
                }
            });

            sDnfBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    penaltyTextView1.setText("");
                    timeTextView1.setText("DNF(" + formatTime(Long.parseLong(time)) + ")");
                    solveRef.child("Penalty").setValue("DNF");
                    prepDnf();
                }
            });

            sDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        builder.setTitle("Are you sure you want to delete this solve?");

                        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                solveRef.removeValue();
                                setResult(Activity.RESULT_OK, new Intent().putExtra("com.example.timerapp.SOLVEPUZZLE", puzzleString));
                                finish();
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

            doScramble();
        }
    }

    public void doScramble() {
        scrambleImageView1.setImageDrawable(null);
        try {
            image = puzzle.getScrambler().drawScramble(scramble, null);
            SVG svg = SVGParser.getSVGFromString(image.toString());
            Drawable d = new PictureDrawable(svg.getPicture());
            scrambleImageView1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            scrambleImageView1.setImageDrawable(d);
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) scrambleImageView1.getLayoutParams();

            /*DisplayMetrics metrics = new DisplayMetrics();

            activity = getActivity();
            WindowManager wm = activity.getWindowManager();
            Display display = wm.getDefaultDisplay();

            display.getMetrics(metrics);*/

            DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();

            params.width = (int) (image.getSize().width * metrics.density);
            params.height = (int) (image.getSize().height * metrics.density);
            scrambleImageView1.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prepOk() {
        sOkBtn.setVisibility(View.INVISIBLE);
        sPtBtn.setVisibility(View.VISIBLE);
        sDnfBtn.setVisibility(View.VISIBLE);
    }

    public void prepPt() {
        sPtBtn.setVisibility(View.INVISIBLE);
        sOkBtn.setVisibility(View.VISIBLE);
        sDnfBtn.setVisibility(View.VISIBLE);
    }

    public void prepDnf() {
        penaltyTextView1.setText("");
        sDnfBtn.setVisibility(View.INVISIBLE);
        sOkBtn.setVisibility(View.VISIBLE);
        sPtBtn.setVisibility(View.VISIBLE);
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

    public String formatTimePT(long time) {
        long seconds = ((time / 1000) % 60) + 2;
        final long minutes = ((time - (1000 * seconds)) / 1000) / 60;
        long milliseconds = time % 1000;
        String formatSeconds = String.format("%2s", seconds).replace(' ', '0');
        String formatMilliseconds = String.format("%3s", milliseconds).replace(' ', '0');
        String tDisplay = minutes + ":" + formatSeconds + "." + formatMilliseconds + "+";
        return tDisplay;
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
}
