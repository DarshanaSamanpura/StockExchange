package com.dfn.exchange.registry;

import com.dfn.exchange.beans.LastTrade;
import com.dfn.exchange.beans.MarketVolume;
import com.dfn.exchange.beans.OrderBook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by darshanas on 12/12/2017.
 */
public class StateRegistry {

    private static int marketState = 2;
    public static long newOrdCount = 0;
    public static long matchCount = 0;

    private static Map<String,OrderBook> orderBookSnapShot = new HashMap<>();
    private static Map<String,LastTrade> ltpMap = new HashMap<>();
    private static Map<String,MarketVolume> mktVolMap = new HashMap<>();

    public static void setMarketState(int state){
        marketState = state;
    }

    public static int getMarketState(){
        return marketState;
    }

    public static void addOrderBookSnapShot(String symbol,OrderBook orderBook){
        orderBookSnapShot.put(symbol,orderBook);
    }

    public static void putLastTradePrice(String symbol,LastTrade lastTrade){
        ltpMap.put(symbol,lastTrade);
    }

    public static LastTrade getLastTradePrice(String symbol){
        return ltpMap.get(symbol);
    }

    public static OrderBook getOrderBookSnapShot(String symbol){
        return orderBookSnapShot.get(symbol);
    }

    public static void putMarketVolumeState(String symbol,MarketVolume marketVolume){
        mktVolMap.put(symbol,marketVolume);
    }

    public static MarketVolume getMarketVolume(String symbol){
        return mktVolMap.get(symbol);
    }

    public static List<MarketVolume> getAllMarketVolumes(){
        List<MarketVolume> list = new ArrayList<>();
        mktVolMap.forEach((k,v) -> list.add(v));
        return list;
    }

}
