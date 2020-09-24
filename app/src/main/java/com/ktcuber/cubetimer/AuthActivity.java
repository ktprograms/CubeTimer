package com.ktcuber.cubetimer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    EditText AuthEmailEditText;
    EditText AuthPwEditText;
    Button AuthAcctBtn;
    Button AuthSignInBtn;
    Button AuthSignOutBtn;
    Button AuthHomeBtn;
    TextView AuthSignStatsTextView;
    TextView textView;

    String email;
    String password;
    boolean fromHomeDone = false;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportActionBar().hide();

        AuthEmailEditText = (EditText) findViewById(R.id.AuthEmailEditText);
        AuthPwEditText = (EditText) findViewById(R.id.AuthPwEditText);
        AuthAcctBtn = (Button) findViewById(R.id.AuthAcctBtn);
        AuthSignInBtn = (Button) findViewById(R.id.AuthSignInBtn);
        AuthSignOutBtn = (Button) findViewById(R.id.AuthSignOutBtn);
        AuthHomeBtn = (Button) findViewById(R.id.AuthHomeBtn);
        AuthSignStatsTextView = (TextView) findViewById(R.id.AuthSignStatsTextView);
        textView = (TextView) findViewById(R.id.textView);

        AuthHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authMainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(authMainIntent);
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        AuthAcctBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getInputs();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("AUTH", "createUserWithEmail:success");
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                updateUI(currentUser);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("AUTH", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(AuthActivity.this, "Authentication failed : " + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        AuthSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getInputs();
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("AUTH", "signInUserWithEmail:success");
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                updateUI(currentUser);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("AUTH", "signInUserWithEmail:failure", task.getException());
                                Toast.makeText(AuthActivity.this, "Authentication failed : " + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        AuthSignOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mAuth.signOut();
                    updateUI(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            AuthAcctBtn.setVisibility(View.INVISIBLE);
            AuthSignInBtn.setVisibility(View.INVISIBLE);
            AuthSignOutBtn.setVisibility(View.VISIBLE);
            textView.setVisibility(View.INVISIBLE);
            AuthSignStatsTextView.setText("Signed in");
            AuthEmailEditText.setText("");
            AuthPwEditText.setText("");
        } else {
            AuthAcctBtn.setVisibility(View.VISIBLE);
            AuthSignInBtn.setVisibility(View.VISIBLE);
            AuthSignOutBtn.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
            AuthSignStatsTextView.setText("Signed out");
            if (getIntent().hasExtra("com.example.timerapp.AUTHFROMMAIN") && !fromHomeDone) {
                fromHomeDone = true;
                AuthSignStatsTextView.setText("To continue to the timer app, please create an account or sign in, then click the 'BACK TO HOME' button");
            }
        }
    }

    public void getInputs() {
        email = AuthEmailEditText.getText().toString();
        password = AuthPwEditText.getText().toString();
    }
}
