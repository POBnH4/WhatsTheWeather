package com.example.peterboncheff.coursework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.peterboncheff.coursework.UsersDatabase.users;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registerButton, cancelButton;
    private EditText usernameField, passwordField, confirmPasswordField, emailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activitiy);
        setVariables();
    }

    private void setVariables(){
        this.usernameField = findViewById(R.id.usernameField);
        this.passwordField = findViewById(R.id.passwordField);
        this.confirmPasswordField = findViewById(R.id.confirmPasswordField);
        this.emailField = findViewById(R.id.emailField);

        this.registerButton = findViewById(R.id.registerButton);
        this.cancelButton = findViewById(R.id.cancelButton);

        this.registerButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == this.registerButton.getId()){
            final String PASSWORD_MISMATCH = "Passwords do not match!";
            if(checkUsernameAndPassword()) register();
            else Toast.makeText(this,PASSWORD_MISMATCH,Toast.LENGTH_SHORT).show();
        }
        if(v.getId() == this.cancelButton.getId()) cancelRegister();
    }

    private void cancelRegister() {
        final Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void register() {
        users.add(new User(this.usernameField.getText().toString(),
                           this.passwordField.getText().toString(),
                           this.emailField.getText().toString()));

        final Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    private boolean checkUsernameAndPassword(){
        return passwordField.getText().toString().equals(confirmPasswordField.getText().toString());
    }
}
