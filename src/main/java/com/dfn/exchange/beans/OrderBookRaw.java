package com.dfn.exchange.beans;

import java.math.BigDecimal;

/**
 * Created by darshanas on 11/2/2017.
 */
public class OrderBookRaw {

    private final int rawId;
    private final String bidTime;
    private final double bidQty;
    private final double bidValue;
    private final String askTime;
    private final double askQty;
    private final double askValue;

    public OrderBookRaw(int rawId, String bidTime, double bidQty, double bidValue, String askTime, double askQty, double askValue) {
        this.rawId = rawId;
        this.bidTime = bidTime;
        this.bidQty = bidQty;
        this.bidValue = bidValue;
        this.askTime = askTime;
        this.askQty = askQty;
        this.askValue = askValue;
    }

    public int getRawId() {
        return rawId;
    }

    public String getBidTime() {
        return bidTime;
    }

    public double getBidQty() {
        return bidQty;
    }

    public double getBidValue() {
        return bidValue;
    }

    public String getAskTime() {
        return askTime;
    }

    public double getAskQty() {
        return askQty;
    }

    public double getAskValue() {
        return askValue;
    }
}
