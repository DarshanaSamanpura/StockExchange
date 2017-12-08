package com.dfn.exchange.beans;

import java.util.List;

/**
 * Created by darshanas on 11/24/2017.
 */
public class SymbolList extends DfnMessage {

    private List<Symbol> symbolList;

    public List<Symbol> getSymbolList() {
        return symbolList;
    }

    public void setSymbolList(List<Symbol> symbolList) {
        this.symbolList = symbolList;
    }
}
