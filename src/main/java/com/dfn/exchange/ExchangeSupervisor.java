package com.dfn.exchange;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.dfn.exchange.ado.DataService;
import com.dfn.exchange.ado.OrderDao;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.*;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.NewOrderSingle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by darshanas on 11/2/2017.
 */
public class ExchangeSupervisor extends UntypedActor {

    private ActorRef feedHandler = null;
    private ActorRef tradeHandler = null;
    private ActorRef tradeHelper = null;
    private ActorRef feedHelper = null;
    private Map<String,ActorRef> symbolActorMap = new HashMap<>();
    private OrderDao orderDao = null;


    @Override
    public void preStart() throws Exception {
        super.preStart();
        this.orderDao = DataService.getInstance(Constants.DB).getDbi().onDemand(OrderDao.class);
        feedHandler = getContext().actorOf(Props.create(FeedHandler.class), "feedHandler");
        tradeHandler = getContext().actorOf(Props.create(FixHandler.class),"tradeHandler");
//        Constants.getSymbols().forEach(s -> {
//            ActorRef ref = getContext().actorOf(Props.create(SymbolActor.class,s,tradeHandler,feedHandler),s);
//            symbolActorMap.put(s,ref);
//        });
        SymbolSettings.getSymbolList().forEach(symbol -> {
            ActorRef ref = getContext().actorOf(Props.create(SymbolActor.class,symbol.getSymbolCode(),tradeHandler,feedHandler),
                    symbol.getSymbolCode());
            symbolActorMap.put(symbol.getSymbolCode(),ref);
        });
        tradeHandler = getContext().actorOf(Props.create(TradeHelper.class));
        feedHelper = getContext().actorOf(Props.create(FeedHelper.class));
        ExecutionCounter.setLastExecutionId(orderDao.getLastTradeMatchId());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println("The exchange received a message");
        if(message instanceof InMessageFix){
            feedHandler.tell(message,getSelf());
            InMessageFix inf = (InMessageFix) message;
            System.out.println("Sender Comp " + inf.getSessionID().getSenderCompID());
            System.out.println("Target Comp " + inf.getSessionID().getTargetCompID());
            if(inf.getFixMessage() instanceof NewOrderSingle){
                NewOrderSingle order = (NewOrderSingle)inf.getFixMessage();
                String symbol = order.getSymbol().getValue();
                ActorRef ref = symbolActorMap.get(symbol);
                if(ref != null){
                    acceptOrder(order,getSender(),inf.getSessionID());
                    ref.tell(message,getSelf());
                }else {
                    rejectOrder(order,getSender(),inf.getSessionID());
                }

            }
        }
    }

    private void acceptOrder(NewOrderSingle order, ActorRef fix, SessionID sessionID) throws FieldNotFound {
        OrderQty qty = new OrderQty(order.getOrderQty().getValue());
        ExecutionReport executionReport = new ExecutionReport();
        executionReport.set(new OrderID(order.getClOrdID().getValue()));
        executionReport.set(order.getClOrdID());
        executionReport.set(new ExecID("0000000000"));
        executionReport.set(new ExecTransType(ExecTransType.NEW));
        executionReport.set(new ExecType(ExecType.FILL));
        executionReport.set(new OrdStatus(OrdStatus.NEW));
        executionReport.set(order.getSymbol());
        executionReport.set(order.getSide());
        executionReport.set(new LeavesQty(qty.getValue()));
        executionReport.set(new CumQty(0.0));
        executionReport.set(new AvgPx(105));
        executionReport.set(new ClientID("1"));
        OutMessageFix outMessageFix = new OutMessageFix(executionReport,sessionID);
        fix.tell(outMessageFix,getSelf());
    }

    private void rejectOrder(NewOrderSingle order, ActorRef fix, SessionID sessionID) throws FieldNotFound {
        OrderQty qty = new OrderQty(order.getOrderQty().getValue());
        ExecutionReport executionReport = new ExecutionReport();
        executionReport.set(new OrderID(order.getClOrdID().getValue()));
        executionReport.set(order.getClOrdID());
        executionReport.set(new ExecID("0000000000"));
        executionReport.set(new ExecTransType(ExecTransType.NEW));
        executionReport.set(new ExecType(ExecType.REJECTED));
        executionReport.set(new OrdStatus(OrdStatus.REJECTED));
        executionReport.set(order.getSymbol());
        executionReport.set(order.getSide());
        executionReport.set(new LeavesQty(qty.getValue()));
        executionReport.set(new CumQty(0.0));
        executionReport.set(new AvgPx(0.0));
        executionReport.set(new OrdRejReason(1));
        executionReport.set(new ClientID("1"));
        OutMessageFix outMessageFix = new OutMessageFix(executionReport,sessionID);
        fix.tell(outMessageFix,getSelf());
    }

}
