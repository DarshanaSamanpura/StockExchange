package com.dfn.exchange.ado;


import com.dfn.exchange.beans.Symbol;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.util.IntegerMapper;

import java.util.List;

/**
 * Created by darshanas on 11/13/2017.
 */
public interface SymbolDao {

    @SqlUpdate("insert into symbols(sid,symbol,symbol_code,exchange,exchange_code,instrument_type,decimal_corr_factor,market_code) " +
            "values(NULL,:symbol,:symbol_code,:exchange,:exchange_code,:instrument_type,:decimal_corr_factor,:market_code)")
    void addNewSymbol(@Bind("symbol") String symbol,@Bind("symbol_code") String symbolCode, @Bind("exchange") String exchange,
                      @Bind("exchange_code") String exchangeCode, @Bind("instrument_type") String instumentTyp,
                      @Bind("decimal_corr_factor") int decimalCorrFac, @Bind("market_code") String mktCode);

    @SqlQuery("select count(*) from symbols")
    @Mapper(IntegerMapper.class)
    int countSymbols();



}
