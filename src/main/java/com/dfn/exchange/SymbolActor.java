package com.dfn.exchange;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.dfn.exchange.ado.DataService;
import com.dfn.exchange.ado.OrderDao;
import com.dfn.exchange.beans.OrderBook;
import com.dfn.exchange.beans.OrderBookRaw;
import com.dfn.exchange.beans.OrderEntity;
import com.google.gson.Gson;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.*;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.NewOrderSingle;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by darshanas on 11/2/2017.
 */
public class SymbolActor extends UntypedActor {

    private static final Logger logger = LogManager.getLogger(SymbolActor.class);

    private String symbolName;
    private long tradeVolume;
    private double lastTradePrice;
    private OrderDao orderDao = null;
    private ActorRef fixHandler = null;
    private ActorRef feedHandler = null;
    Gson gson = new Gson();
    private Map<String, SessionID> orderSessions = new HashMap<>();
    private long orderEntryTime;

    public SymbolActor(String symbolName, ActorRef fixHander, ActorRef feedHander){
        this.symbolName = symbolName;
        this.fixHandler = fixHander;
        this.feedHandler = feedHander;
        this.orderDao = DataService.getInstance(Constants.DB).getDbi().onDemand(OrderDao.class);
    }


    @Override
    public void preStart() throws Exception {
        super.preStart();
        logger.info("Symbol " + symbolName + " has been created");
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if(message instanceof InMessageFix){
            InMessageFix input = (InMessageFix)message;
            if(input.getFixMessage() instanceof NewOrderSingle){
                orderEntryTime = System.currentTimeMillis();
                NewOrderSingle newOrder = (NewOrderSingle)input.getFixMessage();
                saveOrder(newOrder,input.getSessionID().getTargetCompID());
                orderSessions.put(newOrder.getClOrdID().getValue(),input.getSessionID());

                if(newOrder.getOrdType().getValue() == OrdType.LIMIT){
                    List<OrderEntity> buyOrders = orderDao.getBuyLimitOrders(this.symbolName);
                    List<OrderEntity> sellOrders = orderDao.getSellLimitOrders(this.symbolName);
                    transmitOrderBook(buyOrders,sellOrders);
                    matchLimitOrders(buyOrders,sellOrders);
                }else {
                    matchMarketOrder(newOrder);
                }

            }
        }

    }


    private void matchMarketOrder(NewOrderSingle orderSingle) throws FieldNotFound{



        if(orderSingle.getSide().getValue() == Side.BUY){

            List<OrderEntity> limitSellOrders = orderDao.getSellLimitOrders(orderSingle.getSymbol().getValue());
            matchMktBuyOrder(orderSingle,limitSellOrders);

        }else if(orderSingle.getSide().getValue() == Side.SELL){

            List<OrderEntity> limitBuyOrders = orderDao.getBuyLimitOrders(orderSingle.getSymbol().getValue());
            matchMktSellOrder(orderSingle,limitBuyOrders);

        }else {

            logger.info("Unknown Order Type " + orderSingle.getSide().getValue());

        }



    }

    private void matchMktBuyOrder(NewOrderSingle mktOrder, List<OrderEntity> limitSellOrders) throws FieldNotFound{

        double orderQty = mktOrder.getOrderQty().getValue();
        double remainingQty = mktOrder.getOrderQty().getValue();

        for(OrderEntity entity : limitSellOrders){

            if(entity.getRemainingQty() == orderQty){
                String executionId = ExecutionCounter.getTradeExecutionId(symbolName);
                fillOrder(entity,executionId);
                OrderEntity x = orderDao.getOrder(mktOrder.getClOrdID().getValue());
                x.setPrice(entity.getPrice());
                fillOrder(x, executionId);
                orderDao.updateTradeMatch(executionId, orderQty, entity.getPrice(), entity.getOrderId(), x.getOrderId());
                orderDao.addOrderExecution(executionId, entity.getOrderId(), orderQty, entity.getPrice());
                orderDao.addOrderExecution(executionId, x.getOrderId(), orderQty, entity.getPrice());
                long time = System.currentTimeMillis() - orderEntryTime;
                System.out.println("******** MATCHING TIME " + time + " ms ********");
                break;
            }else if(entity.getRemainingQty() > remainingQty){
                // mkt order is filling
                String executionId = ExecutionCounter.getTradeExecutionId(symbolName);
                OrderEntity mktOrdEntity = orderDao.getOrder(mktOrder.getClOrdID().getValue());
                mktOrdEntity.setPrice(entity.getPrice());
                fillOrder(mktOrdEntity, executionId);
                entity.setExecutedQty(entity.getExecutedQty() + remainingQty);
                entity.setRemainingQty(entity.getQty() - entity.getExecutedQty());
                partialFillOrder(entity, executionId);
                orderDao.updateTradeMatch(executionId, remainingQty, entity.getPrice(), entity.getOrderId(), mktOrdEntity.getOrderId());
                orderDao.addOrderExecution(executionId, entity.getOrderId(), remainingQty, entity.getPrice());
                orderDao.addOrderExecution(executionId,mktOrdEntity.getOrderId(),remainingQty,entity.getPrice());
                long time = System.currentTimeMillis() - orderEntryTime;
                System.out.println("******** MATCHING TIME " + time + " ms ********");
                break;
            }else if(entity.getRemainingQty() < remainingQty){
                // limit order will be fill and mkt order will be Partially filled
                String executionId = ExecutionCounter.getTradeExecutionId(symbolName);
                OrderEntity mktOrdEntry = orderDao.getOrder(mktOrder.getClOrdID().getValue());
                mktOrdEntry.setPrice(entity.getPrice());
                mktOrdEntry.setExecutedQty(mktOrdEntry.getExecutedQty() + entity.getRemainingQty());
                mktOrdEntry.setRemainingQty(mktOrdEntry.getQty() - mktOrdEntry.getExecutedQty());
                partialFillOrder(mktOrdEntry, executionId);
                fillOrder(entity, executionId);
                orderDao.updateTradeMatch(executionId, entity.getRemainingQty(), entity.getPrice(), entity.getOrderId(), mktOrdEntry.getOrderId());
                orderDao.addOrderExecution(executionId, entity.getOrderId(), entity.getRemainingQty(), entity.getPrice());
                orderDao.addOrderExecution(executionId,mktOrdEntry.getOrderId(),entity.getRemainingQty(),entity.getPrice());
                long time = System.currentTimeMillis() - orderEntryTime;
                System.out.println("******** MATCHING TIME " + time + " ms ********");
            }

        }

    }

    private void matchMktSellOrder(NewOrderSingle mktOrder, List<OrderEntity> limitBuyOrders) throws FieldNotFound{

        double orderQty = mktOrder.getOrderQty().getValue();
        double remainingQty = mktOrder.getOrderQty().getValue();

        for(OrderEntity entity : limitBuyOrders){
           if(entity.getRemainingQty() == orderQty){
               String executionId = ExecutionCounter.getTradeExecutionId(symbolName);
               fillOrder(entity,executionId);
               OrderEntity x = orderDao.getOrder(mktOrder.getClOrdID().getValue());
               x.setPrice(entity.getPrice());
               fillOrder(x, executionId);
               orderDao.updateTradeMatch(executionId,orderQty,entity.getPrice(),x.getOrderId(),entity.getOrderId());
               orderDao.addOrderExecution(executionId, entity.getOrderId(), orderQty, entity.getPrice());
               orderDao.addOrderExecution(executionId,x.getOrderId(),orderQty,entity.getPrice());
               long time = System.currentTimeMillis() - orderEntryTime;
               System.out.println("******** MATCHING TIME " + time + " ms ********");
               break;
           }else if(entity.getRemainingQty() > remainingQty){

               String executionId = ExecutionCounter.getTradeExecutionId(symbolName);
               OrderEntity mktOrdEntity = orderDao.getOrder(mktOrder.getClOrdID().getValue());
               mktOrdEntity.setPrice(entity.getPrice());
               fillOrder(mktOrdEntity, executionId);
               entity.setExecutedQty(entity.getExecutedQty() + remainingQty);
               entity.setRemainingQty(entity.getQty() - entity.getExecutedQty());
               partialFillOrder(entity, executionId);
               orderDao.updateTradeMatch(executionId,remainingQty,entity.getPrice(),entity.getOrderId(),mktOrdEntity.getOrderId());
               orderDao.addOrderExecution(executionId, entity.getOrderId(), remainingQty, entity.getPrice());
               orderDao.addOrderExecution(executionId,mktOrdEntity.getOrderId(),remainingQty,entity.getPrice());
               long time = System.currentTimeMillis() - orderEntryTime;
               System.out.println("******** MATCHING TIME " + time + " ms ********");
               break;

           }else if(entity.getRemainingQty() < remainingQty){
               // limit order will be fill and mkt order will be Partially filled
               String executionId = ExecutionCounter.getTradeExecutionId(symbolName);
               OrderEntity mktOrdEntry = orderDao.getOrder(mktOrder.getClOrdID().getValue());
               mktOrdEntry.setPrice(entity.getPrice());
               mktOrdEntry.setExecutedQty(mktOrdEntry.getExecutedQty() + entity.getRemainingQty());
               mktOrdEntry.setRemainingQty(mktOrdEntry.getQty() - mktOrdEntry.getExecutedQty());
               partialFillOrder(mktOrdEntry, executionId);
               fillOrder(entity, executionId);
               orderDao.updateTradeMatch(executionId, entity.getRemainingQty(), entity.getPrice(), entity.getOrderId(), mktOrdEntry.getOrderId());
               orderDao.addOrderExecution(executionId, entity.getOrderId(), entity.getRemainingQty(), entity.getPrice());
               orderDao.addOrderExecution(executionId,mktOrdEntry.getOrderId(),entity.getRemainingQty(),entity.getPrice());
               long time = System.currentTimeMillis() - orderEntryTime;
               System.out.println("******** MATCHING TIME " + time + " ms ********");
           }
        }

    }

    private void saveOrder(NewOrderSingle order,String traderId){
        try {
            double price = 0;
            if(order.getOrdType().getValue() == OrdType.LIMIT){
                price = order.getPrice().getValue();
            }
            orderDao.createOrder(order.getClOrdID().getValue(),traderId,order.getSymbol().getValue(),
                    order.getOrderQty().getValue(),price,order.getOrdType().getValue(),order.getSide().getValue(),
                    order.getTimeInForce().getValue(),System.currentTimeMillis(), OrdStatus.NEW,
                    0l,order.getOrderQty().getValue());
        } catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();
        }
    }

    private void matchLimitOrders(List<OrderEntity> buyOrders,List<OrderEntity> sellOrders) throws FieldNotFound {

        logger.info("Start matching limit orders");

        int loopLength = 0;
        if(buyOrders != null && sellOrders != null){
            if(buyOrders.size() > sellOrders.size()){
                loopLength = sellOrders.size();
            }else {
                loopLength = buyOrders.size();
            }
        }else {
            return;
        }

        for(int i = 0; i < loopLength; i++){
            OrderEntity buyOd = buyOrders.get(i);
            OrderEntity sellOd = sellOrders.get(i);
            double buyOrderPrice = buyOd.getPrice();
            double sellOrderPrice = sellOd.getPrice();
            if(buyOrderPrice == sellOrderPrice || buyOrderPrice > sellOrderPrice){
                // match
                lastTradePrice = buyOrderPrice;
                executeLimitOrderMatch(buyOd,sellOd);
            }

        }


    }


    private void executeLimitOrderMatch(OrderEntity buyOd,OrderEntity sellOd){



        if (buyOd.getRemainingQty() == sellOd.getRemainingQty()){
            String executionId = ExecutionCounter.getTradeExecutionId(symbolName);
            fillOrder(buyOd,executionId);
            fillOrder(sellOd, executionId);

            orderDao.updateTradeMatch(executionId, buyOd.getRemainingQty(),
                    buyOd.getPrice(), sellOd.getOrderId(), buyOd.getOrderId());
            orderDao.addOrderExecution(executionId, buyOd.getOrderId(), buyOd.getRemainingQty(), buyOd.getPrice());
            orderDao.addOrderExecution(executionId,sellOd.getOrderId(),sellOd.getRemainingQty(),sellOd.getPrice());
            long time = System.currentTimeMillis() - orderEntryTime;
            System.out.println("******** MATCHING TIME " + time + " ms ********");
        }else if(buyOd.getRemainingQty() < sellOd.getRemainingQty()){
            String executionId = ExecutionCounter.getTradeExecutionId(symbolName);
            fillOrder(buyOd,executionId);
            sellOd.setExecutedQty(sellOd.getExecutedQty() + buyOd.getRemainingQty());
            sellOd.setRemainingQty(sellOd.getQty() - sellOd.getExecutedQty());
            partialFillOrder(sellOd, executionId);
            orderDao.updateTradeMatch(executionId, buyOd.getRemainingQty(),
                    buyOd.getPrice(), sellOd.getOrderId(), buyOd.getOrderId());
            orderDao.addOrderExecution(executionId, buyOd.getOrderId(), buyOd.getRemainingQty(), buyOd.getPrice());
            orderDao.addOrderExecution(executionId,sellOd.getOrderId(),buyOd.getRemainingQty(),buyOd.getPrice());
            long time = System.currentTimeMillis() - orderEntryTime;
            System.out.println("******** MATCHING TIME " + time + " ms ********");
        }else if(buyOd.getRemainingQty() > sellOd.getRemainingQty()){
            String executionId = ExecutionCounter.getTradeExecutionId(symbolName);
            fillOrder(sellOd,executionId);
            buyOd.setExecutedQty(buyOd.getExecutedQty() + sellOd.getRemainingQty());
            buyOd.setRemainingQty(buyOd.getQty() - buyOd.getExecutedQty());
            partialFillOrder(buyOd, executionId);
            orderDao.updateTradeMatch(ExecutionCounter.getTradeExecutionId(symbolName), sellOd.getRemainingQty(),
                    buyOd.getPrice(), sellOd.getOrderId(), buyOd.getOrderId());
            orderDao.addOrderExecution(executionId, buyOd.getOrderId(), sellOd.getRemainingQty(), buyOd.getPrice());
            orderDao.addOrderExecution(executionId, sellOd.getOrderId(), sellOd.getRemainingQty(), sellOd.getPrice());
            long time = System.currentTimeMillis() - orderEntryTime;
            System.out.println("******** MATCHING TIME " + time + " ms ********");
        }else {
            logger.info("Unknown matching case.");
            return;
        }



        transmitOrderBook(orderDao.getBuyLimitOrders(buyOd.getSymbol()),orderDao.getSellLimitOrders(sellOd.getSymbol()));

    }

    private void fillOrder(OrderEntity orderEntity,String executionId){
        orderDao.updateOrdFill(orderEntity.getOrderId(),orderEntity.getQty());
        ExecutionReport report = getExecutionReport(orderEntity,OrdStatus.FILLED,executionId);
        SessionID sessionID = orderSessions.get(orderEntity.getOrderId());
        OutMessageFix outMessageFix = new OutMessageFix(report,sessionID);
        fixHandler.tell(outMessageFix,getSelf());
        orderSessions.remove(orderEntity.getOrderId());
    }

    public void partialFillOrder(OrderEntity order, String executionId){
        orderDao.partialFill(order.getOrderId(),order.getExecutedQty(),order.getRemainingQty());
        ExecutionReport report = getExecutionReport(order,OrdStatus.PARTIALLY_FILLED,executionId);
        SessionID sessionID = orderSessions.get(order.getOrderId());
        OutMessageFix outMessageFix = new OutMessageFix(report,sessionID);
        fixHandler.tell(outMessageFix,getSelf());
    }

    private void transmitOrderBook(List<OrderEntity> buyOrders,List<OrderEntity> sellOrders){

        int loopLength = 0;
        if((buyOrders != null && buyOrders.size() > 0) && (sellOrders != null && sellOrders.size() > 0)){
            if(buyOrders.size() > sellOrders.size()){
                loopLength = sellOrders.size();
            }else {
                loopLength = buyOrders.size();
            }
        }else {
            return;
        }

        List<OrderBookRaw> orderBookRaws = new ArrayList<>(loopLength);

        for(int i = 0; i < loopLength; i++){
            OrderEntity buy = buyOrders.get(i);
            OrderEntity sell = sellOrders.get(i);
            OrderBookRaw orderBookRaw = new OrderBookRaw(i,getTimeStmpString(new Date(buy.getOrdTime())),buy.getQty(),buy.getPrice(),
                    getTimeStmpString(new Date(sell.getOrdTime())),sell.getQty(),sell.getPrice());
            orderBookRaws.add(orderBookRaw);
        }

        OrderBook orderBook = new OrderBook(orderBookRaws);
        orderBook.setMessageType('B');
        String json = gson.toJson(orderBook);
        feedHandler.tell(json, getSelf());


    }

    public String getTimeStmpString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        return sdf.format(date);
    }

    private ExecutionReport getExecutionReport(OrderEntity order,char ordStatus, String executionId){
        ExecutionReport report = new ExecutionReport();
        report.set(new OrderQty(order.getQty()));
        report.set(new OrderID(order.getOrderId()));
        report.set(new ClOrdID(order.getOrderId()));
        report.set(new ExecID(executionId));
        report.set(new ExecTransType(ExecTransType.STATUS));
        report.set(new ExecType(ordStatus));
        report.set(new OrdStatus(ordStatus));
        report.set(new Symbol(order.getSymbol()));
        report.set(new Side(order.getOrdSide()));
        report.set(new LeavesQty(order.getRemainingQty()));
        report.set(new CumQty(order.getExecutedQty()));
        report.set(new AvgPx(orderDao.getAveragePrice(order.getOrderId())));
        report.set(new ClientID("1"));
        return report;
    }

}
