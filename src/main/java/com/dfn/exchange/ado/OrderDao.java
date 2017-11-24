package com.dfn.exchange.ado;

import com.dfn.exchange.beans.OrderEntity;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.util.DoubleMapper;
import org.skife.jdbi.v2.util.IntegerMapper;
import org.skife.jdbi.v2.util.LongMapper;
import quickfix.fix42.NewOrderSingle;

import java.util.List;

/**
 * Created by darshanas on 11/6/2017.
 */
public interface OrderDao {

    @SqlUpdate("insert into orders(order_id, trader_id, symbol, qty, price, ord_type, ord_side, tif, ord_time, order_status," +
            "executed_qty, remaining_qty) values (:order_id,:trader_id,:symbol,:qty,:price,:ord_type,:ord_side,:tif," +
            ":ord_time,:order_status,:executed_qty,:remaining_qty)")
    void createOrder(@Bind("order_id") String ordId, @Bind("trader_id") String traderId, @Bind("symbol") String symbol,
                     @Bind("qty") double qty, @Bind("price") double price, @Bind("ord_type") char ordType,
                     @Bind("ord_side") char ordSide, @Bind("tif") char tif, @Bind("ord_time") long ordTime,
                     @Bind("order_status") char ordStatus, @Bind("executed_qty") double executedQty,
                     @Bind("remaining_qty") double remQty);

    @SqlQuery("select * from orders")
    @Mapper(OrderMapper.class)
    List<NewOrderSingle> getAllOrders();

    @SqlQuery("select * from orders where symbol = :symbol and ord_side = '1' and ord_type = '2' and order_status in (0,1) " +
            "order by price desc, ord_time asc")
    @Mapper(OrderEntityMapper.class)
    List<OrderEntity> getBuyLimitOrders(@Bind("symbol") String symbol);

    @SqlQuery("select * from orders where symbol = :symbol and ord_side = '2' and ord_type = '2' and order_status in (0,1) " +
            "order by price, ord_time")
    @Mapper(OrderEntityMapper.class)
    List<OrderEntity> getSellLimitOrders(@Bind("symbol") String symbol);

    @SqlQuery("select * from orders where symbol = :symbol and ord_side = '2' and ord_type = '2' and order_status in (0,1) and  price <= :price " +
            "order by price, ord_time")
    @Mapper(OrderEntityMapper.class)
    List<OrderEntity> getMatchingSellLimitOrders(@Bind("symbol") String symbol, @Bind("price") double price);

    @SqlQuery("select * from orders where symbol = '1020' and ord_side = '1' and ord_type = '2' and order_status in (0,1) and price >= :price " +
            "order by price desc, ord_time asc;")
    @Mapper(OrderEntityMapper.class)
    List<OrderEntity> getMatchingBuyLimitOrders(@Bind("symbol") String symbol, @Bind("price") double price);


    @SqlQuery("select * from orders where order_id = :order_id")
    @Mapper(OrderEntityMapper.class)
    OrderEntity getOrder(@Bind("order_id") String orderId);

    @SqlUpdate("delete from orders where order_id = :order_id")
    void deleteOrder(@Bind("order_id") String ordId);

    @SqlUpdate("truncate table orders")
    void deleteAllOrders();

    @SqlUpdate("update orders set is_filled = true, executed_qty = :executed_qty, remaining_qty = 0, order_status = '2' " +
            "where order_id = :order_id")
    void updateOrdFill(@Bind("order_id") String ordId, @Bind("executed_qty") double exQty);

    @SqlUpdate("update orders set executed_qty = :executed_qty, remaining_qty = :remaining_qty, order_status = '1'  where " +
            "order_id = :order_id")
    void partialFill(@Bind("order_id") String ordId, @Bind("executed_qty") double exQty, @Bind("remaining_qty") double remQty);

    @SqlUpdate("insert into trade_match(mid,execution_id,match_qty,match_price,sell_ord_id,buy_ord_id) values(" +
            "NULL,:execution_id,:match_qty,:match_price,:sell_ord_id,:buy_ord_id)")
    void updateTradeMatch(@Bind("execution_id") String executionId, @Bind("match_qty") double matchQty, @Bind("match_price") double matchPrice,
                          @Bind("sell_ord_id") String sellOrdId, @Bind("buy_ord_id") String buyOrdId);

    @SqlQuery("select max(mid) from trade_match")
    @Mapper(LongMapper.class)
    Long getLastTradeMatchId();

    @SqlUpdate("insert into order_executions(execution_id,order_id,executed_qty,price) values(:execution_id," +
            ":order_id,:executed_qty,:price)")
    void addOrderExecution(@Bind("execution_id") String executionId, @Bind("order_id") String orderId,
                           @Bind("executed_qty") double executedQty, @Bind("price") double price);

    @SqlQuery("select AVG(price) from order_executions where order_id = :order_id")
    @Mapper(DoubleMapper.class)
    double getAveragePrice(@Bind("order_id") String orderId);

    @SqlUpdate("update orders set qty = :qty,price = :price, remaining_qty= :remQty, ord_Type= :ordType, ord_side= :ordSide  where order_id = :orderId")
    void updateOrder(@Bind("orderId") String orderId,@Bind("qty") double qty, @Bind("remQty") double remQty, @Bind("price") double price, @Bind("ordType") String ordType, @Bind("ordSide") String ordSide);


}
