package com.dfn.exchange.beans;

/**
 * Created by darshanas on 11/13/2017.
 */
public class Symbol {

    private long sid;
    private String symbol;
    private String symbolCode;
    private String exchange;
    private String exchangeCode;
    private String instrumentTyp;
    private int decimalCorrFactor;
    private String mktCode;

    public Symbol(){

    }

    public Symbol(String symbol, String symbolCode){
        this.symbol = symbol;
        this.symbolCode = symbolCode;
        this.exchange = "TDWL";
        this.exchangeCode = "2";
        this.instrumentTyp = "60";
        this.decimalCorrFactor = 1;
        this.mktCode = "2";
    }

    public Symbol(String symbol, String symbolCode, String exchange, String exchangeCode,
                  String instrumentTyp, int decimalCorrFactor, String mktCode) {

        this.symbol = symbol;
        this.symbolCode = symbolCode;
        this.exchange = exchange;
        this.exchangeCode = exchangeCode;
        this.instrumentTyp = instrumentTyp;
        this.decimalCorrFactor = decimalCorrFactor;
        this.mktCode = mktCode;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbolCode() {
        return symbolCode;
    }

    public void setSymbolCode(String symbolCode) {
        this.symbolCode = symbolCode;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getInstrumentTyp() {
        return instrumentTyp;
    }

    public void setInstrumentTyp(String instrumentTyp) {
        this.instrumentTyp = instrumentTyp;
    }

    public int getDecimalCorrFactor() {
        return decimalCorrFactor;
    }

    public void setDecimalCorrFactor(int decimalCorrFactor) {
        this.decimalCorrFactor = decimalCorrFactor;
    }

    public String getMktCode() {
        return mktCode;
    }

    public void setMktCode(String mktCode) {
        this.mktCode = mktCode;
    }
}
