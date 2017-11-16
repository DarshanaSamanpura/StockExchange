package com.dfn.exchange.ado;

import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.sql.DataSource;

/**
 * Created by darshanas on 11/6/2017.
 */
public class DataService {

    private static String tbl_order = "create table if NOT EXISTS orders(order_id VARCHAR(255) PRIMARY KEY, trader_id INT, symbol VARCHAR(50), qty BIGINT, price DECIMAL(12,3),\n" +
            "        ord_type INT, ord_side INT, tif INT, ord_time BIGINT, executed_qty INT, remaining_qty INT,\n" +
            "        is_filled BOOLEAN DEFAULT FALSE);";

    private static String tbl_transactions = "CREATE TABLE if NOT EXISTS trade_info(tid BIGINT PRIMARY KEY AUTO_INCREMENT, symbol VARCHAR(50), trade_volume BIGINT,\n" +
            "        trade_value DECIMAL(18,3), last_trade_price DECIMAL(18,3));";

    private static DBI dbi;
    private static DataService dataService = null;

    private DataService(String db){
        DataSource dataSource = null;
        if(db.equals("h2")){
            dataSource = JdbcConnectionPool.create("jdbc:h2:mem:test","sa","sa");
            dbi = new DBI(dataSource);
        }else if(db.equals("mysql")){
            dbi = new DBI("jdbc:mysql://localhost:3306/stock_exchange","root","password");
        }else {
            System.out.println("Can't create the db connections.");
        }

    }

    public static DataService getInstance(String db){
        if(dataService == null){
            dataService = new DataService(db);
        }
        return dataService;
    }

    public DBI getDbi(){
        return dbi;
    }



}
