package com.dfn.exchange;

import edu.emory.mathcs.backport.java.util.Arrays;

import java.util.List;

/**
 * Created by darshanas on 11/2/2017.
 */
public class Constants {

    public static List<String> getSymbols(){
        return Arrays.asList(new String[]{"1010","1020","1030"});
    }

     public static final String DB = "mysql";
  //  public static final String DB = "h2";

    public static final int FIX_MSG_IN = 1;
    public static final int FIX_MSG_OUT = 2;

}
