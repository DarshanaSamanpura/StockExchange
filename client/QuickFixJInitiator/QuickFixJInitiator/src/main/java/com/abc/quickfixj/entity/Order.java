package com.abc.quickfixj.entity;

/**
 * Created by darshanas on 11/6/2017.
 */
public class Order {

    private String clOrdId;
    private char side;
    private float price;
    private float qty;
    private char tif;
    private char ordType;
    private String symbol;
    private char ordStatus;
    private String tradingAcc;

    public String getTradingAcc() {
        return tradingAcc;
    }

    public void setTradingAcc(String tradingAcc) {
        this.tradingAcc = tradingAcc;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getClOrdId() {
        return clOrdId;
    }

    public void setClOrdId(String clOrdId) {
        this.clOrdId = clOrdId;
    }

    public char getSide() {
        return side;
    }

    public void setSide(char side) {
        this.side = side;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public char getTif() {
        return tif;
    }

    public void setTif(char tif) {
        this.tif = tif;
    }

    public char getOrdType() {
        return ordType;
    }

    public void setOrdType(char ordType) {
        this.ordType = ordType;
    }

    public char getOrdStatus() {
        return ordStatus;
    }

    public void setOrdStatus(char ordStatus) {
        this.ordStatus = ordStatus;
    }
}
