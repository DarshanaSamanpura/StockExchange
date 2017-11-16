package com.abc.quickfixj;


import com.abc.quickfixj.entity.Order;
import quickfix.*;
import quickfix.MessageFactory;
import quickfix.field.*;
import quickfix.fix42.*;

/**
 * Created by darshanas on 11/6/2017.
 */
public class FixSession {

    SocketInitiator socketInitiator = null;
    private static FixSession fixSession = null;

    public static FixSession getFixSession(){
        if(fixSession == null){
            fixSession = new FixSession();
        }
        return fixSession;
    }

    private FixSession(){
        try {
            System.out.println("Loading the Fix Session");
            SessionSettings sessionSettings = new SessionSettings("./initiatorSettings.txt");
            Application initiatorApplication = new TradeAppInitiator();
            FileStoreFactory fileStoreFactory = new FileStoreFactory(sessionSettings);
            FileLogFactory fileLogFactory = new FileLogFactory(sessionSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            socketInitiator = new SocketInitiator(initiatorApplication,fileStoreFactory,sessionSettings,fileLogFactory,
                    messageFactory);
            socketInitiator.start();
        } catch (ConfigError error) {
            error.printStackTrace();
        }

    }

    public void Login(){
        SessionID sessionID = (SessionID) socketInitiator.getSessions().get(0);
        Session.lookupSession(sessionID);
        Logon logon = new Logon();
        logon.set(new HeartBtInt(30));
        logon.set(new ResetSeqNumFlag(true));
        logon.set(new EncryptMethod(0));
        try {
            Session.sendToTarget(logon, sessionID);
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }

    public void LogOut(){
        SessionID sessionId = (SessionID) socketInitiator.getSessions().get(0);
        Session.lookupSession(sessionId);
        Logout logout = new Logout();
        try {
            Session.sendToTarget(logout,sessionId);
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }

    public void sendNewOrder(Order order){
        SessionID sessionId = (SessionID) socketInitiator.getSessions().get(0);
        Session.lookupSession(sessionId);
        NewOrderSingle fixOrd = new NewOrderSingle();
        fixOrd.set(new ClOrdID(order.getClOrdId()));
        fixOrd.set(new HandlInst('3'));
        fixOrd.set(new Symbol(order.getSymbol()));
        fixOrd.set(new Side(order.getSide()));
        fixOrd.set(new TransactTime());
        fixOrd.set(new OrdType(order.getOrdType()));
        fixOrd.set(new TimeInForce(order.getTif()));
        fixOrd.set(new OrderQty(order.getQty()));
        fixOrd.set(new Price(order.getPrice()));
        try {
            Session.sendToTarget(fixOrd,sessionId);
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }

    public void sendCancelOrder(OrderCancelRequest orderCancelRequest){
        SessionID sessionId = (SessionID) socketInitiator.getSessions().get(0);
        Session.lookupSession(sessionId);
        try{
            Session.sendToTarget(orderCancelRequest,sessionId);
        }catch (SessionNotFound sessionNotFound){
            sessionNotFound.printStackTrace();
        }
    }

    public void sendAmendOrder(OrderCancelReplaceRequest request){
        SessionID sessionId = (SessionID) socketInitiator.getSessions().get(0);
        Session.lookupSession(sessionId);
        try{
            Session.sendToTarget(request,sessionId);
        }catch (SessionNotFound sessionNotFound){
            sessionNotFound.printStackTrace();
        }
    }

}
