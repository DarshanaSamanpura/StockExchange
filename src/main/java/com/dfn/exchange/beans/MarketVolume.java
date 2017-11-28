package com.dfn.exchange.beans;

/**
 * Created by darshanas on 11/25/2017.
 */
public class MarketVolume extends DfnMessage{

    private double sellSide;
    private double buySide;
    private double volume;
    private double exVolume;

    public MarketVolume(){
        setMessageType('V');
    }

    public MarketVolume(double sellSide, double buySide, double exVolume) {
        setMessageType('V');
        this.sellSide = sellSide;
        this.buySide = buySide;
        this.exVolume = exVolume;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getExVolume() {
        return exVolume;
    }

    public void setExVolume(double exVolume) {
        this.exVolume = exVolume;
    }

    public double getSellSide() {
        return sellSide;
    }

    public void setSellSide(double sellSide) {
        this.sellSide = sellSide;
    }

    public double getBuySide() {
        return buySide;
    }

    public void setBuySide(double buySide) {
        this.buySide = buySide;
    }
}
