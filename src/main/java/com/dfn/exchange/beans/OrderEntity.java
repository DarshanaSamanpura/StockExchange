package com.dfn.exchange.beans;

import quickfix.fix42.NewOrderSingle;

/**
 * Created by darshanas on 11/8/2017.
 */
public class OrderEntity {

    private String orderId;
    private String traderId;
    private String symbol;
    private double qty;
    private double price;
    private char ordType;
    private char ordSide;
    private char tif;
    private long ordTime;
    private char ordStatus;
    private double executedQty;
    private double remainingQty;
    private boolean isFilled;
    private String accountNumber;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public char getOrdType() {
        return ordType;
    }

    public void setOrdType(char ordType) {
        this.ordType = ordType;
    }

    public char getOrdSide() {
        return ordSide;
    }

    public void setOrdSide(char ordSide) {
        this.ordSide = ordSide;
    }

    public char getTif() {
        return tif;
    }

    public void setTif(char tif) {
        this.tif = tif;
    }

    public long getOrdTime() {
        return ordTime;
    }

    public void setOrdTime(long ordTime) {
        this.ordTime = ordTime;
    }

    public char getOrdStatus() {
        return ordStatus;
    }

    public void setOrdStatus(char ordStatus) {
        this.ordStatus = ordStatus;
    }

    public double getExecutedQty() {
        return executedQty;
    }

    public void setExecutedQty(double executedQty) {
        this.executedQty = executedQty;
    }

    public double getRemainingQty() {
        return remainingQty;
    }

    public void setRemainingQty(double remainingQty) {
        this.remainingQty = remainingQty;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setIsFilled(boolean isFilled) {
        this.isFilled = isFilled;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
