package com.dfn.exchange;

import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import com.dfn.exchange.beans.stat.Match;
import com.dfn.exchange.beans.stat.WakeUp;
import scala.concurrent.duration.Duration;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by darshanas on 12/8/2017.
 */
public class StatActor extends UntypedActor {

    private List<Long> matchTimes = new ArrayList<>();
    Path path = null;
    BufferedWriter fileWriter = null;

    Cancellable cancellable = null;

    @Override
    public void preStart() throws Exception {
        super.preStart();
        System.out.println("Stat Actor path " + getSelf().path());
        cancellable = getContext().system().scheduler().schedule(
                Duration.Zero(),
                Duration.create(10, TimeUnit.SECONDS),getSelf(),new WakeUp(),getContext().dispatcher(),null
        );


        path = Paths.get("./matchTimes.txt");
        fileWriter = Files.newBufferedWriter(path, Charset.forName("UTF-8"));
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if(message instanceof Match){
            matchTimes.add(((Match) message).getMatchTime());
        }else if (message instanceof WakeUp){
            StringBuilder builder = new StringBuilder();
            if(matchTimes.size() > 0){
                matchTimes.forEach(i -> builder.append(i+","));
                fileWriter.write(builder.toString());
                fileWriter.flush();
            }
        }

    }

}
