package com.proj.safe_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.proj.safe_chat.adapter.AdapterUsers;
import com.proj.safe_chat.roomsql.Message;
import com.proj.safe_chat.roomsql.NoteViewModel;
import com.proj.safe_chat.tools.KeysJsonI;
import com.proj.safe_chat.tools.MySocket;
import com.proj.safe_chat.tools.MySocketSingleton;
import com.proj.safe_chat.tools.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//מסך ראשי
public class MainActivity extends AppCompatActivity implements KeysJsonI {
    private MySocket mySocket;
    private User user;
    private RecyclerView recyclerView;
    private List<String> names = new ArrayList<>();
    private List<String> uids = new ArrayList<>();
    private List<String> emails = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<String> tokens = new ArrayList<>();
    private NoteViewModel noteViewModel;
    private final String TAG = getClass().getName();

    //נקרא כאשר נוצר האקטיביטי
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.list_participants);
        Log.d("TAG", "recyclerView: "+recyclerView);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        mySocket = MySocketSingleton.getMySocket();
        mySocket.setContext(this);

        String myName = getIntent().getStringExtra("name");
        String myEmail = getIntent().getStringExtra("email");
        String myUnique_id = getIntent().getStringExtra("unique_id");

        user = new User(myName, myUnique_id, myEmail, "");

        try {
            getTokenMessaging();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (mySocket != null && mySocket.isChanged()) {
                        byte[] bytes = mySocket.readLast();
                        try {
                            Log.d("TAG", "bytesMain: " + new String(bytes));
                            receive(bytes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mySocket.setChanged(false);
                    }
                }
            }
        };
        thread.start();
    }

    //דואג לקבל TOKEN להתראות
    private void getTokenMessaging() throws JSONException {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        String token = "";
                        if (!task.isSuccessful()) {
                            token = "NOT_DEFINE";
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                        }

                        // Get new FCM registration token
                        if(!token.equals("NOT_DEFINE"))
                            token = task.getResult();

                        // Log and toast
                        sendGetAllRequest(token);
                        Log.d("TAG", "token: " + token);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailureToken: ");
                sendGetAllRequest("NOT_DEFINE");
            }
        });
    }
    //שולח בקשה לקבל את המשתמשים איתם ניתן לדבר
    private void sendGetAllRequest(String token){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE_KEY, GET_ALL_VALUE);
            jsonObject.put(UID_KEY, user.getUnique_id());
            jsonObject.put(TOKEN_KEY, token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    mySocket.send(jsonObject.toString().getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    //בודק האם MYSOCKET קיבל מידע חדש ואם זה רלוונטי אליו
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

            singleStr = jsonObject.getString("tokens")
                    .substring(1, jsonObject.getString("tokens").length() - 1);
            str = singleStr.split(",");
            tokens = Arrays.asList(str);

            Log.d("TAG", "uids: " + uids.size());
            for (int i = 0; i < uids.size(); i++) {
                try {
                    String s = uids.get(i).substring(1, uids.get(i).length() - 1);
                    uids.set(i, s);
                    s = names.get(i).substring(1, names.get(i).length() - 1);
                    names.set(i, s);
                    s = emails.get(i).substring(1, emails.get(i).length() - 1);
                    emails.set(i, s);
                    s = tokens.get(i).substring(1, tokens.get(i).length() - 1);
                    tokens.set(i, s);
                    users.add(new User(names.get(i), uids.get(i), emails.get(i), tokens.get(i)));
                } catch (Exception e) {
                    uids = new ArrayList<>();
                    break;
                }
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    AdapterUsers adapterUsers = new AdapterUsers(MainActivity.this, users
                            , getIntent().getExtras().getString("unique_id"));
                    recyclerView.setAdapter(adapterUsers);
                    adapterUsers.setOnItemClickListener(new AdapterUsers.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            MySocketSingleton.setMySocket(mySocket);
                            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                            intent.putExtra("my_name", user.getName());
                            intent.putExtra("my_unique_id", user.getUnique_id());
                            intent.putExtra("my_email", user.getEmail());
                            intent.putExtra("other_name", users.get(position).getName());
                            intent.putExtra("other_unique_id", users.get(position).getUnique_id());
                            intent.putExtra("other_email", users.get(position).getEmail());
                            intent.putExtra("other_token", users.get(position).getToken());
                            startActivity(intent);
                        }
                    });
                    Log.d("TAG", "user.getUnique_id(): "+user.getUnique_id());
                    noteViewModel.getLastMessageOfAll(user.getUnique_id())
                            .observe(MainActivity.this, new Observer<Message>() {
                        @Override
                        public void onChanged(Message message) {
                            if (message == null)
                                return;
                            Log.d("TAG", "onChanged3456788: ");
                            int position = 0;
                            for (int i = 0; i < users.size(); i++) {
                                if (users.get(i).getUnique_id().equals(message.getToId())
                                        || users.get(i).getUnique_id().equals(message.getFromId())) {
                                    position = i;
                                    break;
                                }
                            }
                            if (position != 0 && position < users.size()) {
                                for (int i = position; i > 0; i--) {
                                    Collections.swap(users, i, i - 1);
                                    adapterUsers.notifyItemMoved(i, i - 1);
                                    adapterUsers.notifyItemChanged(i, Boolean.FALSE);
                                    adapterUsers.notifyItemChanged(i - 1, Boolean.FALSE);
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    //לMENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //מאזין אם אחד האפשרויות בMENU נלחץ
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                intent.putExtra("uid", user.getUnique_id());
                intent.putExtra("name", user.getName());
                intent.putExtra("email", user.getEmail());
                startActivityForResult(intent, 103);
                return true;
            case R.id.logout:
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(TYPE_KEY, LOGOUT);
                    jsonObject.put(UID_KEY, user.getUnique_id());
                    jsonObject.put(TOKEN_KEY, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            mySocket.send(jsonObject.toString().getBytes());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };thread.start();
                noteViewModel.deleteAllNotesUser();
                startActivity(new Intent(MainActivity.this,StartActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //מקבל Result מאקטיביטי אחר
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 103) {
            if(resultCode == RESULT_OK) {
                try {
                    String newName = data.getStringExtra("newName");
                    Log.d(TAG, "newName: "+newName);
                    if(!newName.trim().equals(""))
                        user.setName(newName);
                    String newEmail = data.getStringExtra("newEmail");
                    if(!newEmail.trim().equals(""))
                        user.setEmail(newEmail);
                }catch (Exception e){}
            }
        }
    }
}