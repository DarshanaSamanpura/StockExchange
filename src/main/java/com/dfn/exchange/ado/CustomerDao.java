package com.dfn.exchange.ado;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.util.DoubleMapper;
import org.skife.jdbi.v2.util.IntegerMapper;

/**
 * Created by darshanas on 12/4/2017.
 */
public interface CustomerDao {

    @SqlUpdate("insert into customer(nic,first_name,middle_name,last_name,acc_number) values(:nic,:first_name,:middle_name," +
            ":last_name,:acc_number)")
    void addNewCustomer(@Bind("nic") String nic, @Bind("first_name") String firstName, @Bind("middle_name") String middleName,
                        @Bind("last_name") String lastName, @Bind("acc_number") String accNumber);

    @SqlUpdate("insert into holdings(acc_number,symbol,balance) values(:acc_number,:symbol,:balance)")
    void addHolding(@Bind("acc_number") String accNumber,@Bind("symbol") String symbol, @Bind("balance") double balance);

    @SqlUpdate("update holdings set balance = :balance where acc_number = :acc_number and symbol = :symbol")
    void updateHolding(@Bind("acc_number") String accNumber, @Bind("symbol") String symbol, @Bind("balance") double balance);

    @SqlQuery("select balance from holdings where acc_number = :acc_number and symbol = :symbol")
    @Mapper(DoubleMapper.class)
    Double getCurrentBalance(@Bind("acc_number") String accNumber, @Bind("symbol") String symbol);

    @SqlUpdate("update holdings set balance = balance + :qty where acc_number = :acc_number and symbol = :symbol")
    void creditAccount(@Bind("acc_number") String accNumber, @Bind("symbol") String symbol, @Bind("qty") double qty);

    @SqlUpdate("update holdings set balance = balance - :qty where acc_number = :acc_number and symbol = :symbol")
    void debitAccount(@Bind("acc_number") String accNumber, @Bind("symbol") String symbol, @Bind("qty") double qty);

    @SqlQuery("select count(*) from customer")
    @Mapper(IntegerMapper.class)
    Integer getCustomerCount();

}
