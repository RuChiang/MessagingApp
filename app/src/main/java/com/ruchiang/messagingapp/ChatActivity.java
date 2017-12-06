package com.ruchiang.messagingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.xml.sax.InputSource;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.message;

public class ChatActivity extends AppCompatActivity {

    EditText text;
    Button btn;
    ListView listView;
    String receiver;
    ArrayList<String> msgs;
    ArrayAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        text = (EditText)findViewById(R.id.editText);
        btn = (Button) findViewById(R.id.button);
        listView = (ListView) findViewById(R.id.chat);

        Intent intent = getIntent();
        receiver = intent.getStringExtra("name");
        Toolbar toolbar = (Toolbar)findViewById(R.id.bar);
        toolbar.setTitle(receiver);
        setSupportActionBar(toolbar);

        msgs = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,msgs);
        listView.setAdapter(adapter);


        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");
        query1.whereEqualTo("sender",ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("receiver", receiver);

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");
        query2.whereEqualTo("receiver",ParseUser.getCurrentUser().getUsername());
        query2.whereEqualTo("sender", receiver);

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(queries);
        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size()>0){
                    for(ParseObject o : objects){
                        String msgContent = o.getString("message");
                        if(o.getString("sender").equals(ParseUser.getCurrentUser().getUsername())){
                            msgContent = "> " + msgContent;
                        }
                        msgs.add(msgContent);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });


    }

    public void send(View view){
        String msg = text.getText().toString();

        ParseObject parseObject = new ParseObject("Message");
        parseObject.put("sender", ParseUser.getCurrentUser().getUsername());
        parseObject.put("message", msg);
        parseObject.put("receiver", receiver);
        parseObject.saveInBackground();
        text.setText("");



    }
}
