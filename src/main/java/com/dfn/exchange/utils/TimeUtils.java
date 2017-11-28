package com.dfn.exchange.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by darshanas on 11/24/2017.
 */
public class TimeUtils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");

    public static String getTimeString(){
        return dateFormat.format(new Date());
    }


}
