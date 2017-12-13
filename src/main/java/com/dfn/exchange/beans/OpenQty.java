package com.dfn.exchange.beans;

/**
 * Created by darshanas on 12/12/2017.
 */
public class OpenQty {

    private final String symbol;
    private final double qty;
    private String ordSide;

    public OpenQty(String symbol, double qty) {
        this.symbol = symbol;
        this.qty = qty;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getQty() {
        return qty;
    }

    public String getOrdSide() {
        return ordSide;
    }

    public void setOrdSide(String ordSide) {
        this.ordSide = ordSide;
    }
}
