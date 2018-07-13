package com.chat.chatclient;

/**
 * Create by Guolianxing on 2018/7/6.
 */
public class GlobalStore {

    public static final String SERVER_IP = "47.104.246.160";

    public static final String SERVER_URL = "http://" + SERVER_IP + ":8080";

    public static final String LOGIN_URL = SERVER_URL + "/user/login";

    public static final String LOGOUT_URL = SERVER_URL + "/user/logout";

    public static final String REGISTER_URL = SERVER_URL + "/user/register";

    public static final String GET_ROOMINFO_URL = SERVER_URL + "/user/getAllRoom";

    public static final String ENTER_ROOM = SERVER_URL + "/user/enterRoom";

    public static final String LEAVE_ROOM = SERVER_URL + "/user/leaveRoom";

    public static final String CREATE_ROOM = SERVER_URL + "/user/createRoom";

    public static final String GET_USERS_IN_ROOM = SERVER_URL + "/user/getUsersInRoom";

    public static String SOCKET_URL;

    public static String TOKEN;

    public static User USER;
}
