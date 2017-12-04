package com.dfn.exchange;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.dfn.exchange.ado.CustomerDao;
import com.dfn.exchange.ado.DataService;
import com.dfn.exchange.ado.OrderDao;
import com.dfn.exchange.beans.*;
import com.dfn.exchange.utils.TimeUtils;
import com.google.gson.Gson;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.*;
import quickfix.field.Symbol;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.NewOrderSingle;
import quickfix.fix42.OrderCancelReplaceRequest;
import quickfix.fix42.OrderCancelRequest;

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
    private CustomerDao customerDao = null;
    private ActorRef fixHandler = null;
    private ActorRef feedHandler = null;
    Gson gson = new Gson();
    private Map<String, SessionID> orderSessions = new HashMap<>();
    private long orderEntryTime;
    private double buyOrdVol;
    private double sellOrdVol;
    private double executedVol = 0;

    public SymbolActor(String symbolName, ActorRef fixHander, ActorRef feedHander) {
        this.symbolName = symbolName;
        this.fixHandler = fixHander;
        this.feedHandler = feedHander;
        this.orderDao = DataService.getInstance(Constants.DB).getDbi().onDemand(OrderDao.class);
        this.customerDao = DataService.getInstance(Constants.DB).getDbi().onDemand(CustomerDao.class);
    }


    @Override
    public void preStart() throws Exception {
        super.preStart();
        logger.info("Symbol " + symbolName + " has been created");
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof InMessageFix) {
            InMessageFix input = (InMessageFix) message;
            if (input.getFixMessage() instanceof NewOrderSingle) {
                orderEntryTime = System.currentTimeMillis();
                NewOrderSingle newOrder = (NewOrderSingle) input.getFixMessage();
                saveOrder(newOrder, input.getSessionID().getTargetCompID());
                updateVolume( '+',newOrder.getOrderQty().getValue(), newOrder.getSide().getValue());
                orderSessions.put(newOrder.getClOrdID().getValue(), input.getSessionID());

                if (newOrder.getOrdType().getValue() == OrdType.LIMIT) {
                    if (newOrder.getSide().getValue() == Side.BUY) { // if new Order is Buy get the matching sell orders.
                        OrderEntity newBuyOrder = orderDao.getOrder(newOrder.getClOrdID().getValue());
                        List<OrderEntity> sellOrders = orderDao.getMatchingSellLimitOrders(this.symbolName, newOrder.getPrice().getValue());
                        if (sellOrders != null && sellOrders.size() > 0) {
                            matchLimitOrders(newBuyOrder, sellOrders);
                        }
                    } else{
                        OrderEntity newSellOrder = orderDao.getOrder(newOrder.getClOrdID().getValue());
                        List<OrderEntity> buyOrders = orderDao.getMatchingBuyLimitOrders(this.symbolName, newOrder.getPrice().getValue());
                        if (buyOrders != null && buyOrders.size() > 0) {
                            matchLimitOrders(newSellOrder, buyOrders);
                        }
                    }

                    List<OrderEntity> buyOrders = orderDao.getBuyLimitOrders(this.symbolName);
                    List<OrderEntity> sellOrders = orderDao.getSellLimitOrders(this.symbolName);
                    transmitOrderBook(buyOrders, sellOrders);
                    // matchLimitOrders(buyOrders,sellOrders);
                } else {
                    matchMarketOrder(newOrder);
                }

            } else if (input.getFixMessage() instanceof OrderCancelRequest) {

            } else if(input.getFixMessage() instanceof OrderCancelReplaceRequest){
                OrderCancelReplaceRequest amendOrder = (OrderCancelReplaceRequest) input.getFixMessage();
                OrderEntity modifiedOrder = orderDao.getOrder(amendOrder.getClOrdID().getValue());
                modifiedOrder.setRemainingQty(modifiedOrder.getRemainingQty() + amendOrder.getOrderQty().getValue() - modifiedOrder.getQty());
                modifiedOrder.setPrice(amendOrder.getPrice().getValue());
                modifiedOrder.setQty(amendOrder.getOrderQty().getValue());
                modifiedOrder.setOrdSide(amendOrder.getSide().getValue());
                modifiedOrder.setOrdType(amendOrder.getOrdType().getValue());

                orderDao.updateOrder(amendOrder.getClOrdID().getValue(), modifiedOrder.getQty(), modifiedOrder.getRemainingQty(), modifiedOrder.getPrice(), String.valueOf(modifiedOrder.getOrdType()), String.valueOf(modifiedOrder.getOrdSide()));

                if (amendOrder.getOrdType().getValue() == OrdType.LIMIT) {
                    if (amendOrder.getSide().getValue() == Side.BUY) { // if new Order is Buy get the matching sell orders.
                        List<OrderEntity> sellOrders = orderDao.getMatchingSellLimitOrders(this.symbolName, modifiedOrder.getPrice());
                        if (sellOrders != null && sellOrders.size() > 0) {
                            matchLimitOrders(modifiedOrder, sellOrders);
                        }
                    } else{
                        List<OrderEntity> buyOrders = orderDao.getMatchingBuyLimitOrders(this.symbolName, modifiedOrder.getPrice());
                        if (buyOrders != null && buyOrders.size() > 0) {
                            matchLimitOrders(modifiedOrder, buyOrders);
                        }
                    }

                    List<OrderEntity> buyOrders = orderDao.getBuyLimitOrders(this.symbolName);
                    List<OrderEntity> sellOrders = orderDao.getSellLimitOrders(this.symbolName);
                    transmitOrderBook(buyOrders, sellOrders);
                    // matchLimitOrders(buyOrders,sellOrders);
                } else {
                    NewOrderSingle modifiedSingleOrder = new NewOrderSingle();
                    modifiedSingleOrder.set(new ClOrdID(modifiedOrder.getOrderId()));
                    modifiedSingleOrder.set(new Symbol(modifiedOrder.getSymbol()));
                    modifiedSingleOrder.set(new Price(modifiedOrder.getPrice()));
                    modifiedSingleOrder.set(new OrderQty(modifiedOrder.getQty()));
                    matchMarketOrder(modifiedSingleOrder);
                }

            }
        }

    }


    private void updateVolume(char operation,double qty, char side) throws FieldNotFound {

        if(operation == '+'){
            if(side == Side.BUY){
                buyOrdVol = buyOrdVol + qty;
            }else if(side == Side.SELL){
                sellOrdVol = sellOrdVol + qty;
            }
        }else if(operation == '-'){
            if(side == Side.BUY){
                buyOrdVol = buyOrdVol - qty;
            }else if(side == Side.SELL){
                sellOrdVol = sellOrdVol - qty;
            }
        }

        MarketVolume marketVolume = new MarketVolume(sellOrdVol,buyOrdVol,executedVol);
        feedHandler.tell(marketVolume, getSelf());

    }

    public void updateVolume(double qty){
        sellOrdVol = sellOrdVol - qty;
        buyOrdVol = buyOrdVol - qty;
        MarketVolume marketVolume = new MarketVolume(sellOrdVol,buyOrdVol,executedVol);
        feedHandler.tell(marketVolume, getSelf());
    }

    private void matchLimitOrders(OrderEntity newOrder, List<OrderEntity> counterOrderList) throws FieldNotFound {

        /*logger.info("Start matching limit orders");

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

        }*/
        logger.info("*****Start matching limit orders******");
        for (OrderEntity counterOrder : counterOrderList) {
            String status = executeLimitOrderMatch(newOrder, counterOrder);
            if(status.equalsIgnoreCase(com.dfn.exchange.utils.Constants.EXECUTION_ORDER_NOT_MATCHING) ||
                    status.equalsIgnoreCase(com.dfn.exchange.utils.Constants.EXECUTION_ORDER_FILLED)){
               break;
            }
        }


    }

    private String executeLimitOrderMatch(OrderEntity newOrder, OrderEntity counterOrder) {

        boolean isMatched = false;

        if (newOrder.getRemainingQty() == counterOrder.getRemainingQty() ) { // newOrder and counter order filled

            isMatched = true;
            executedVol = executedVol + newOrder.getRemainingQty();
            String executionIdNewOrder = ExecutionCounter.getTradeExecutionId(symbolName);
            String executionIdCounterOrder = ExecutionCounter.getTradeExecutionId(symbolName);
            fillOrder(newOrder, executionIdNewOrder);
            fillOrder(counterOrder, executionIdCounterOrder);
            updateVolume(newOrder.getRemainingQty());
            String buyOrderId = null;
            String sellOrderId = null;
            if(String.valueOf(newOrder.getOrdSide()).equalsIgnoreCase(String.valueOf(Side.BUY))){
                buyOrderId = newOrder.getOrderId();
                sellOrderId = counterOrder.getOrderId();
            }else{
                buyOrderId = counterOrder.getOrderId();
                sellOrderId = newOrder.getOrderId();
            }
            double executedPrice = newOrder.getPrice() <= counterOrder.getPrice() ? newOrder.getPrice(): counterOrder.getPrice();

            feedHandler.tell(new TradeMatch(executionIdNewOrder, newOrder.getRemainingQty(),
                    executedPrice, TimeUtils.getTimeString(),buyOrderId,sellOrderId), getSelf());

            orderDao.updateTradeMatch(executionIdNewOrder, newOrder.getRemainingQty(),
                    executedPrice, sellOrderId, buyOrderId); // todo need change executionIdNewOrder to transactionID

            orderDao.addOrderExecution(executionIdNewOrder, newOrder.getOrderId(), newOrder.getRemainingQty(), executedPrice);
            orderDao.addOrderExecution(executionIdCounterOrder, counterOrder.getOrderId(), counterOrder.getRemainingQty(), executedPrice);
            transferHolding(newOrder,counterOrder,newOrder.getRemainingQty());

            long time = System.currentTimeMillis() - orderEntryTime;
            System.out.println("******** MATCHING TIME " + time + " ms ********");
            transmitUpdatedOrderBook();
            return com.dfn.exchange.utils.Constants.EXECUTION_ORDER_FILLED;

        } else if (newOrder.getRemainingQty() > counterOrder.getRemainingQty()) {  // partially fill new Order full fill counter order
            isMatched = true;
            String executionIdNewOrder = ExecutionCounter.getTradeExecutionId(symbolName);
            String executionIdCounterOrder = ExecutionCounter.getTradeExecutionId(symbolName);
            executedVol = executedVol + counterOrder.getQty();
            newOrder.setExecutedQty(newOrder.getExecutedQty() + counterOrder.getRemainingQty());
            newOrder.setRemainingQty(newOrder.getRemainingQty() - counterOrder.getRemainingQty());
            partialFillOrder(newOrder, executionIdNewOrder);

            counterOrder.setExecutedQty(counterOrder.getQty());
            fillOrder(counterOrder, executionIdCounterOrder);

            String buyOrderId = null;
            String sellOrderId = null;
            if(String.valueOf(newOrder.getOrdSide()).equalsIgnoreCase(String.valueOf(Side.BUY))){
                buyOrderId = newOrder.getOrderId();
                sellOrderId = counterOrder.getOrderId();
            }else{
                buyOrderId = counterOrder.getOrderId();
                sellOrderId = newOrder.getOrderId();
            }

            double executedPrice = newOrder.getPrice() <= counterOrder.getPrice() ? newOrder.getPrice(): counterOrder.getPrice();

            feedHandler.tell(new TradeMatch(executionIdNewOrder,newOrder.getRemainingQty(),
                    executedPrice, TimeUtils.getTimeString(),buyOrderId,sellOrderId), getSelf());

            orderDao.updateTradeMatch(executionIdNewOrder, counterOrder.getRemainingQty(),
                    executedPrice, sellOrderId, buyOrderId); //todo need change executionIdNewOrder to transactionID

            updateVolume(counterOrder.getQty());

            orderDao.addOrderExecution(executionIdNewOrder, newOrder.getOrderId(), counterOrder.getRemainingQty(), executedPrice);
            orderDao.addOrderExecution(executionIdCounterOrder, counterOrder.getOrderId(), counterOrder.getRemainingQty(), executedPrice);
            transferHolding(newOrder,counterOrder,counterOrder.getRemainingQty());

            long time = System.currentTimeMillis() - orderEntryTime;
            System.out.println("******** MATCHING TIME " + time + " ms ********");
            transmitUpdatedOrderBook();
            return com.dfn.exchange.utils.Constants.EXECUTION_ORDER_PARTIALLY_FILLED;
        }else if(newOrder.getRemainingQty() < counterOrder.getRemainingQty()){ //   full fill new Order partially fill counter order
            isMatched = true;
            String executionIdNewOrder = ExecutionCounter.getTradeExecutionId(symbolName);
            String executionIdCounterOrder = ExecutionCounter.getTradeExecutionId(symbolName);

            counterOrder.setExecutedQty(counterOrder.getExecutedQty() + newOrder.getRemainingQty());
            counterOrder.setRemainingQty(counterOrder.getQty() - counterOrder.getExecutedQty());
            partialFillOrder(counterOrder, executionIdCounterOrder);

            fillOrder(newOrder, executionIdNewOrder);

            String buyOrderId = null;
            String sellOrderId = null;
            if(String.valueOf(newOrder.getOrdSide()).equalsIgnoreCase(String.valueOf(Side.BUY))){
                buyOrderId = newOrder.getOrderId();
                sellOrderId = counterOrder.getOrderId();
            }else{
                buyOrderId = counterOrder.getOrderId();
                sellOrderId = newOrder.getOrderId();
            }

            double executedPrice = newOrder.getPrice() <= counterOrder.getPrice() ? newOrder.getPrice(): counterOrder.getPrice();

            feedHandler.tell(new TradeMatch(executionIdNewOrder,newOrder.getRemainingQty(),
                    executedPrice, TimeUtils.getTimeString(),buyOrderId,sellOrderId), getSelf());

            orderDao.updateTradeMatch(executionIdNewOrder, newOrder.getRemainingQty(),
                    executedPrice, sellOrderId, buyOrderId);  //todo need change executionIdNewOrder to transactionID

            executedVol = executedVol + newOrder.getRemainingQty();
            updateVolume(newOrder.getRemainingQty());

            orderDao.addOrderExecution(executionIdNewOrder, newOrder.getOrderId(), newOrder.getRemainingQty(), executedPrice);
            orderDao.addOrderExecution(executionIdCounterOrder, counterOrder.getOrderId(), newOrder.getRemainingQty(),executedPrice);
            transferHolding(newOrder,counterOrder,newOrder.getRemainingQty());

            long time = System.currentTimeMillis() - orderEntryTime;
            System.out.println("******** MATCHING TIME " + time + " ms ********");
            transmitUpdatedOrderBook();
            return com.dfn.exchange.utils.Constants.EXECUTION_ORDER_FILLED;
        } else {
            logger.info("Unknown matching case.");
            return com.dfn.exchange.utils.Constants.EXECUTION_ORDER_NOT_MATCHING;

        }



    }

  /*  private void executeLimitOrderMatch(OrderEntity buyOd, OrderEntity sellOd){

        if (buyOd.getRemainingQty() == sellOd.getRemainingQty()){
            String executionIdBuyOrder = ExecutionCounter.getTradeExecutionId(symbolName);
            String executionIdSellOrder = ExecutionCounter.getTradeExecutionId(symbolName);
            fillOrder(buyOd, executionIdBuyOrder);
            fillOrder(sellOd, executionIdSellOrder);

            orderDao.updateTradeMatch(executionIdBuyOrder, buyOd.getRemainingQty(),
                    buyOd.getPrice(), sellOd.getOrderId(), buyOd.getOrderId());
            orderDao.addOrderExecution(executionIdBuyOrder, buyOd.getOrderId(), buyOd.getRemainingQty(), buyOd.getPrice());
            orderDao.addOrderExecution(executionIdSellOrder, sellOd.getOrderId(),sellOd.getRemainingQty(),sellOd.getPrice());
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

    }*/



    private void matchMarketOrder(NewOrderSingle orderSingle) throws FieldNotFound {


        if (orderSingle.getSide().getValue() == Side.BUY) {

            List<OrderEntity> limitSellOrders = orderDao.getSellLimitOrders(orderSingle.getSymbol().getValue());
            if(limitSellOrders.size() <= 0)
                rejectOrder(orderSingle);

            matchMktBuyOrder(orderSingle, limitSellOrders);

        } else if (orderSingle.getSide().getValue() == Side.SELL) {

            List<OrderEntity> limitBuyOrders = orderDao.getBuyLimitOrders(orderSingle.getSymbol().getValue());
            if(limitBuyOrders.size() <= 0)
                rejectOrder(orderSingle);
            matchMktSellOrder(orderSingle, limitBuyOrders);

        } else {

            logger.info("Unknown Order Type " + orderSingle.getSide().getValue());

        }


    }

    private void matchMktBuyOrder(NewOrderSingle mktOrder, List<OrderEntity> limitSellOrders) throws FieldNotFound {

        double orderQty = mktOrder.getOrderQty().getValue();
        double remainingQty = mktOrder.getOrderQty().getValue();
        boolean isMatched = false;
        double buyOrdRemainingQty = mktOrder.getOrderQty().getValue();

        for (OrderEntity entity : limitSellOrders) {

            if (entity.getRemainingQty() == orderQty) {
                isMatched = true;
                String executionId = ExecutionCounter.getTradeExecutionId(symbolName);
                fillOrder(entity, executionId);
                OrderEntity x = orderDao.getOrder(mktOrder.getClOrdID().getValue());
                x.setPrice(entity.getPrice());
                fillOrder(x, executionId);
                feedHandler.tell(new TradeMatch(executionId, orderQty,
                        entity.getPrice(), TimeUtils.getTimeString(), x.getOrderId(), entity.getOrderId()), getSelf());
                orderDao.updateTradeMatch(executionId, orderQty, entity.getPrice(), entity.getOrderId(), x.getOrderId());
                executedVol = executedVol + orderQty;
                updateVolume(orderQty);
                orderDao.addOrderExecution(executionId, entity.getOrderId(), orderQty, entity.getPrice());
                orderDao.addOrderExecution(executionId, x.getOrderId(), orderQty, entity.getPrice());
                transferHolding(entity.getAccountNumber(),x.getAccountNumber(),x.getSymbol(),orderQty);
                long time = System.currentTimeMillis() - orderEntryTime;
                System.out.println("******** MATCHING TIME " + time + " ms ********");
                break;
            } else if (entity.getRemainingQty() > remainingQty) {
                // mkt order is filling
                isMatched = true;
                String executionId = ExecutionCounter.getTradeExecutionId(symbolName);
                OrderEntity mktOrdEntity = orderDao.getOrder(mktOrder.getClOrdID().getValue());
                mktOrdEntity.setPrice(entity.getPrice());
                fillOrder(mktOrdEntity, executionId);
                entity.setExecutedQty(entity.getExecutedQty() + remainingQty);
                entity.setRemainingQty(entity.getQty() - entity.getExecutedQty());
                partialFillOrder(entity, executionId);
                feedHandler.tell(new TradeMatch(executionId, remainingQty,
                        entity.getPrice(), TimeUtils.getTimeString(), mktOrdEntity.getOrderId(), entity.getOrderId()), getSelf());
                orderDao.updateTradeMatch(executionId, remainingQty, entity.getPrice(), entity.getOrderId(), mktOrdEntity.getOrderId());
                executedVol = executedVol + remainingQty;
                updateVolume(remainingQty);
                orderDao.addOrderExecution(executionId, entity.getOrderId(), remainingQty, entity.getPrice());
                orderDao.addOrderExecution(executionId, mktOrdEntity.getOrderId(), remainingQty, entity.getPrice());
                transferHolding(entity.getAccountNumber(), mktOrdEntity.getAccountNumber(), mktOrdEntity.getSymbol(),remainingQty);
                long time = System.currentTimeMillis() - orderEntryTime;
                System.out.println("******** MATCHING TIME " + time + " ms ********");
                break;
            } else if (entity.getRemainingQty() < remainingQty) {
                // limit order will be fill and mkt order will be Partially filled
                isMatched = true;
                String executionId = ExecutionCounter.getTradeExecutionId(symbolName);
                OrderEntity mktOrdEntry = orderDao.getOrder(mktOrder.getClOrdID().getValue());
                mktOrdEntry.setPrice(entity.getPrice());
                mktOrdEntry.setExecutedQty(mktOrdEntry.getExecutedQty() + entity.getRemainingQty());
                mktOrdEntry.setRemainingQty(mktOrdEntry.getQty() - mktOrdEntry.getExecutedQty());
                partialFillOrder(mktOrdEntry, executionId);
                fillOrder(entity, executionId);
                feedHandler.tell(new TradeMatch(executionId, entity.getRemainingQty(),
                        entity.getPrice(), TimeUtils.getTimeString(), mktOrdEntry.getOrderId(), entity.getOrderId()), getSelf());
                orderDao.updateTradeMatch(executionId, entity.getRemainingQty(), entity.getPrice(), entity.getOrderId(), mktOrdEntry.getOrderId());
                executedVol = executedVol + entity.getRemainingQty();
                updateVolume(entity.getRemainingQty());
                orderDao.addOrderExecution(executionId, entity.getOrderId(), entity.getRemainingQty(), entity.getPrice());
                orderDao.addOrderExecution(executionId, mktOrdEntry.getOrderId(), entity.getRemainingQty(), entity.getPrice());
                transferHolding(entity.getAccountNumber(), mktOrdEntry.getAccountNumber(), mktOrdEntry.getSymbol(), entity.getRemainingQty());
                long time = System.currentTimeMillis() - orderEntryTime;
                System.out.println("******** MATCHING TIME " + time + " ms ********");
            }

        }

        if(isMatched)
            transmitUpdatedOrderBook();

    }

    private void matchMktSellOrder(NewOrderSingle mktOrder, List<OrderEntity> limitBuyOrders) throws FieldNotFound {

        double orderQty = mktOrder.getOrderQty().getValue();
        double remainingQty = mktOrder.getOrderQty().getValue();
        boolean isMatched = false;

        for (OrderEntity entity : limitBuyOrders) {

            if (entity.getRemainingQty() == orderQty) {
                isMatched = true;
                String executionId = ExecutionCounter.getTradeExecutionId(symbolName);
                fillOrder(entity, executionId);
                OrderEntity x = orderDao.getOrder(mktOrder.getClOrdID().getValue());
                x.setPrice(entity.getPrice());
                fillOrder(x, executionId);
                feedHandler.tell(new TradeMatch(executionId, orderQty,
                        entity.getPrice(), TimeUtils.getTimeString(), entity.getOrderId(), x.getOrderId()), getSelf());
                orderDao.updateTradeMatch(executionId, orderQty, entity.getPrice(), x.getOrderId(), entity.getOrderId());
                executedVol = executedVol + orderQty;
                updateVolume(orderQty);
                orderDao.addOrderExecution(executionId, entity.getOrderId(), orderQty, entity.getPrice());
                orderDao.addOrderExecution(executionId, x.getOrderId(), orderQty, entity.getPrice());
                transferHolding(x.getAccountNumber(),entity.getAccountNumber(),x.getSymbol(),orderQty);
                long time = System.currentTimeMillis() - orderEntryTime;
                System.out.println("******** MATCHING TIME " + time + " ms ********");
                break;
            } else if (entity.getRemainingQty() > remainingQty) {
                isMatched = true;
                String executionId = ExecutionCounter.getTradeExecutionId(symbolName);
                OrderEntity mktOrdEntity = orderDao.getOrder(mktOrder.getClOrdID().getValue());
                mktOrdEntity.setPrice(entity.getPrice());
                fillOrder(mktOrdEntity, executionId);
                entity.setExecutedQty(entity.getExecutedQty() + remainingQty);
                entity.setRemainingQty(entity.getQty() - entity.getExecutedQty());
                partialFillOrder(entity, executionId);
                feedHandler.tell(new TradeMatch(executionId, remainingQty,
                        entity.getPrice(), TimeUtils.getTimeString(), entity.getOrderId(), mktOrdEntity.getOrderId()), getSelf());
                orderDao.updateTradeMatch(executionId, remainingQty, entity.getPrice(), entity.getOrderId(), mktOrdEntity.getOrderId());
                executedVol = executedVol + remainingQty;
                updateVolume(remainingQty);
                orderDao.addOrderExecution(executionId, entity.getOrderId(), remainingQty, entity.getPrice());
                orderDao.addOrderExecution(executionId, mktOrdEntity.getOrderId(), remainingQty, entity.getPrice());
                transferHolding(mktOrdEntity.getAccountNumber(),entity.getAccountNumber(),mktOrdEntity.getSymbol(),remainingQty);
                long time = System.currentTimeMillis() - orderEntryTime;
                System.out.println("******** MATCHING TIME " + time + " ms ********");
                break;

            } else if (entity.getRemainingQty() < remainingQty) {
                // limit order will be fill and mkt order will be Partially filled
                isMatched = true;
                String executionId = ExecutionCounter.getTradeExecutionId(symbolName);
                OrderEntity mktOrdEntry = orderDao.getOrder(mktOrder.getClOrdID().getValue());
                mktOrdEntry.setPrice(entity.getPrice());
                mktOrdEntry.setExecutedQty(mktOrdEntry.getExecutedQty() + entity.getRemainingQty());
                mktOrdEntry.setRemainingQty(mktOrdEntry.getQty() - mktOrdEntry.getExecutedQty());
                partialFillOrder(mktOrdEntry, executionId);
                fillOrder(entity, executionId);
                feedHandler.tell(new TradeMatch(executionId, entity.getRemainingQty(),
                        entity.getPrice(), TimeUtils.getTimeString(), entity.getOrderId(), mktOrdEntry.getOrderId()), getSelf());
                orderDao.updateTradeMatch(executionId, entity.getRemainingQty(), entity.getPrice(), entity.getOrderId(), mktOrdEntry.getOrderId());
                executedVol = executedVol + entity.getRemainingQty();
                updateVolume(entity.getRemainingQty());
                orderDao.addOrderExecution(executionId, entity.getOrderId(), entity.getRemainingQty(), entity.getPrice());
                orderDao.addOrderExecution(executionId, mktOrdEntry.getOrderId(), entity.getRemainingQty(), entity.getPrice());
                transferHolding(mktOrdEntry.getAccountNumber(), entity.getAccountNumber(), mktOrdEntry.getSymbol(), entity.getRemainingQty());
                long time = System.currentTimeMillis() - orderEntryTime;
                System.out.println("******** MATCHING TIME " + time + " ms ********");
            }
        }

        if(isMatched)
            transmitUpdatedOrderBook();

    }

    private void saveOrder(NewOrderSingle order, String traderId) {
        try {
            double price = 0;
            if (order.getOrdType().getValue() == OrdType.LIMIT) {
                price = order.getPrice().getValue();
            }
            orderDao.createOrder(order.getClOrdID().getValue(), traderId,order.getSymbol().getValue(),order.getAccount().getValue(),
                    order.getOrderQty().getValue(), price, order.getOrdType().getValue(), order.getSide().getValue(),
                    order.getTimeInForce().getValue(), System.currentTimeMillis(), OrdStatus.NEW,
                    0l, order.getOrderQty().getValue());
        } catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();
        }
    }


    private void fillOrder(OrderEntity orderEntity, String executionId) {
        orderDao.updateOrdFill(orderEntity.getOrderId(), orderEntity.getQty());
        ExecutionReport report = getExecutionReport(orderEntity, OrdStatus.FILLED, executionId);
        SessionID sessionID = orderSessions.get(orderEntity.getOrderId());
        OutMessageFix outMessageFix = new OutMessageFix(report, sessionID);
        fixHandler.tell(outMessageFix, getSelf());
        orderSessions.remove(orderEntity.getOrderId());
    }

    public void partialFillOrder(OrderEntity order, String executionId) {
        orderDao.partialFill(order.getOrderId(), order.getExecutedQty(), order.getRemainingQty());
        ExecutionReport report = getExecutionReport(order, OrdStatus.PARTIALLY_FILLED, executionId);
        SessionID sessionID = orderSessions.get(order.getOrderId());
        OutMessageFix outMessageFix = new OutMessageFix(report, sessionID);
        fixHandler.tell(outMessageFix, getSelf());
    }

    private void transmitOrderBook(List<OrderEntity> buyOrders, List<OrderEntity> sellOrders) {

        int loopLength = 0;


        if(buyOrders.size() > sellOrders.size()){
            loopLength = buyOrders.size();
        }else {
            loopLength = sellOrders.size();
        }

        List<OrderBookRaw> orderBookRaws = new ArrayList<>(loopLength);

        for (int i = 0; i < loopLength; i++) {
//            OrderEntity buy = buyOrders.get(i);
//            OrderEntity sell = sellOrders.get(i);

            OrderEntity buy;
            OrderEntity sell;
            if(i < buyOrders.size()){
                buy = buyOrders.get(i);
            }else {
                buy = new OrderEntity();
                buy.setOrdTime(0);
                buy.setRemainingQty(0);
                buy.setPrice(0);
                buy.setOrderId(null);
            }

            if(i < sellOrders.size()){
                sell = sellOrders.get(i);
            }else {
                sell = new OrderEntity();
                sell.setOrdTime(0);
                sell.setRemainingQty(0);
                sell.setPrice(0);
                sell.setOrderId(null);
            }

            OrderBookRaw orderBookRaw = new OrderBookRaw(i, getTimeStmpString(new Date(buy.getOrdTime())), buy.getRemainingQty(), buy.getPrice(),
                    getTimeStmpString(new Date(sell.getOrdTime())), sell.getRemainingQty(), sell.getPrice(),
                    buy.getOrderId(),sell.getOrderId());
            orderBookRaws.add(orderBookRaw);

        }

        OrderBook orderBook = new OrderBook(orderBookRaws);
        orderBook.setMessageType('B');
        orderBook.setSymbol(this.symbolName);
        String json = gson.toJson(orderBook);
        feedHandler.tell(json, getSelf());


    }

    private void transferHolding(String fromAcc,String toAcc,String symbol,double qty){
        customerDao.debitAccount(fromAcc, symbol, qty);
        customerDao.creditAccount(toAcc, symbol, qty);
    }

    private void transferHolding(OrderEntity ordOne, OrderEntity ordTwo, double qty){

        if(ordOne.getOrdSide() == Side.BUY && ordTwo.getOrdSide() == Side.SELL){
            customerDao.creditAccount(ordOne.getAccountNumber(),ordOne.getSymbol(),qty);
            customerDao.debitAccount(ordTwo.getAccountNumber(),ordOne.getSymbol(),qty);
        }else if(ordOne.getOrdSide() == Side.SELL && ordTwo.getOrdSide() == Side.BUY){
            customerDao.creditAccount(ordTwo.getAccountNumber(),ordOne.getSymbol(),qty);
            customerDao.debitAccount(ordOne.getAccountNumber(),ordOne.getSymbol(),qty);
        }else {
            logger.warn("UNKNOWN condition in holding transfer.");
        }



    }

    public String getTimeStmpString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }

    private void rejectOrder(NewOrderSingle order) throws FieldNotFound {
        OrderEntity orderEntity = orderDao.getOrder(order.getClOrdID().getValue());
        ExecutionReport report = getExecutionReport(orderEntity,OrdStatus.REJECTED,"00000");
        orderDao.updateOrderStatus(order.getClOrdID().getValue());
        SessionID sessionID = orderSessions.get(order.getClOrdID().getValue());
        OutMessageFix outMessageFix = new OutMessageFix(report, sessionID);
        fixHandler.tell(outMessageFix, getSelf());
    }

    private ExecutionReport getExecutionReport(OrderEntity order, char ordStatus, String executionId) {
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

    private void transmitUpdatedOrderBook(){
        List<OrderEntity> buyOrders = orderDao.getBuyLimitOrders(this.symbolName);
        List<OrderEntity> sellOrders = orderDao.getSellLimitOrders(this.symbolName);
        transmitOrderBook(buyOrders, sellOrders);
    }

}
