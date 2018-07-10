package com.chat.chatclient.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Create by Guolianxing on 2018/7/6.
 */
public class TimeUtil {

    public static String dataTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
