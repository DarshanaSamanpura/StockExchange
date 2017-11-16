package com.dfn.exchange.ado;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import quickfix.field.*;
import quickfix.fix42.NewOrderSingle;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by darshanas on 11/6/2017.
 */
public class OrderMapper implements ResultSetMapper<NewOrderSingle> {


    @Override
    public NewOrderSingle map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        NewOrderSingle ord = new NewOrderSingle();
        ord.set(new ClOrdID(resultSet.getString("order_id")));
        ord.set(new Side(resultSet.getString("ord_side").toCharArray()[0]));
        ord.set(new TimeInForce(resultSet.getString("tif").toCharArray()[0]));
        ord.set(new OrdType(resultSet.getString("ord_type").toCharArray()[0]));
        ord.set(new HandlInst('3'));
        ord.set(new Symbol(resultSet.getString("symbol")));
        ord.set(new OrderQty(resultSet.getDouble("qty")));
        ord.set(new Price(resultSet.getDouble("price")));
        return ord;
    }
}
