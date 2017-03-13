package com.example.alcanzer.instagramdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{
    EditText username, password;
    Boolean signUpActive = true;
    Button signUpBtn;
    TextView mTextView;
    RelativeLayout relativeLayout;
    ParseUser in;
    ArrayList<ParseObject> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = (EditText) findViewById(R.id.usernameText);
        password = (EditText) findViewById(R.id.passwordText);
        signUpBtn = (Button) findViewById(R.id.Signup);
        mTextView = (TextView) findViewById(R.id.login);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        mTextView.setOnClickListener(this);
        password.setOnKeyListener(this);
        arrayList = new ArrayList<>();
        relativeLayout.setOnClickListener(this);
        if(ParseUser.getCurrentUser() != null){
            checkUser();
        }
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    private void checkUser() {
            Intent intent = new Intent(this, UserListAct.class);
            startActivity(intent);

        }


    public void signUp(View v){
        if (username.getText().toString() == "" || password.getText().toString() == ""){
            Toast.makeText(getApplicationContext(), "Username or Password is required", Toast.LENGTH_SHORT).show();
        }else {
            if (signUpActive) {
                ParseUser user = new ParseUser();
                user.setUsername(username.getText().toString());
                user.setPassword(password.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "Signed up successfully", Toast.LENGTH_SHORT).show();
                            checkUser();
                        } else {
                            Log.i("SignUpError", e.getMessage());
                        }
                    }
                });
            }else{
                ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(user != null){
                            Toast.makeText(getApplicationContext(), user + " logged in!", Toast.LENGTH_SHORT).show();
                            getMembers();
                            checkUser();

                        }
                        else{
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }
    }

    private void getMembers() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ExampleObject");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    for(ParseObject object : objects){
                        Log.i("Users", object.toString());
                        arrayList.add(object);
                    }
                }else{
                    Log.i("Error", e.getMessage());
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login){
            if(signUpActive){
                signUpActive = false;
                signUpBtn.setText("Login");
                mTextView.setText("Sign Up!");

            }else{
                signUpActive = true;
                signUpBtn.setText("Sign Up");
                mTextView.setText("Login");
            }
        } else if(v.getId() == R.id.relativeLayout){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
            signUp(v);
        }
        return false;
    }
}
