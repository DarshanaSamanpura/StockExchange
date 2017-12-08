package com.dfn.exchange;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.dfn.exchange.ado.DMLDao;
import com.dfn.exchange.ado.DataService;

/**
 * Created by darshanas on 11/2/2017.
 */
public class StartExchange {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting the actor system");
        DataService ds = DataService.getInstance(Constants.DB);
        DMLDao dmlDao = ds.getDbi().onDemand(DMLDao.class);
        dmlDao.createTableOrders();
        dmlDao.createTableTradeInfo();
        dmlDao.createTableTradeMatch();
        dmlDao.createTableOrderExecutions();
        dmlDao.createTableSymbols();
        dmlDao.createFixStore();
        System.out.println("Table creation successful.");
        ActorSystem system = ActorSystem.create("stockExchange");
        system.actorOf(Props.create(ExchangeSupervisor.class),"ExchangeSupervisor");
    }
}
