import com.dfn.exchange.ado.DMLDao;
import com.dfn.exchange.ado.DataService;
import com.dfn.exchange.ado.OrderDao;
import com.dfn.exchange.beans.OrderEntity;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import quickfix.FieldNotFound;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.Side;
import quickfix.field.TimeInForce;
import quickfix.fix42.NewOrderSingle;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by darshanas on 11/6/2017.
 */
public class DaoTest {

    private String db = "h2";
    //private String db = "mysql";

    @Test
    public void testTableCreations(){
        DBI dbi = DataService.getInstance(db).getDbi();
        DMLDao dmlDao = dbi.onDemand(DMLDao.class);
        dmlDao.createTableOrders();
        dmlDao.createTableTradeInfo();
        dmlDao.createTableTradeMatch();
    }

    @Test
    public void testSingleInsert(){


        DBI dbi = DataService.getInstance(db).getDbi();
        DMLDao dmlDao = dbi.onDemand(DMLDao.class);
        dmlDao.createTableOrders();
        dmlDao.createTableTradeInfo();
        dmlDao.createTableTradeMatch();
        OrderDao orderDao = dbi.onDemand(OrderDao.class);
        orderDao.createOrder("1000", "1", "1010", "5555555",55, 45.63, '1', '2', '1', System.currentTimeMillis(), OrdStatus.NEW, 0, 0);
        List<NewOrderSingle> list = orderDao.getAllOrders();
        assertEquals(1, list.size());
        NewOrderSingle order = list.get(0);
        try {
            assertEquals("1000",order.getClOrdID().getValue());
            assertEquals("1010",order.getSymbol().getValue());
            assertEquals(55,order.getOrderQty().getValue(),0.000001);
            assertEquals(45.63,order.getPrice().getValue(),0.000001);
            assertEquals(OrdType.MARKET,order.getOrdType().getValue());
            orderDao.deleteOrder(order.getClOrdID().getValue());
        } catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();
        }

    }

    @Test
    public void addBulkOrders(){

        long start = System.currentTimeMillis();

        int counter = 1000;
        DBI dbi = DataService.getInstance(db).getDbi();
        DMLDao dmlDao = dbi.onDemand(DMLDao.class);
        dmlDao.createTableOrders();
        dmlDao.createTableTradeInfo();
        dmlDao.createTableTradeMatch();
        OrderDao orderDao = dbi.onDemand(OrderDao.class);
        int orderCount = 0;
        double min = 7.56;
        double max = 100.92;

        double randMin = 0;
        double randMax = 0;

        for(int i = 0; i < 500; i++){
            char side;
            if(i % 2 == 0){
                side = Side.BUY;
            }else {
                side = Side.SELL;
            }

            double price = getRandomPrice(min, max);

            if(randMin == 0 && randMax == 0){
                randMax = price;
                randMin = price;
            }else {
                if(price < randMin){
                    randMin = price;
                }
                if(price > randMax){
                    randMax = price;
                }
            }

            orderDao.createOrder(Integer.toString(counter),"TEST","1010","5555555",50,getRandomPrice(min,max),OrdType.LIMIT, side, TimeInForce.DAY,
                    System.nanoTime(),OrdStatus.NEW,0,50);
            counter++;
        }

        orderCount = orderDao.getAllOrders().size();
        assertEquals(500,orderCount);

        List<OrderEntity> buyOrders = orderDao.getBuyLimitOrders("1010");
        List<OrderEntity> sellOrders = orderDao.getSellLimitOrders("1010");


        assertTrue(buyOrders.get(0).getPrice() > buyOrders.get(1).getPrice());
        assertTrue(sellOrders.get(0).getPrice() < sellOrders.get(1).getPrice());


        if (db.equals("mysql")) {
            orderDao.deleteAllOrders();
        }

        long end = System.currentTimeMillis();

        System.out.println("TIME " + (end - start) + " s");

    }


    private double getRandomPrice(double min, double max){
        return min + Math.random() * (max - min);
    }


}
