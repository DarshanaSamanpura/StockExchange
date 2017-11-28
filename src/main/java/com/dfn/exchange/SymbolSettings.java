package com.dfn.exchange;

import com.dfn.exchange.beans.Symbol;
import com.dfn.exchange.beans.SymbolList;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by darshanas on 11/13/2017.
 */
public class SymbolSettings {

    private static Gson gson = new Gson();
    private static String symbolJson = null;

    public static List<Symbol> getSymbolList(){

        Symbol [] symbols = new Symbol[]{
                new Symbol("ribl","1010","tdwl","2","60",1,"2"),
                new Symbol("Bank Al Jazira","1020"),
                new Symbol("The Saudi Investment Bank","1030"),
                new Symbol("Alawal Bank","1040"),
                new Symbol("Banque Saudi Faransi","1050"),
                new Symbol("Saudi British Bank","1060"),
                new Symbol("Arab National Bank","1080"),
                new Symbol("Samba Financial Group","1090"),
                new Symbol("Al Rajhi Bank","1120"),
                new Symbol("Bank Albilad","1140"),
                new Symbol("Al Inma Bank","1150"),
                new Symbol("National Commercial Bank","1180"),
                new Symbol("Takween Advanced Industries","1201"),
                new Symbol("Middle east paper co","1202"),
                new Symbol("Basic chemical industries","1210"),
                new Symbol("Saudi Arabian mining co","1211"),
                new Symbol("Astra Industries","1212"),
                new Symbol("Al Sorayai","1213"),
                new Symbol("SHAKER","1214"),
                new Symbol("United wire factories","1301"),
                new Symbol("Bawan co","1302"),
                new Symbol("Electrical Industries","1303"),
                new Symbol("Al Yamama Steel","1304"),
                new Symbol("Saudi Steel Pipe co","1320"),
                new Symbol("ALKHODARI","1330"),
                new Symbol("ALTAYYAR","1810"),
                new Symbol("Al Hokair Group","1820"),
                new Symbol("Methanol co","2001"),
                new Symbol("National Patro chemical co","2002"),
                new Symbol("Saudi Basic Industries co","2010"),
                new Symbol("Saudi Arabia Fertilizers","2020"),
                new Symbol("Saudi Arabia Refineries","2030"),
                new Symbol("Saudi Ceramic","2040"),
                new Symbol("Savola Group","2050"),
                new Symbol("National Industrialization","2060"),
                new Symbol("SPIMACO","2070"),
                new Symbol("National Gas and industrialization","2080"),
                new Symbol("National Gypsum","2090"),
                new Symbol("WAFRAH","2100"),
                new Symbol("Saudi Cable","2110"),
                new Symbol("Saudi Advanced industries","2120")
        };
        return Arrays.asList(symbols);

    }


    public static String getSymbolString(){
        if(symbolJson == null){
            SymbolList symbolList = new SymbolList();
            symbolList.setMessageType('S');
            symbolList.setSymbolList(getSymbolList());
            symbolJson = gson.toJson(symbolList);
        }
        return symbolJson;
    }
    
}
