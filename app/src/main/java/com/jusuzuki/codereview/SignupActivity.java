package com.jusuzuki.codereview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {


    @Bind(R.id.newUsername)
    EditText newUsername;
    @Bind(R.id.newPassword) EditText newPassword;
    @Bind(R.id.newPasswordCheck) EditText newPasswordCheck;
    @Bind(R.id.signupButton)
    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser newUser = new ParseUser();
                newUser.setUsername(newUsername.getText().toString());

                if (newPassword.getText().toString().equals(newPasswordCheck.getText().toString())){
                    newUser.setPassword(newPassword.getText().toString());

                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                //success!
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //research what this does exactly
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "I'm sorry, there was an error. Please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(),"Sorry, passwords don't match. Please try again",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

}
