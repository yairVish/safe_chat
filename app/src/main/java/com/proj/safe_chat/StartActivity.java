package com.proj.safe_chat;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.proj.safe_chat.roomsql.NoteUser;
import com.proj.safe_chat.roomsql.NoteViewModel;
import com.proj.safe_chat.tools.KeysJsonI;
import com.proj.safe_chat.tools.MySocket;
import com.proj.safe_chat.tools.MySocketSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StartActivity extends AppCompatActivity implements KeysJsonI {
    private MySocket mySocket = null;
    private Context context;
    private EditText editName, editPassword;
    private Button btnSignIn, btnSignUp;
    private NoteViewModel noteViewModel;

    @Override
    protected void onStart(){
        mySocket = MySocketSingleton.getMySocket();
        if(mySocket!=null)
            mySocket.setContext(this);
        Log.d("TAG", "start: ");
        super.onStart();

        Thread thread = new Thread(){
            @Override
            public void run(){
                noteViewModel = ViewModelProviders.of(StartActivity.this).get(NoteViewModel.class);
                NoteUser noteUser = noteViewModel.getNoteUser();
                try {
                    Socket socket = new Socket("192.168.5.59", 9786);
                    mySocket = new MySocket(socket, StartActivity.this);
                    mySocket.listen();
                    if(noteUser!=null){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(TYPE_KEY, SIGN_IN_SEND_VALUE);
                        jsonObject.put(AUTO_SIGN_IN_KEY, false);
                        jsonObject.put(EMAIL_KEY, noteUser.getEmail());
                        jsonObject.put(PASSWORD_KEY, noteUser.getPassword());
                        mySocket.send(jsonObject.toString().getBytes());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(context)
                                        .setTitle("Connection Error")
                                        .setCancelable(false)
                                        .setMessage("Lost connection to the server. Try again.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                                System.exit(0);
                                            }
                                        }).show();
                            }
                        });
                    }catch (Exception e2){}
                }
            }
        };thread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        editName = findViewById(R.id.edit_text_name);
        editPassword = findViewById(R.id.edit_text_pass);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignUp = findViewById(R.id.btn_sign_up);
        context = this;
        Thread thread = new Thread(){
            @Override
            public void run(){
                while(true){
                    if(mySocket!=null&&mySocket.isChanged()){
                        byte[] bytes = mySocket.readLast();
                        try {
                            Log.d("TAG", "bytes: "+new String(bytes));
                            receive(bytes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mySocket.setChanged(false);
                    }
                }
            }
        };thread.start();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                String email = editName.getText().toString();
                String password = editPassword.getText().toString();
                MessageDigest digest = null;
                try {
                    digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
                    String sHash= Base64.encodeToString(hash, Base64.DEFAULT);
                    obj.put(TYPE_KEY, SIGN_IN_SEND_VALUE);
                    obj.put(AUTO_SIGN_IN_KEY, false);
                    obj.put(EMAIL_KEY, email);
                    obj.put(PASSWORD_KEY, sHash);
                    Thread thread1 = new Thread(){
                        @Override
                        public void run(){
                            try {
                                mySocket.send(obj.toString().getBytes());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };thread1.start();
                    noteViewModel.deleteAllNotesUser();
                    noteViewModel.insertUser(new NoteUser(email, sHash));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialog();
            }
        });
    }

    private void setDialog(){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(StartActivity.this);
        final View mView = LayoutInflater.from(StartActivity.this).inflate(R.layout.sign_up_dialog, null);
        EditText editTextName =mView.findViewById(R.id.edit_text_name);
        EditText editTextNPass =mView.findViewById(R.id.edit_text_pass);
        EditText editTextEmail =mView.findViewById(R.id.edit_text_email);
        Button dBtnSignUp=mView.findViewById(R.id.btn_sign_up);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        dBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String pass=editTextNPass.getText().toString();
                JSONObject obj = new JSONObject();
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(pass.getBytes(StandardCharsets.UTF_8));
                    String sHash= Base64.encodeToString(hash, Base64.DEFAULT);
                    Log.d("TAG", "sHash: "+sHash);
                    obj.put(TYPE_KEY, SIGN_UP_SEND_VALUE);
                    obj.put(NAME_KEY, editTextName.getText().toString());
                    obj.put(PASSWORD_KEY, sHash);
                    obj.put(EMAIL_KEY, editTextEmail.getText().toString());
                    noteViewModel.deleteAllNotesUser();
                    noteViewModel.insertUser(new NoteUser(editTextEmail.getText().toString(), sHash));
                    Thread thread = new Thread(){
                        @Override
                        public void run(){
                            try {
                                mySocket.send(obj.toString().getBytes());
                            } catch (Exception e) {e.printStackTrace();}
                        }
                    };thread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void receive(byte[] bytes) throws JSONException {
        String result = new String(bytes);
        JSONObject jsonObject = null;
        if(mySocket.isJson(result))
            jsonObject = new JSONObject(result);

        assert jsonObject != null;
        if(jsonObject.get(TYPE_KEY).equals(SUCCESS_SIGN_IN_VALUE)){
            JSONObject finalJsonObject = jsonObject;
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context,SUCCESS_SIGN_IN_VALUE,Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(context,MainActivity.class);
                    try {
                        MySocketSingleton.setMySocket(mySocket);
                        intent.putExtra("unique_id", finalJsonObject.getString("unique_id"));
                        intent.putExtra("name", finalJsonObject.getString("name"));
                        intent.putExtra("email", finalJsonObject.getString("email"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    context.startActivity(intent);
                    finish();
                }
            });
        }else if(jsonObject.get(TYPE_KEY).equals(ERROR_SIGN_IN_VALUE)){
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, ERROR_SIGN_IN_VALUE,Toast.LENGTH_SHORT).show();
                }
            });
        }else if(jsonObject.get(TYPE_KEY).equals(SUCCESS_SIGN_UP_VALUE)){
            JSONObject finalJsonObject1 = jsonObject;
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context,SUCCESS_SIGN_UP_VALUE,Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(context,MainActivity.class);
                    try {
                        MySocketSingleton.setMySocket(mySocket);
                        intent.putExtra("unique_id", finalJsonObject1.getString("unique_id"));
                        intent.putExtra("name", finalJsonObject1.getString("name"));
                        intent.putExtra("email", finalJsonObject1.getString("email"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    context.startActivity(intent);
                    finish();
                }
            });
        }else if(jsonObject.get(TYPE_KEY).equals(ERROR_SIGN_UP_VALUE)){
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, ERROR_SIGN_UP_VALUE,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}