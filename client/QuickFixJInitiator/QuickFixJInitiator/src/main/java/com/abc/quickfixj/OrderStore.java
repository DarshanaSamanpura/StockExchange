package com.abc.quickfixj;

import com.abc.quickfixj.entity.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darshanas on 11/6/2017.
 */
public class OrderStore {

    private static final List<Order> orderList = new ArrayList<Order>();

    public static void addOrder(Order order){
        orderList.add(order);
    }

    public static List<Order> getAllOrders(){
        return orderList;
    }

    public static Order findOrder(String clordId){
        for(Order order : orderList){
            if(order.getClOrdId().equals(clordId)){
                return order;
            }
        }
        return null;
    }

}
