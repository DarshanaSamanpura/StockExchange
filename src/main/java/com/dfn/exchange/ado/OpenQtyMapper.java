package com.dfn.exchange.ado;

import com.dfn.exchange.beans.OpenQty;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by darshanas on 12/12/2017.
 */
public class OpenQtyMapper implements ResultSetMapper<OpenQty> {

    @Override
    public OpenQty map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        OpenQty a = new OpenQty(resultSet.getString("symbol"),resultSet.getDouble("open_vol"));
        a.setOrdSide(resultSet.getString("ord_side"));
        return a;
    }

}
