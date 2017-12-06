package com.ruchiang.messagingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseSession;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserList extends AppCompatActivity {

    ListView listView;


    SimpleAdapter simpleAdapter;

    ArrayList<HashMap<String, String>> UserInfo;
    ArrayList<String> activeUsers;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);




        Toolbar toolbar = (Toolbar)findViewById(R.id.include);
        setSupportActionBar(toolbar);

        activeUsers = new ArrayList<>();
        UserInfo = new ArrayList<>();

        listView = (ListView)findViewById(R.id.listview);

        simpleAdapter = new SimpleAdapter(this, UserInfo, R.layout.list_item, new String[]{"name","status"}, new int[]{R.id.name,R.id.status});
        listView.setAdapter(simpleAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = UserInfo.get(position).get("name");
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        //query.whereEqualTo("Active", true);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null && objects.size()> 0){
                    for(ParseUser u : objects){

                        //activeUsers.add(u.getUsername());

                        HashMap<String, String> entry = new HashMap();
                        entry.put("name", u.getUsername().toString());
                        entry.put("status", u.getString("Status"));
                        UserInfo.add(entry);

                        if(u.getBoolean("Active") == true){
                            activeUsers.add(u.getUsername());
                        }
                        Log.i("activeUsers",activeUsers.toString());

                    }
                    simpleAdapter.notifyDataSetChanged();
                    Log.i("INFO","update list done");



                }
            }
        });



    }



}
