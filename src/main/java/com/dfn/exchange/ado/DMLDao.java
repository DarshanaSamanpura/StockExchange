package com.dfn.exchange.ado;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Created by darshanas on 11/6/2017.
 */
public interface DMLDao {

    @SqlUpdate("create table IF NOT EXISTS orders(order_id VARCHAR(255) PRIMARY KEY, trader_id VARCHAR(200), acc_number varchar(255), symbol VARCHAR(50), qty DECIMAL(12,3), price DECIMAL(12,3),\n" +
            "        ord_type CHAR(1), ord_side CHAR(1), tif CHAR(1), ord_time BIGINT, order_status CHAR(1), executed_qty DECIMAL(12,3), remaining_qty DECIMAL(12,3),\n" +
            "        is_filled BOOLEAN DEFAULT FALSE)")
    void createTableOrders();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS trade_info(tid BIGINT PRIMARY KEY AUTO_INCREMENT, symbol VARCHAR(50), trade_volume BIGINT,\n" +
            "        trade_value DECIMAL(18,3), last_trade_price DECIMAL(18,3))")
    void createTableTradeInfo();

    @SqlUpdate("create table IF NOT EXISTS trade_match(mid bigint AUTO_INCREMENT primary key, execution_id varchar(255),\n" +
            "        match_qty DECIMAL(12,3), match_price DECIMAL(12,3), sell_ord_id VARCHAR(255),\n" +
            "        buy_ord_id VARCHAR(255))")
    void createTableTradeMatch();

    @SqlUpdate("create table IF NOT EXISTS order_executions(execution_id varchar(255), order_id varchar(255),\n" +
            "    executed_qty DECIMAL(12,3), price DECIMAL(12,3)," +
            " primary key(execution_id,order_id))")
    void createTableOrderExecutions();

    @SqlUpdate("create table IF NOT EXISTS symbols(sid bigint auto_increment primary key, symbol varchar(255), symbol_code varchar(255),\n" +
            "    exchange varchar(255), exchange_code varchar(20), instrument_type varchar(20),\n" +
            "    decimal_corr_factor int, market_code varchar(20))")
    void createTableSymbols();


    @SqlUpdate("create table if not EXISTS FIX_STORE(mid bigint auto_increment primary key,\n" +
            "        timestmp DATETIME not null, msg_side int, fix_message TEXT)")
    void createFixStore();

    @SqlUpdate("create table if not EXISTS customer(nic varchar(250) primary key, first_name varchar(255),\n" +
            "    middle_name varchar(255), last_name varchar(255), acc_number varchar(255))")
    void createTableCustomer();

    @SqlUpdate("create table if not EXISTS holdings(acc_number varchar(255), symbol varchar(50), \n" +
            "    balance DECIMAL(12,3), primary key(acc_number,symbol))")
    void createTableAccount();

}
