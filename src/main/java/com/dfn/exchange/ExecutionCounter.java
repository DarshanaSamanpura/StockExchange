package com.dfn.exchange;

/**
 * Created by darshanas on 11/13/2017.
 */
public class ExecutionCounter {

    private static Long lastExecutionId;

    public static void setLastExecutionId(Long eid){
        lastExecutionId = eid;
    }


    public static synchronized String getTradeExecutionId(String symbol){
        lastExecutionId++;
        return System.currentTimeMillis() + "-" + symbol + "-" + Long.toString(lastExecutionId);
    }

}
