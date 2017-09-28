package com.zeptolab.tests;

import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;


/**
 * Created by jose on 28/09/17.
 */
public class ChatServerHandler extends ChannelInboundMessageHandlerAdapter<String> {

    private static final ChannelGroup channels = new DefaultChannelGroup();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : channels){
            channel.write("[SERVER] - " + incoming.remoteAddress() + "has joined! \n");
        }
        channels.add(incoming);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : channels){
            channel.write("[SERVER] - " + incoming.remoteAddress() + "has left! \n");
        }
        channels.remove(incoming);
    }


    public void messageReceived(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        Channel incoming = channelHandlerContext.channel();
        for (Channel channel : channels){
            channel.write("[" + incoming.remoteAddress() + "]" + s + "\n");
        }
    }
}
