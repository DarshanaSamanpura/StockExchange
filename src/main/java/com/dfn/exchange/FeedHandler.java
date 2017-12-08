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
    private final int port = 16500;
    private ServerSocket serverSocket = null;
//    private List<SocketHandler> socketList = new ArrayList<>();
    private ActorRef socketHandlerActor;
    private ActorRef webSocketHandlerActor;
    Gson gson = new Gson();

    @Override
    public void preStart() throws Exception {
        super.preStart();
        System.out.println("Starting feed handler");
        System.out.println("Feed Hander Path " + getSelf().path());
        socketHandlerActor = getContext().actorOf(Props.create(SocketFeedHandler.class));
        webSocketHandlerActor = getContext().actorOf(Props.create(WebSocketFeedHandler.class));

       /* Runnable r = () -> startServerSocket();
        new Thread(r).start();*/
    }

    @Override
    public void onReceive(Object message) throws Exception {

        socketHandlerActor.tell(message, getSelf());
        webSocketHandlerActor.tell(message, getSelf());

        /*String stringMsg = "";

        if(message instanceof InMessageFix){
            InMessageFix inf = (InMessageFix) message;

            if(inf.getFixMessage() instanceof NewOrderSingle){
                NewOrderSingle order = (NewOrderSingle) inf.getFixMessage();
                stringMsg = gson.toJson(getQuote(order));
            }

        }else if(message instanceof String){
            stringMsg = (String) message;
        }else if(message instanceof TradeMatch){
            stringMsg = gson.toJson(message);
        }else if(message instanceof MarketVolume){
            stringMsg = gson.toJson(message);
        }

        sendMessage(stringMsg);*/

    }


   /* private void sendMessage(String message){
        if(message != null && !message.equals("")){
            write(message);
        }
    }


    private Quote getQuote(NewOrderSingle order) throws FieldNotFound{
        Quote quote = new Quote();
        if(order.isSet(new Price()))
            quote.setPrice(order.getPrice().getValue());
        quote.setQty(order.getOrderQty().getValue());
        quote.setSide(order.getSide().getValue());
        quote.setSymbol(order.getSymbol().getValue());
        quote.setTif(order.getTimeInForce().getValue());
        quote.setType(order.getOrdType().getValue());
        quote.setMessageType('D');
        return quote;
    }

    private void startServerSocket(){

        try {
            serverSocket = new ServerSocket(port);
            while (true) {

                logger.info("Listening to connections");
                final Socket activeSocket = serverSocket.accept();
                socketList.add(new SocketHandler(activeSocket));

            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void write(String message){
        socketList.forEach(s -> {
            s.write(message);
        });
    }


    class SocketHandler{

        private Socket socket;
        BufferedReader socketReader = null;
        BufferedWriter socketWriter = null;
        Thread readerThred;

        public SocketHandler(Socket socket){

            try {
                this.socket = socket;
                socketReader = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                socketWriter = new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream()));
                Runnable r = () -> startReadSocket();
                readerThred = new Thread(r);
                //sending initial symbol details response.
                write(Settings.getSymbolString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void write(String message)  {
            try {
                socketWriter.write(message);
                socketWriter.write("\n");
                socketWriter.flush();
            } catch (IOException e) {
                System.out.println("Removing client from store");
                //clientStore.remove(key);
            }
        }

        private void startReadSocket(){

            String inMsg = null;

            try {
                while ((inMsg = socketReader.readLine()) != null) {

                    System.out.println("New in message from price " + inMsg);

                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
*/

}
