package com.dfn.exchange.ado;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.util.StringMapper;

import java.util.List;

/**
 * Created by darshanas on 11/14/2017.
 */
public interface FixServiceDao {

    @SqlUpdate("insert into FIX_STORE(mid,timestmp,msg_side,fix_message) values(NULL,NOW(),:msg_side,:fix_message)")
    void addToFixStore(@Bind("msg_side") int msgSide, @Bind("fix_message") String fixMessage);

    @SqlQuery("select fix_message from FIX_STORE")
    @Mapper(StringMapper.class)
    List<String> getFixMessages();

}
