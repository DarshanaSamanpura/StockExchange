package com.abc.quickfixj;

import com.abc.quickfixj.entity.Order;
import quickfix.field.*;
import quickfix.fix42.OrderCancelRequest;

import java.util.Date;
import java.util.Scanner;

/**
 * Created by darshanas on 11/6/2017.
 */
public class FixClient {

    private static FixSession fixSession = null;

    public static void main(String[] args) {
        System.out.println("Starting Simple FIX Client");
        fixSession = FixSession.getFixSession();
        Scanner scanner = new Scanner(System.in);
        String command;
        while (true){
            System.out.println("Enter your command");
            System.out.println(" _ ");
            command = scanner.nextLine();
            if(validateCommand(command)){

                if(command.equals("quit") || command.equals("q") || command.equals("exit")){
                    break;
                }else {
                    processCommand(command,scanner);
                }

            }else {
                System.out.println("Invalid Command pls try again.");
            }
        }
    }

    private static boolean validateCommand(String command){
        if(command == null){
            return false;
        }
        if(command.equals("")){
            return false;
        }
        return true;
    }

    private static void processCommand(String command,Scanner scanner){
        if(command.equals("login")){
            fixSession.Login();
        }else if(command.equals("logout")){
            fixSession.LogOut();
        }else if(command.equals("newOrd")){
            Order order = createOrder(scanner);
            OrderStore.addOrder(order);
            fixSession.sendNewOrder(order);
        }else if(command.equals("cancelOrd")){
            System.out.println("Order Id to cancel : ");
            String ordId = scanner.nextLine();
            OrderCancelRequest request = getOrderCancelReq(ordId);
        }else if(command.equals("amendOrd")){

        }else {
            System.out.println("Unknown Command");
        }
    }


    private static Order createOrder(Scanner scanner){
        Order order = new Order();
        order.setClOrdId(Long.toString(System.currentTimeMillis()));
        System.out.println("Enter the Symbol ");
        order.setSymbol(scanner.nextLine());
        System.out.println("Side (1 - buy / 2 - sell)");
        order.setSide(scanner.nextLine().toCharArray()[0]);
        System.out.println("Type (1 - market, 2 - limit)");
        order.setOrdType(scanner.nextLine().toCharArray()[0]);
        if(order.getOrdType() == '2'){
            System.out.println("Price ");
            order.setPrice(Float.parseFloat(scanner.nextLine()));
        }
        System.out.println("Qty");
        order.setQty(Float.parseFloat(scanner.nextLine()));
        System.out.println("TIF (0 - Day, 1 - GTC, 2 - at opening, 3 - IOC)");
        order.setTif(scanner.nextLine().toCharArray()[0]);
        return order;
    }

    private static OrderCancelRequest getOrderCancelReq(String orderId){
        Order order = OrderStore.findOrder(orderId);
        if(order != null){
            OrderCancelRequest cancelRequest = new OrderCancelRequest();
            cancelRequest.set(new ClOrdID(orderId));
            cancelRequest.set(new Symbol(order.getSymbol()));
            cancelRequest.set(new Side(order.getSide()));
            cancelRequest.set(new TransactTime(new Date()));
            cancelRequest.set(new ClientID("jhon1"));
        }
        return null;
    }

}
