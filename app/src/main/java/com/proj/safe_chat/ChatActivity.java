package com.proj.safe_chat;

import static com.proj.safe_chat.tools.MySocket.idChat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.proj.safe_chat.adapter.AdapterMessage;
import com.proj.safe_chat.firebase_noti.Data;
import com.proj.safe_chat.firebase_noti.SendNotification;
import com.proj.safe_chat.roomsql.Message;
import com.proj.safe_chat.roomsql.NoteViewModel;
import com.proj.safe_chat.tools.KeysJsonI;
import com.proj.safe_chat.tools.MySocket;
import com.proj.safe_chat.tools.MySocketSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import static com.proj.safe_chat.firebase_noti.MyFirebaseMessaginig.activeChat;
import static com.proj.safe_chat.firebase_noti.MyFirebaseMessaginig.idUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements KeysJsonI {
    private TextView textUsername;
    private EditText editTextChat;
    private ImageButton btnSend;
    private MySocket mySocket;
    private NoteViewModel noteViewModel;
    private List<Boolean> bDates = new ArrayList<>();
    private CircleImageView profileImage;
    private RecyclerView recyclerView;
    private AdapterMessage adapterMessage;
    private List<Message>messages=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        textUsername = findViewById(R.id.username);
        btnSend = findViewById(R.id.btn_send);
        recyclerView=findViewById(R.id.recyclerView);
        editTextChat=findViewById(R.id.text_send);
        profileImage=findViewById(R.id.profile_image);

        String my_uid = getIntent().getStringExtra("my_unique_id");
        String o_uid = getIntent().getStringExtra("other_unique_id");

        noteViewModel= ViewModelProviders.of(this).get(NoteViewModel.class);

        textUsername.setText(getIntent().getStringExtra("other_name"));

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapterMessage=new AdapterMessage(this,messages,my_uid);
        recyclerView.setAdapter(adapterMessage);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mySocket = MySocketSingleton.getMySocket();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mySocket.getProfileImage(o_uid, new MySocket.OnReceiveImage() {
                    @Override
                    public void OnReceive(Bitmap bitmap) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                profileImage.setImageBitmap(bitmap);
                            }
                        });
                    }
                });
            }
        });thread.start();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        noteViewModel.getAllMessages(my_uid, o_uid).observe(ChatActivity.this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                Log.d("TAG", "onChanged: ");
                bDates=new ArrayList<>();
                for(int i=0;i<messages.size();i++){
                    if(i==0){
                        bDates.add(true);
                    }else{
                        SimpleDateFormat formatDate = new SimpleDateFormat("MMM dd EE");
                        String strDate1 = formatDate.format(messages.get(i-1).getTime());
                        String strDate2 = formatDate.format(messages.get(i).getTime());
                        if(!strDate1.equals(strDate2)){
                            bDates.add(true);
                        }else{
                            bDates.add(false);
                        }
                    }
                    if(!messages.get(i).isShow()){
                        messages.get(i).setShow(true);
                        noteViewModel.updateMessage(messages.get(i));
                    }
                }
                Log.d("TAG", "bDates: "+bDates);
                // TODO: 8/16/2021 create AdapterMessage
                adapterMessage.setBDates(bDates);
                adapterMessage.setMessages(messages);
                recyclerView.scrollToPosition(messages.size() - 1);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String message = editTextChat.getText().toString();
                    if(!message.trim().equals("")) {
                        long time=System.currentTimeMillis();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(TYPE_KEY, MESSAGE_VALUE);
                        jsonObject.put(FROM_KEY, getIntent().getStringExtra("my_unique_id"));
                        jsonObject.put(TO_KEY, o_uid);
                        jsonObject.put(BODY_KEY,message);
                        jsonObject.put(TIME_KEY,time);
                        editTextChat.setText("");

                        noteViewModel.insertMessage(new Message(message,o_uid, my_uid,true, time));

                        Thread thread = new Thread(){
                            @Override
                            public void run(){
                                try {
                                    mySocket.send(jsonObject.toString().getBytes());
                                    Data data = new Data(jsonObject.getString("type")
                                            , jsonObject.getString("from"), jsonObject.getString("to")
                                            ,getIntent().getStringExtra("my_name"));
                                    String other_token = getIntent().getStringExtra("other_token");
                                    new SendNotification(other_token, data).sendNotification();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };thread.start();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        idChat=getIntent().getExtras().getString("other_unique_id");
        idUser=getIntent().getExtras().getString("other_unique_id");
        activeChat=true;
        Log.d("TAG", "idChat: "+idChat);
    }

    @Override
    protected void onStop() {
        super.onStop();
        activeChat=false;
        idChat="";
    }
}