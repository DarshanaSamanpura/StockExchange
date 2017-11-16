package com.dfn.exchange.ado;

import com.dfn.exchange.beans.OrderEntity;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by darshanas on 11/8/2017.
 */
public class OrderEntityMapper implements ResultSetMapper<OrderEntity> {
    @Override
    public OrderEntity map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        OrderEntity entity = new OrderEntity();
        entity.setOrderId(resultSet.getString("order_id"));
        entity.setExecutedQty(resultSet.getDouble("executed_qty"));
        entity.setIsFilled(resultSet.getBoolean("is_filled"));
        entity.setOrdSide(resultSet.getString("ord_side").toCharArray()[0]);
        entity.setOrdStatus(resultSet.getString("order_status").toCharArray()[0]);
        entity.setOrdTime(resultSet.getLong("ord_time"));
        entity.setOrdType(resultSet.getString("ord_type").toCharArray()[0]);
        entity.setPrice(resultSet.getDouble("price"));
        entity.setQty(resultSet.getDouble("qty"));
        entity.setRemainingQty(resultSet.getDouble("remaining_qty"));
        entity.setSymbol(resultSet.getString("symbol"));
        entity.setTif(resultSet.getString("tif").toCharArray()[0]);
        entity.setTraderId(resultSet.getString("trader_id"));
        return entity;
    }
}
