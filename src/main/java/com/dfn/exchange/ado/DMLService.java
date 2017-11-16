package com.dfn.exchange.ado;

import org.h2.jdbcx.JdbcConnectionPool;

import javax.sql.DataSource;

/**
 * Created by darshanas on 11/6/2017.
 */
public class DMLService {

    private static String tbl_order = "create table if NOT EXISTS orders(order_id VARCHAR(255) PRIMARY KEY, trader_id INT, symbol VARCHAR(50), qty BIGINT, price DECIMAL(12,3),\n" +
            "        ord_type INT, ord_side INT, tif INT, ord_time BIGINT, executed_qty INT, remaining_qty INT,\n" +
            "        is_filled BOOLEAN DEFAULT FALSE);";

    private static String tbl_transactions = "CREATE TABLE if NOT EXISTS trade_info(tid BIGINT PRIMARY KEY AUTO_INCREMENT, symbol VARCHAR(50), trade_volume BIGINT,\n" +
            "        trade_value DECIMAL(18,3), last_trade_price DECIMAL(18,3));";

    public static void createTables(){
        DataSource source = JdbcConnectionPool.create("jdbc:h2:mem:test","sa","password");

    }

}
