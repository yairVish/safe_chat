package com.proj.safe_chat.tools;

public interface KeysJsonI {
    String TYPE_KEY = "type";
    String AUTO_SIGN_IN_KEY = "oto";
    String EMAIL_KEY = "email";
    String NAME_KEY = "name";
    String PASSWORD_KEY = "password";
    String UID_KEY = "uid";
    String FROM_KEY = "from";
    String TO_KEY = "to";
    String BODY_KEY = "body";
    String TIME_KEY = "time";
    String TOKEN_KEY = "token";

    //////////////////////////////////////////////////////////////////////////////////////

    String SIGN_IN_SEND_VALUE = "signIn";
    String SIGN_UP_SEND_VALUE = "signUp";
    String P_AND_A_VALUE = "PublicKeyAndAcceptedKey";
    String SUCCESS_SIGN_IN_VALUE = "successSignIn";
    String ERROR_SIGN_IN_VALUE = "errorSignIn";
    String SUCCESS_SIGN_UP_VALUE = "successSignUp";
    String ERROR_SIGN_UP_VALUE = "errorSignUp";
    String GET_ALL_VALUE = "getAll";
    String LOGOUT = "logout";
    String MESSAGE_VALUE = "message";
    String MESSAGE_ONE_TIME_VALUE = "messageOneTime";
}
