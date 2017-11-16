package com.dfn.exchange;

import akka.actor.UntypedActor;

import java.net.Socket;

/**
 * Created by darshanas on 11/7/2017.
 */
public class PriceSocketSender extends UntypedActor {

    private Socket socket = null;

    public PriceSocketSender(Socket socket){
        this.socket = socket;
    }

    @Override
    public void onReceive(Object message) throws Exception {

    }
}
