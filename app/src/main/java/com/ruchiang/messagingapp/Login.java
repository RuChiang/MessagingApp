package com.ruchiang.messagingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;

public class Login extends AppCompatActivity implements View.OnClickListener {

    EditText id;
    EditText password;
    EditText email;
    Button btn;
    TextView text;
    View layout;

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        //getCurrentFocus() gives the view under current focus

    }

    public void goToUserListActivity(){
        Intent intent = new Intent(this, UserList.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("b6a334a7155e0a4b6354556dbd179e185ee4e5c8")
                .server("http://52.15.113.236:80/parse/")
                .build()
        );

        if(ParseUser.getCurrentUser() != null){
            ParseUser.getCurrentUser().put("Active", false);
            //ParseUser.getCurrentUser().put("lastLogoutTime", new Date().);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                     ParseUser.logOut();
                    }else{
                        Toast.makeText(getApplicationContext(),"failed to log the user out", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }



        layout = findViewById(R.id.layout);
        id = (EditText)findViewById(R.id.Id);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
        btn = (Button)findViewById(R.id.button);
        text = (TextView) findViewById(R.id.textView);
        layout.setOnClickListener(this);
        email.setVisibility(View.INVISIBLE);

        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(email.getVisibility() == View.INVISIBLE){
                    if( keyCode == KEYCODE_ENTER && event.getAction() == ACTION_DOWN){
                        btnclick(btn);
                    }
                }
                return true;
            }
        });

        email.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KEYCODE_ENTER && event.getAction() == ACTION_DOWN){
                    btnclick(btn);

                }
                return true;
            }
        });



        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swtichTextForBtnAndText();
            }
        });

    }

    public void swtichTextForBtnAndText(){
        String tmp = text.getText().toString().split(" ")[1];
        if(tmp.equals("login")){
            email.setVisibility(View.INVISIBLE);
        }else{
            email.setVisibility(View.VISIBLE);
        }
        text.setText("or " + btn.getText());
        btn.setText(tmp);
    }

    public void btnclick(View view){

        if(btn.getText().toString().equals("login")){
            if(!(Pattern.matches("\\s*",id.getText()) || Pattern.matches("\\s*",password.getText()))) {
                //Toast.makeText(this,"I will log you in", Toast.LENGTH_SHORT).show();
                ParseUser.logInInBackground(id.getText().toString(), password.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(e == null){
                            user.put("Active", true);
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null){
                                        Log.i("INFO","log in successful");
                                        goToUserListActivity();
                                    }else{
                                        Toast.makeText(getApplicationContext(),e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(),e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                Toast.makeText(this,"invalid input", Toast.LENGTH_SHORT).show();
            }
        }else{
            if(!(Pattern.matches("\\s*",id.getText()) || Pattern.matches("\\s*",password.getText()) || Pattern.matches("\\s*",email.getText()))) {
                //Toast.makeText(this,"I will sign you up", Toast.LENGTH_SHORT).show();
                ParseUser user = new ParseUser();
                user.setUsername(id.getText().toString());
                user.setPassword(password.getText().toString());
                user.setEmail(email.getText().toString());
                user.put("Active", true);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Log.i("INFO", "signupsuccessful");
                            goToUserListActivity();
                        }else{
                            Toast.makeText(getApplicationContext(),e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                Toast.makeText(this,"invalid input", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
