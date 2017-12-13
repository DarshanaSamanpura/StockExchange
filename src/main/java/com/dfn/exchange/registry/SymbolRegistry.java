package com.dfn.exchange.registry;

import akka.actor.ActorRef;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by darshanas on 12/11/2017.
 */
public class SymbolRegistry {

    private static final Map<String,ActorRef> symbolMap = new HashMap<>();

    public static void addToMap(String symbol,ActorRef ref){
        symbolMap.put(symbol,ref);
    }

    public static ActorRef getSymbolActor(String symbol){
        return symbolMap.get(symbol);
    }

}
