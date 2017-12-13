package com.dfn.exchange.beans.WSUtil;

import com.google.gson.annotations.SerializedName;

/**
 * Created by manodyas on 12/12/2017.
 */
public class SymbolSubscriptionRequest {
    @SerializedName("symbolCode")
    private String symbolCode;

    public String getSymbolCode() {
        return symbolCode;
    }

    public void setSymbolCode(String symbolCode) {
        this.symbolCode = symbolCode;
    }
}
