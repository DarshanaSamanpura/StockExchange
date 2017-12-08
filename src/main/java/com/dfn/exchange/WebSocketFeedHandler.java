package com.dfn.exchange;

import akka.actor.UntypedActor;
import com.dfn.exchange.beans.Quote;
import com.dfn.exchange.beans.WSUtil.Header;
import com.dfn.exchange.beans.WSUtil.LoginResponse;
import com.dfn.exchange.beans.WSUtil.Message;
import com.google.gson.Gson;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import quickfix.FieldNotFound;
import quickfix.fix42.NewOrderSingle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by manodyas on 11/28/2017.
 */
public class WebSocketFeedHandler extends UntypedActor {
    private static List<ChannelHandlerContext> webSocket = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(WebSocketFeedHandler.class);
    Gson gson = new Gson();
    private static String WS_PATH = "/websocket";
    private static int WS_PORT = 8085;

    @Override
    public void preStart() throws Exception {
        super.preStart();
        System.out.println("WS Feed Handler Path " + getSelf().path());
        start();

    }

    @Override
    public void onReceive(Object message) throws Exception {
        String stringMsg = "";

        if (message instanceof InMessageFix) {
            InMessageFix inf = (InMessageFix) message;

            if (inf.getFixMessage() instanceof NewOrderSingle) {
                NewOrderSingle order = (NewOrderSingle) inf.getFixMessage();
                stringMsg = gson.toJson(getQuote(order));
            }

        } else if (message instanceof String) {
            stringMsg = (String) message;
        }

        sendMessage(stringMsg);
    }

    private void sendMessage(String message) {
        System.out.println("Writing Message to WS EndPoints " + message);
        if (message != null && !message.equals("")) {
            write(message);
        }
    }

    private void write(String message) {
        webSocket.forEach(s -> {
            s.channel().writeAndFlush(new TextWebSocketFrame(message));
        });
    }

    private Quote getQuote(NewOrderSingle order) throws FieldNotFound {
        Quote quote = new Quote();
        quote.setPrice(order.getPrice().getValue());
        quote.setQty(order.getOrderQty().getValue());
        quote.setSide(order.getSide().getValue());
        quote.setSymbol(order.getSymbol().getValue());
        quote.setTif(order.getTimeInForce().getValue());
        quote.setType(order.getOrdType().getValue());
        quote.setMessageType('D');
        return quote;
    }

    public void addWebSocket(ChannelHandlerContext context) {
        webSocket.add(context);

    }


    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            final ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(final SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new HttpServerCodec(),
                                    new HttpObjectAggregator(65536),
                                    new WebSocketServerProtocolHandler(WS_PATH),
                                    new WebSocketFrameHandler());
                        }
                    });


            final Channel ch = sb.bind(WS_PORT).sync().channel();

            System.out.println("Web socket server started at port  :" + WS_PORT);
            //ch.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
           /* bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();*/
        }
    }


    class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> implements Serializable {
        public WebSocketFrameHandler() {

        }


        @Override
        public void channelRead0(ChannelHandlerContext channelHandlerContext, WebSocketFrame frame) throws Exception {
            System.out.println("Got new message");
            if (frame instanceof TextWebSocketFrame) {
                String request = ((TextWebSocketFrame) frame).text();
                System.out.println("Request:" + request);
                processWSRequest(channelHandlerContext, gson.fromJson(request, Message.class));
            } else {
                String message = "unsupported frame type: " + frame.getClass().getName();
                throw new UnsupportedOperationException(message);
            }

        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("New client connected.");

        }

        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            addWebSocket(ctx);
        }

        public void processWSRequest(ChannelHandlerContext channelHandlerContext, Message requestMessage){
            Header responseHeader = new Header();
            switch (requestMessage.getHeader().getMessageGroup()){


                case 1:  // login
                    Message loginResponse =  new Message();
                    responseHeader.setMessageGroup(101);
                    LoginResponse responseBody = new LoginResponse();
                    responseBody.setResCode(1);
                    loginResponse.setHeader(responseHeader);
                    loginResponse.setData(responseBody);
                    channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(gson.toJson(loginResponse)));
                    break;
                case 2: // getSymbolMeta
                    Message symbolMetaResponse = new Message();
                    responseHeader.setMessageGroup(102);
                    symbolMetaResponse.setHeader(responseHeader);
                    symbolMetaResponse.setData(SymbolSettings.getSymbolList());
                    channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(gson.toJson(symbolMetaResponse)));
                    break;


            }
        }




    }

}
