package com.example.joan.meep_upscheduleapp;

/*
    Code had been reuse from The Complete Android-N-Developer Course
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    int mSignUp = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Meet-Up");

        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordEditText.setOnKeyListener(this);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        layout.setOnClickListener(this);
        imageView.setOnClickListener(this);

        if (ParseUser.getCurrentUser() != null) {
            //TODO: if logged in already
        }

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            signUp(view);
        }

        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageView || view.getId() == R.id.activity_main) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void change(View view) {

        TextView changeTextView = (TextView) findViewById(R.id.changeModeTextView);
        Button signupButton = (Button) findViewById(R.id.signupButton);

        switch (mSignUp) {
            case 1 :
                signupButton.setText("Login");
                changeTextView.setText("Or Sign Up");
                break;
            case 0:
                signupButton.setText("Sign Up");
                changeTextView.setText("Or Login");
        }

        mSignUp = (mSignUp + 1) % 2;

    }

    public void signUp(View view) {
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        if (usernameEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("")) {
            Toast.makeText(this, "Both username and password are required!", Toast.LENGTH_SHORT).show();
        } else {
            switch (mSignUp) {
                case 1:
                    ParseUser user = new ParseUser();
                    user.setUsername(usernameEditText.getText().toString());
                    user.setPassword(passwordEditText.getText().toString());
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i("Sign Up", "Successful");
                                //TODO: logged in
                            } else {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    break;
                case 0:
                    ParseUser.logInInBackground(
                            usernameEditText.getText().toString()
                            , passwordEditText.getText().toString()
                            , new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException e) {
                                    if (user != null) {
                                        Log.i("Login", "Successful");
                                        //TODO: logged in
                                    } else {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    break;
                default: break;
            }
        }
    }
}
