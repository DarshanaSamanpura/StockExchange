package com.dfn.exchange;

import com.dfn.exchange.beans.OrderBook;
import com.dfn.exchange.beans.OrderBookRaw;
import com.dfn.exchange.beans.WSUtil.Header;
import com.dfn.exchange.beans.WSUtil.LoginResponse;
import com.dfn.exchange.beans.WSUtil.Message;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darshanas on 11/13/2017.
 */
public class SimpleTest {

    public static void main(String[] args) {

       /* Gson gson = new Gson();
        List<OrderBookRaw> orderBookRawList = new ArrayList<>();
        OrderBookRaw raw1 = new OrderBookRaw(1,"10:30:00",90,100,"10:30:00",90.60,90);
        OrderBookRaw raw2 = new OrderBookRaw(1,"10:31:00",90,100,"10:31:00",90.60,90);
        orderBookRawList.add(raw1);
        orderBookRawList.add(raw2);
        OrderBook orderBook = new OrderBook(orderBookRawList);
        orderBook.setMessageType('B');
        String ss = gson.toJson(orderBook);
        System.out.println(ss);*/
        Gson gson = new Gson();
        Message loginRequest =  new Message();
        Header requestHeader = new Header();
        requestHeader.setMessageGroup(1);
        loginRequest.setHeader(requestHeader);

        System.out.println("Login :" + gson.toJson(loginRequest));

        Message symbolMetaRequest = new Message();
        requestHeader.setMessageGroup(2);
        symbolMetaRequest.setHeader(requestHeader);

        System.out.println("Symbol Meta :" + gson.toJson(symbolMetaRequest));


    }

}
