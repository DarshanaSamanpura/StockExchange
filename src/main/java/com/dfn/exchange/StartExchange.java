package com.dfn.exchange;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.dfn.exchange.ado.CustomerDao;
import com.dfn.exchange.ado.DMLDao;
import com.dfn.exchange.ado.DataService;
import com.dfn.exchange.beans.Customer;
import org.skife.jdbi.v2.DBI;

import java.util.List;

/**
 * Created by darshanas on 11/2/2017.
 */
public class StartExchange {

    public static void main(String[] args) {
        System.out.println("Starting the actor system");
        DataService ds = DataService.getInstance(Constants.DB);
        DMLDao dmlDao = ds.getDbi().onDemand(DMLDao.class);
        dmlDao.createTableOrders();
        dmlDao.createTableCompletedOrders();
        dmlDao.createTableTradeInfo();
        dmlDao.createTableTradeMatch();
        dmlDao.createTableOrderExecutions();
        dmlDao.createTableSymbols();
        dmlDao.createFixStore();
        dmlDao.createTableCustomer();
        dmlDao.createTableAccount();
        populateData(ds.getDbi());
        System.out.println("Table creation successful.");
        ActorSystem system = ActorSystem.create("stockExchange");
        system.actorOf(Props.create(ExchangeSupervisor.class),"ExchangeSupervisor");
    }

    private static void populateData(DBI dbi){
        System.out.println("Populating initial data");
        CustomerDao customerDao = dbi.onDemand(CustomerDao.class);
        boolean toInset = true;
        if(Constants.DB.equals("mysql")){
            if(customerDao.getCustomerCount() != 0){
                toInset = false;
            }
        }
        if(toInset){
            List<Customer> list = Settings.getCustomerList();
            list.forEach(c -> {
                customerDao.addNewCustomer(c.getNic(),c.getFirstName(),c.getMiddleName(),c.getLastName(),c.getAccount());
                c.getHoldings().forEach(h -> {
                    customerDao.addHolding(h.getAccountNumber(),h.getSymbol(),h.getQty());
                });
            });
        }
    }
}
