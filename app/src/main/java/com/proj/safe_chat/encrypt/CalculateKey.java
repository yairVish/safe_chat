package com.proj.safe_chat.encrypt;

import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Random;

public class CalculateKey {
    private BigInteger privateKey;
    private BigInteger myPublicKey;
    private BigInteger otherPublicKey;
    private BigInteger acceptedKey;
    private String theRealKey="";

    //בנאי המחלקה
    public CalculateKey(String acceptedKey, String otherPublicKey){
        this.acceptedKey = new BigInteger(acceptedKey);
        this.otherPublicKey = new BigInteger(otherPublicKey);
        privateKey=random();
    }
    //מחשב מפתח ציבורי ומפתח סופי - מחזיר את המפתח הציבורי בפונקציה כערך כדי לשלוח את המםתח הציבורי שלו לצד השני
    public JSONObject calculate() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        myPublicKey = BigInteger.valueOf(3).modPow(privateKey,acceptedKey);
        jsonObject.put("type", "PublicKey");
        jsonObject.put("otherPublicKey", myPublicKey);
        BigInteger output = otherPublicKey.modPow(privateKey,acceptedKey);
        theRealKey= String.valueOf(output);
        return jsonObject;
    }

    //מגריל מפתח על גבי ספרות גדולות - bigInteger
    private BigInteger random(){
        BigInteger maxLimit = new BigInteger("99999");
        BigInteger minLimit = new BigInteger("10000");
        BigInteger bigInteger = maxLimit.subtract(minLimit);
        Random randNum = new Random();
        int len = maxLimit.bitLength();
        BigInteger res = new BigInteger(len, randNum);
        if (res.compareTo(minLimit) < 0)
            res = res.add(minLimit);
        if (res.compareTo(bigInteger) >= 0)
            res = res.mod(bigInteger).add(minLimit);
        System.out.println("The random BigInteger = "+res);
        return res;
    }

    //שינוי מפתח מוסכם
    public void setAcceptedKey(String acceptedKey) {
        this.acceptedKey = new BigInteger(acceptedKey);
    }

    //שינוי מפתח ציבורי של הצד השני
    public void setOtherPublicKey(String otherPublicKey) {
        this.otherPublicKey = new BigInteger(otherPublicKey);
    }

    //קבלת מפתח סופי
    public String getTheRealKey() {
        return theRealKey;
    }
}
