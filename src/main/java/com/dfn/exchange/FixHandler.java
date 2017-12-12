package com.dfn.exchange;

import akka.actor.UntypedActor;
import com.dfn.exchange.ado.DataService;
import com.dfn.exchange.ado.FixServiceDao;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import quickfix.*;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.MessageFactory;
import quickfix.field.*;
import quickfix.fix42.*;


/**
 * Created by darshanas on 11/2/2017.
 */
public class FixHandler extends UntypedActor {

    private static final Logger logger = LogManager.getLogger(FixHandler.class);
    private FixServer fixServer = null;
    private FixServiceDao fixServiceDao = null;

    @Override
    public void preStart() throws Exception {
        super.preStart();
        logger.info("Starting the FIX Server ");
        System.out.println("FIX Handler path " + getSelf().path());
        SocketAcceptor socketAcceptor = null;
        fixServiceDao = DataService.getInstance(Constants.DB).getDbi().onDemand(FixServiceDao.class);
        try {
            SessionSettings executorSettings = new SessionSettings("acceptorSettings.txt");
            fixServer = new FixServer();
            FileStoreFactory fileStoreFactory = new FileStoreFactory(executorSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            FileLogFactory fileLogFactory = new FileLogFactory(executorSettings);
            socketAcceptor = new SocketAcceptor(fixServer,fileStoreFactory,executorSettings,fileLogFactory,messageFactory);
            socketAcceptor.start();
        }catch (ConfigError error){
            logger.error("Fix Server could not be started",error);
            error.printStackTrace();
        }
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof InMessageFix){
//            System.out.println("Actor system recieved new fix message");
//            InMessageFix inMessageFix = (InMessageFix) message;
//            System.out.println("Fix Message " + inMessageFix.getFixMessage().toString());
//            System.out.println("Session " + inMessageFix.getSessionID().toString());
//            NewOrderSingle order = (NewOrderSingle)inMessageFix.getFixMessage();
//            fixServer.sendFixMessage(new OutMessageFix(getExecutionMsg(order),inMessageFix.getSessionID()));
            //getContext().parent().tell(message,getSelf());
        }else if(message instanceof OutMessageFix){
            fixServer.sendFixMessage((OutMessageFix) message);
        }
    }

    class FixServer extends MessageCracker implements Application{

        private final Logger fixLogger = LogManager.getLogger(FixServer.class);

        @Override
        public void onCreate(SessionID sessionID) {

        }

        @Override
        public void onLogon(SessionID sessionID) {

        }

        @Override
        public void onLogout(SessionID sessionID) {

        }

        @Override
        public void toAdmin(Message message, SessionID sessionID) {

        }

        @Override
        public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
            fixLogger.info("Admin message received " + message.toString() + " session " + sessionID.toString());
        }

        @Override
        public void toApp(Message message, SessionID sessionID) throws DoNotSend {

        }

        public void onMessage(quickfix.fix42.NewOrderSingle order, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
            System.out.println("###NewOrder Received:" + order.toString());
            System.out.println("###Symbol" + order.getSymbol().toString());
            System.out.println("###Side" + order.getSide().toString());
            System.out.println("###Type" + order.getOrdType().toString());
            System.out.println("###TransactioTime" + order.getTransactTime().toString());
            //sendMessageToClient(order, sessionID);
            System.out.println(order.toString());
            InMessageFix inMessageFix = new InMessageFix(order,sessionID);
            getContext().parent().tell(inMessageFix, getSelf());

        }

        public void onMessage(quickfix.fix42.OrderCancelRequest request, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
            System.out.println("Order Cancel request recieved");
            System.out.println(request.toString());
            InMessageFix inMessageFix = new InMessageFix(request,sessionID);
            getContext().parent().tell(inMessageFix, getSelf());
        }

        public void onMessage(OrderCancelReplaceRequest amendRequest, SessionID sessionID){
            System.out.println("****Amend Order Request Received****"  + amendRequest.toString());
        //    System.out.println(amendRequest.toString());
            InMessageFix inMessageFix = new InMessageFix(amendRequest,sessionID);
            getContext().parent().tell(inMessageFix, getSelf());
        }

        @Override
        public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
            try {
                fixServiceDao.addToFixStore(Constants.FIX_MSG_IN,message.toString());
                crack(message, sessionID);
            } catch (UnsupportedMessageType unsupportedMessageType) {
                unsupportedMessageType.printStackTrace();
            } catch (FieldNotFound fieldNotFound) {
                fieldNotFound.printStackTrace();
            } catch (IncorrectTagValue incorrectTagValue) {
                incorrectTagValue.printStackTrace();
            }
        }

        public void onMessage(BusinessMessageReject reject, SessionID sessionID){

        }

        public synchronized void sendFixMessage(OutMessageFix outMessageFix){
            try {
                fixLogger.info("Sending execution message");
                Session.sendToTarget(outMessageFix.getFixMessage(), outMessageFix.getSessionID());
                fixServiceDao.addToFixStore(Constants.FIX_MSG_OUT,outMessageFix.getFixMessage().toString());
            } catch (SessionNotFound sessionNotFound) {
                sessionNotFound.printStackTrace();
            }
        }

    }

}
