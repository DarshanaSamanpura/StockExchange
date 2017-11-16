package com.dfn.exchange;

import akka.actor.UntypedActor;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;


/**
 * Created by darshanas on 11/2/2017.
 */
public class FeedHelper extends UntypedActor {



    private Gson gson = new Gson();

    @Override
    public void preStart() throws Exception {
//        super.preStart();
//        Runnable r = () -> startBroker();
//        new Thread(r).start();
//        setUpProducer();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        String json = gson.toJson(message);
    }



}
