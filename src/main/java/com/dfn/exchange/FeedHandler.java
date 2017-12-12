package com.dfn.exchange;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.dfn.exchange.beans.MarketVolume;
import com.dfn.exchange.beans.Quote;
import com.dfn.exchange.beans.TradeMatch;
import com.google.gson.Gson;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import quickfix.FieldNotFound;
import quickfix.field.Price;
import quickfix.fix42.NewOrderSingle;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by darshanas on 11/2/2017.
 */
public class FeedHandler extends UntypedActor {

    private static final Logger logger = LogManager.getLogger(FeedHandler.class);
    private ActorRef socketHandlerActor;
    private ActorRef webSocketHandlerActor;
    Gson gson = new Gson();

    @Override
    public void preStart() throws Exception {
        super.preStart();
        logger.info("Starting feed handler");
        logger.info("Feed Hander Path " + getSelf().path());
        socketHandlerActor = getContext().actorOf(Props.create(SocketFeedHandler.class));
        webSocketHandlerActor = getContext().actorOf(Props.create(WebSocketFeedHandler.class));
    }

    @Override
    public void onReceive(Object message) throws Exception {

        socketHandlerActor.tell(message, getSelf());
        webSocketHandlerActor.tell(message, getSelf());
    }



}
