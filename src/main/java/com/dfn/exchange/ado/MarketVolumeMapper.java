package com.dfn.exchange.ado;

import com.dfn.exchange.beans.MarketVolume;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by darshanas on 11/25/2017.
 */
public class MarketVolumeMapper implements ResultSetMapper<MarketVolume> {
    @Override
    public MarketVolume map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        MarketVolume v = new MarketVolume();
        v.setVolume(resultSet.getDouble("mkt_vol"));
        v.setExVolume(resultSet.getDouble("ex_qty"));
        return v;
    }
}
