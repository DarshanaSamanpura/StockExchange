package com.dfn.exchange.beans;

/**
 * Created by darshanas on 12/12/2017.
 */
public class LastTrade {

    private final double ltp;
    private final double ltq;

    public LastTrade(double ltp, double ltq) {
        this.ltp = ltp;
        this.ltq = ltq;
    }

    public double getLtp() {
        return ltp;
    }

    public double getLtq() {
        return ltq;
    }
}
