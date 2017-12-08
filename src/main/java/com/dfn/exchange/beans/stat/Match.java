package com.dfn.exchange.beans.stat;

/**
 * Created by darshanas on 12/8/2017.
 */
public class Match {

    private final long matchTime;

    public Match(long matchTime) {
        this.matchTime = matchTime;
    }

    public long getMatchTime() {
        return matchTime;
    }
}
