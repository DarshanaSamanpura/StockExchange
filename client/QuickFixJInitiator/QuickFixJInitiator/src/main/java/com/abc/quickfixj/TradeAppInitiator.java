package com.abc.quickfixj;

import com.abc.quickfixj.entity.Order;
import quickfix.*;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.Heartbeat;
import quickfix.fix42.NewOrderSingle;

/**
 * Created by manodyas on 10/31/2017.
 */
public class TradeAppInitiator extends MessageCracker implements Application{


    public void onCreate(SessionID sessionID) {

    }


    public void onLogon(SessionID sessionID) {

    }


    public void onLogout(SessionID sessionID) {

    }


    public void toAdmin(Message message, SessionID sessionID) {

    }


    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        int y = 0;
        if(!isHeartBeatMessage(message)){
            System.out.println("Admin Message Received (Initiator) :" + message.toString());
        }
    }

    private boolean isHeartBeatMessage(Message message){
        if(message instanceof Heartbeat){
            return true;
        }
        return false;
    }


    public void toApp(Message message, SessionID sessionID) throws DoNotSend {

    }


    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        //System.out.println("Application Response Received (Initiator) :" +  message.toString());
        crack(message,sessionID);
    }

    public void onMessage(ExecutionReport executionReport,SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue{
        System.out.println("New Execution Report for " + executionReport.getClOrdID().getValue() + ", status " + executionReport.getOrdStatus().getValue());
        System.out.println(executionReport.toString());
        Order order = OrderStore.findOrder(executionReport.getClientID().getValue());
        if(order != null){
            order.setOrdStatus(executionReport.getOrdStatus().getValue());
        }
    }
}
