package com.ktcuber.cubetimer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Button timerBtn;
    Button solvesBtn;
    Button statsBtn;
    Button authBtn;

    Intent mainAuthIntent;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("com.example.TimerApp", MODE_PRIVATE);

        mainAuthIntent = new Intent(getApplicationContext(), AuthActivity.class);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            mainAuthIntent.putExtra("com.example.timerapp.AUTHFROMMAIN", "");
            startActivity(mainAuthIntent);
        }

        timerBtn = (Button) findViewById(R.id.timerBtn);
        timerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainTimerIntent = new Intent(getApplicationContext(), TimerActivity.class);
                startActivity(mainTimerIntent);
            }
        });

        solvesBtn = (Button) findViewById(R.id.solvesBtn);
        solvesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainSolvesIntent = new Intent(getApplicationContext(), SolvesActivity.class);
                startActivity(mainSolvesIntent);
            }
        });

        statsBtn = (Button) findViewById(R.id.statsBtn);
        statsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainStatsIntent = new Intent(getApplicationContext(), StatsActivity.class);
                startActivity(mainStatsIntent);
            }
        });

        authBtn = (Button) findViewById(R.id.authBtn);
        authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mainAuthIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            prefs.edit().putBoolean("firstrun", false).commit();
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}
