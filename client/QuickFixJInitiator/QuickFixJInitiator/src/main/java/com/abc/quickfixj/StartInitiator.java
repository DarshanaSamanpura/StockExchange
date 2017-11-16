package com.abc.quickfixj;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix42.NewOrderSingle;
import quickfix.fix42.Logon;

/**
 * Created by manodyas on 10/31/2017.
 */
public class StartInitiator {
    public static void main(String[] args) {
        SocketInitiator socketInitiator = null;
        try {
            SessionSettings initiatorSettings = new SessionSettings(
                    "./initiatorSettings.txt");
            Application initiatorApplication = new TradeAppInitiator();
            FileStoreFactory fileStoreFactory = new FileStoreFactory(
                    initiatorSettings);
            FileLogFactory fileLogFactory = new FileLogFactory(
                    initiatorSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            socketInitiator = new SocketInitiator(initiatorApplication, fileStoreFactory, initiatorSettings, fileLogFactory, messageFactory);
            socketInitiator.start();
            SessionID sessionId =  (SessionID) socketInitiator.getSessions().get(0);
            Session.lookupSession(sessionId).logon();


            Logon logon = new Logon();

            logon.set(new quickfix.field.HeartBtInt(30));
            logon.set(new quickfix.field.ResetSeqNumFlag(true));
            logon.set(new quickfix.field.EncryptMethod(0));

            try {
                Session.sendToTarget(logon, sessionId);
            } catch (SessionNotFound sessionNotFound) {
                sessionNotFound.printStackTrace();
            }


            for(int j = 0; j < 2; j ++){
                try {
                    Thread.sleep(5000);
                    NewOrderSingle newOrderSingle = new NewOrderSingle(
                            new ClOrdID("456"),
                            new HandlInst('3'),
                            new Symbol("AJCB"),
                            new Side(Side.BUY),
                            new TransactTime(),
                            new OrdType(OrdType.MARKET)
                    );
                    System.out.println("####New Order Sent :" + newOrderSingle.toString());
                    Session.sendToTarget(newOrderSingle, sessionId);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SessionNotFound sessionNotFound) {
                    sessionNotFound.printStackTrace();
                }
            }

        }  catch (ConfigError configError) {
            configError.printStackTrace();
        }
    }
}
