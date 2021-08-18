package com.proj.safe_chat.tools;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.proj.safe_chat.encrypt.CalculateKey;
import com.proj.safe_chat.encrypt.Encryption;
import com.proj.safe_chat.roomsql.Message;
import com.proj.safe_chat.roomsql.NoteViewModel;

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
import java.util.Arrays;
import java.util.List;

public class MySocket implements KeysJsonI{
    private Socket socket;
    ArrayList<byte[]> listOfStreams = new ArrayList();
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private boolean isChanged = false;
    private String myKey = "";
    private Context context;
    private NoteViewModel noteViewModel;
    public static String idChat="";


    public MySocket(Socket socket, Context context) throws Exception {
        this.socket = socket;
        this.context = context;

        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
        noteViewModel = ViewModelProviders.of((FragmentActivity) context).get(NoteViewModel.class);
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
        }else if(myType.equals(MESSAGE_VALUE)){
            try {
                noteViewModel.insertMessage(new Message(jsonObject.getString("body")
                        ,jsonObject.getString("from"),jsonObject.getString("from")
                        ,false, jsonObject.getLong("time")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (myType.equals(MESSAGE_ONE_TIME_VALUE)){
            List<String> fromIds;
            List<String> toIds;
            List<String> times;
            List<String> bodys;
            toIds = splitArrayByFiled("to", jsonObject);
            fromIds = splitArrayByFiled("from", jsonObject);
            times = splitArrayByFiled("time", jsonObject);
            bodys = splitArrayByFiled("body", jsonObject);
            Log.d("TAG", "toIds: "+toIds);
            Log.d("TAG", "fromIds: "+fromIds);
            Log.d("TAG", "times: "+times);
            Log.d("TAG", "bodys: "+bodys);
            for(int i=0;i<bodys.size();i++) {
                String fromS = fromIds.get(i).substring(1,fromIds.get(i).length()-1);
                noteViewModel.insertMessage(new Message(bodys.get(i).substring(1,bodys.get(i).length()-1), fromS, fromS
                        ,false,Long.parseLong(times.get(i))));
            }
        }
        myType = "";
    }
    private List<String> splitArrayByFiled(String filed, JSONObject jsonObject) throws JSONException {
        String singleStr=jsonObject.getString(filed).substring(1, jsonObject.getString(filed).length()-1);
        String [] str = singleStr.split(",");
        return Arrays.asList(str);
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
