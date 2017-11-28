package com.dfn.exchange.beans;

/**
 * Created by darshanas on 11/24/2017.
 */
public class TradeMatch extends DfnMessage{


    private String id;
    private double qty;
    private double price;
    private String time;
    private String buyOrdId;
    private String sellOrdId;

    public TradeMatch(){
        setMessageType('8');
    }

    public TradeMatch(String id, double qty, double price, String time,
                      String buyOrdId,String sellOrdId) {
        this.id = id;
        this.qty = qty;
        this.price = price;
        this.time = time;
        this.buyOrdId = buyOrdId;
        this.sellOrdId = sellOrdId;
        setMessageType('8');
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBuyOrdId() {
        return buyOrdId;
    }

    public void setBuyOrdId(String buyOrdId) {
        this.buyOrdId = buyOrdId;
    }

    public String getSellOrdId() {
        return sellOrdId;
    }

    public void setSellOrdId(String sellOrdId) {
        this.sellOrdId = sellOrdId;
    }
}
