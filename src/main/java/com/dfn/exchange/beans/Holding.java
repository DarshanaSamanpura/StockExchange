package com.dfn.exchange.beans;

/**
 * Created by darshanas on 12/4/2017.
 */
public class Holding {

    private String accountNumber;
    private String symbol;
    private double qty;

    public Holding(String accountNumber, String symbol, double qty) {
        this.accountNumber = accountNumber;
        this.symbol = symbol;
        this.qty = qty;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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
}
