package com.proj.safe_chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.proj.safe_chat.adapter.AdapterUsers;
import com.proj.safe_chat.tools.KeysJsonI;
import com.proj.safe_chat.tools.MySocket;
import com.proj.safe_chat.tools.MySocketSingleton;
import com.proj.safe_chat.tools.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements KeysJsonI {
    private MySocket mySocket;
    private User user;
    private RecyclerView recyclerView;
    private List<String> names=new ArrayList<>();
    private List<String> uids=new ArrayList<>();
    private List<String> emails=new ArrayList<>();
    private List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.list_participants);
        Log.d("TAG", "recyclerView: "+recyclerView);

        mySocket = MySocketSingleton.getMySocket();

        String myName = getIntent().getStringExtra("name");
        String myEmail = getIntent().getStringExtra("email");
        String myUnique_id = getIntent().getStringExtra("unique_id");

        user = new User(myName, myUnique_id, myEmail, "");

        try {
            sendGetAllRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(){
            @Override
            public void run(){
                while(true){
                    if(mySocket!=null&&mySocket.isChanged()){
                        byte[] bytes = mySocket.readLast();
                        try {
                            Log.d("TAG", "bytesMain: "+new String(bytes));
                            receive(bytes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mySocket.setChanged(false);
                    }
                }
            }
        };thread.start();
    }

    private void sendGetAllRequest() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TYPE_KEY, GET_ALL_VALUE);
        jsonObject.put(UID_KEY, user.getUnique_id());
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    mySocket.send(jsonObject.toString().getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };thread.start();
    }

    private void receive(byte[] bytes) throws JSONException {
        String result = new String(bytes);
        JSONObject jsonObject = null;
        if (mySocket.isJson(result))
            jsonObject = new JSONObject(result);

        assert jsonObject != null;
        if (jsonObject.get(TYPE_KEY).equals(GET_ALL_VALUE)) {
            String singleStr = jsonObject.getString("uids")
                    .substring(1, jsonObject.getString("uids").length() - 1);
            String[] str = singleStr.split(",");
            uids = Arrays.asList(str);

            singleStr = jsonObject.getString("names")
                    .substring(1, jsonObject.getString("names").length() - 1);
            str = singleStr.split(",");
            names = Arrays.asList(str);

            singleStr = jsonObject.getString("emails")
                    .substring(1, jsonObject.getString("emails").length() - 1);
            str = singleStr.split(",");
            emails = Arrays.asList(str);

            Log.d("TAG", "uids: " + uids.size());
            for (int i = 0; i < uids.size(); i++) {
                try {
                    String s = uids.get(i).substring(1, uids.get(i).length() - 1);
                    uids.set(i, s);
                    s = names.get(i).substring(1, names.get(i).length() - 1);
                    names.set(i, s);
                    s = emails.get(i).substring(1, emails.get(i).length() - 1);
                    emails.set(i, s);
                    users.add(new User(names.get(i), uids.get(i), emails.get(i), ""));
                } catch (Exception e) {
                    uids = new ArrayList<>();
                    break;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        AdapterUsers adapterUsers = new AdapterUsers(MainActivity.this, users);
                        recyclerView.setAdapter(adapterUsers);
                        adapterUsers.setOnItemClickListener(new AdapterUsers.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                MySocketSingleton.setMySocket(mySocket);
                                Intent intent=new Intent(MainActivity.this,ChatActivity.class);
                                intent.putExtra("my_name", user.getName());
                                intent.putExtra("my_unique_id", user.getUnique_id());
                                intent.putExtra("my_email", user.getEmail());
                                intent.putExtra("other_name",users.get(position).getName());
                                intent.putExtra("other_unique_id",users.get(position).getUnique_id());
                                intent.putExtra("other_email",users.get(position).getEmail());
                                startActivity(intent);
                            }
                        });
                    }
                });

            }
        }
    }
}