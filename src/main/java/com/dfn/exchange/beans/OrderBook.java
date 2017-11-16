package com.dfn.exchange.beans;

import java.util.List;

/**
 * Created by darshanas on 11/8/2017.
 */
public class OrderBook extends DfnMessage{

    private final long timeStamp;
    private List<OrderBookRaw> raws;

    public OrderBook(List<OrderBookRaw> raws){
        timeStamp = System.currentTimeMillis();
        this.raws = raws;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public List<OrderBookRaw> getRaws() {
        return raws;
    }
}
