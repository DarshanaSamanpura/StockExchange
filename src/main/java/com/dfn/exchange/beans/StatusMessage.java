package com.dfn.exchange.beans;

import java.util.List;

/**
 * Created by darshanas on 12/12/2017.
 */
public class StatusMessage extends DfnMessage {

    private int marketStatus;
    private long orderCount;
    private long matchCount;
    private List<MarketVolume> marketVolumes;


    public StatusMessage(){
        setMessageType('h');
    }

    public int getMarketStatus() {
        return marketStatus;
    }

    public void setMarketStatus(int marketStatus) {
        this.marketStatus = marketStatus;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(long orderCount) {
        this.orderCount = orderCount;
    }

    public long getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(long matchCount) {
        this.matchCount = matchCount;
    }

    public List<MarketVolume> getMarketVolumes() {
        return marketVolumes;
    }

    public void setMarketVolumes(List<MarketVolume> marketVolumes) {
        this.marketVolumes = marketVolumes;
    }
}
