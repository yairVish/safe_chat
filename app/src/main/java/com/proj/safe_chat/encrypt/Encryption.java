package com.proj.safe_chat.encrypt;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
    private String keyStr = "";
    private SecretKey key;

    public Encryption(String keyStr){
        this.keyStr = keyStr;
        key = new SecretKeySpec(keyStr.getBytes(), "AES");
    }

    public byte[] encrypt(byte[] bytes) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,key);
        bytes = cipher.doFinal(bytes);
        return bytes;
    }

    public byte[] decrypt(byte[] bytes) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,key);
        byte[] text = Base64.decode(new String(bytes), Base64.DEFAULT);
        Log.d("TAG", "decryptStringTEXT: "+text.toString());
        bytes = cipher.doFinal(text);
        return bytes;
    }
}
