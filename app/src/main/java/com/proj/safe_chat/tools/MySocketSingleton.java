package com.proj.safe_chat.tools;

//תבנית עיצוב המחזירה אובייקט קבוע עבור כל בקשה שהיא
public class MySocketSingleton {
    private static MySocket mySocket;

    public static void setMySocket(MySocket _mySocket){
        MySocketSingleton.mySocket = _mySocket;
    }

    public static MySocket getMySocket(){
        return MySocketSingleton.mySocket;
    }
}
