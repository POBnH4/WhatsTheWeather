package com.example.peterboncheff.coursework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;

import static com.example.peterboncheff.coursework.UsersDatabase.users;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, Serializable {

    static final String USE_CURRENT_USER_DATA = "Current User";

    private Button registerButton, signInButton;
    private EditText usernameField, passwordField;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setVariables();
    }

    private void setVariables(){
        this.registerButton = findViewById(R.id.registerButton);
        this.signInButton = findViewById(R.id.signInButton);
        this.usernameField = findViewById(R.id.usernameField);
        this.passwordField = findViewById(R.id.passwordField);

        this.registerButton.setOnClickListener(this);
        this.signInButton.setOnClickListener(this);

        //default currentUser for testing purposes;
        final String DEFAULT_USERNAME = "username", DEFAULT_USER_PASSWORD = "password", DEFAULT_USER_EMAIL = "currentUser@currentUser.com";
        users.add(new User(DEFAULT_USERNAME, DEFAULT_USER_PASSWORD, DEFAULT_USER_EMAIL));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == registerButton.getId())  register();
        if(v.getId() == signInButton.getId()) signIn();
    }

    private void register(){
        final Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    private void signIn(){
        if(checkInDatabaseForUser()){
            final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(USE_CURRENT_USER_DATA, currentUser.getUsername());
            intent.putExtra(USE_CURRENT_USER_DATA, currentUser.getPassword());
            startActivity(intent);
        }else {
            final String INCORRECT = "Incorrect username or password", EMPTY_STRING = "";
            Toast.makeText(this, INCORRECT, Toast.LENGTH_SHORT).show();
            this.passwordField.setText(EMPTY_STRING);
        }
    }

    private boolean checkInDatabaseForUser(){
        for (User user : users) {
            String username = this.usernameField.getText().toString();
            String password = this.passwordField.getText().toString();
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                Log.d("hallo", username + " " + password + " tried to log in");
                this.currentUser = user;
                return true;
            }
        }
        return false;
    }
}
