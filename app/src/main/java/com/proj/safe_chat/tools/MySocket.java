package com.proj.safe_chat.tools;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.proj.safe_chat.encrypt.CalculateKey;
import com.proj.safe_chat.encrypt.Encryption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MySocket implements KeysJsonI{
    private Socket socket;
    ArrayList<byte[]> listOfStreams = new ArrayList();
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private boolean isChanged = false;
    private String myKey = "";
    private Context context;


    public MySocket(Socket socket, Context context) throws Exception {
        this.socket = socket;
        this.context = context;

        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void send(byte[] bytes) throws Exception {
        Log.d("TAG", "myKey24: "+myKey);
        if(!myKey.equals("")) {
            final byte[] encrypted_bytes = new Encryption(myKey).encrypt(bytes);
            outputStream.write(ByteBuffer.allocate(4)
                    .putInt(Base64.encodeToString(encrypted_bytes, Base64.DEFAULT).getBytes().length).array());
            outputStream.write(Base64.encodeToString(encrypted_bytes, Base64.DEFAULT).getBytes());
        }else {
            outputStream.write(ByteBuffer.allocate(4).putInt(bytes.length).array());
            outputStream.write(bytes);
        }
    }

    public void listen(){
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        int size = inputStream.readInt();
                        Log.d("TAG", "size_bytes: "+size);
                        byte[] bytes = new byte[size];
                        inputStream.readFully(bytes);
                        Log.d("TAG", "tbytes: "+new String(bytes));
                        String result = new String(bytes).trim();
                        if(!isJson(result)){
                            // TODO: 8/10/2021 decrypt...
                            bytes = new Encryption(myKey).decrypt(bytes);
                            result = new String(bytes).trim();
                        }
                        conditionsByJson(new JSONObject(result));
                        listOfStreams.add(bytes);
                        isChanged = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };thread.start();
    }

    private void conditionsByJson(JSONObject jsonObject) throws Exception {
        String myType = (String) jsonObject.get(TYPE_KEY);
        if(myType.equals(P_AND_A_VALUE)){
            JSONObject jsonMyPublicKey;
            Log.d("TAG", "PublicKeyAndAcceptedKey: ");
            CalculateKey calculateKey = new CalculateKey(jsonObject.getString("acceptedKey"),
                    jsonObject.getString("otherPublicKey"));

            jsonMyPublicKey = calculateKey.calculate();
            send(jsonMyPublicKey.toString().getBytes());

            while (calculateKey.getTheRealKey().equals("")) {}//wait until get the key
            myKey = calculateKey.getTheRealKey();
            if (calculateKey.getTheRealKey().length() < 16) {
                while (myKey.length() < 16) {
                    myKey += "0";
                }
            }
            Log.d("TAG", "myKey: "+myKey);
        }
        myType = "";
    }

    public byte[] readLast(){
        return this.listOfStreams.get(this.listOfStreams.size() - 1);
    }

    public ArrayList<byte[]> readAll(){
        return this.listOfStreams;
    }

    public boolean isChanged() {
        return this.isChanged;
    }

    public void setChanged(boolean changed) {
        isChanged = changed;
    }

    public boolean isJson(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
