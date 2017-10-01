package com.zeptolab.tests;

import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by jose on 28/09/17.
 */
public class ChatServerHandler extends ChannelInboundMessageHandlerAdapter<String> {

    private static final List<ChatRoom> chatRooms = new ArrayList<>();
    private static final List<AuthenticatedUser> authenticatedUsers = new ArrayList<>();

    public void messageReceived(ChannelHandlerContext channelHandlerContext, String message) throws Exception {

        final Channel channel = channelHandlerContext.channel();

        if (message.indexOf("/leave") > -1 && message.substring(0,6).matches("/leave")){
            chatRooms.forEach(CR -> CR.leaveRoom(channel));
            this.getUser(channel).setStatus(false);
            channel.flush();
            channel.disconnect();
            channel.close();
            return;
        }

        if (message.indexOf("/users") > -1 && message.substring(0,6).matches("/users")){
            chatRooms.forEach(CR -> CR.showUsers(channel));
            return;
        }

        if (message.indexOf("/login") > -1 && message.substring(0,6).matches("/login")){
            String[] array = message.split(" ");

            if (array.length >= 3){

                String username = array[1];
                String password = array[2];
                AuthenticatedUser user = this.getUser(username);

                if (user == null){
                    AuthenticatedUser newUser = new AuthenticatedUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setChannel(channel);
                    newUser.setStatus(true);
                    authenticatedUsers.add(newUser);
                    user = newUser;
                }

                if (user.getPassword().matches(password)){
                    user.setStatus(true);
                    user.setChannel(channel);
                } else {
                    channel.write("Invalid password! \n");
                }


            } else {
                channel.write("You must specify an username & password in the following format: /join username password \n");
            }
            return;
        }


        if (message.indexOf("/join ") > -1 && message.substring(0,5).matches("/join")){

            if (!this.isLogged(channel)){
                channel.write("You have to log in to join a channel \n");
                return;
            }

            AuthenticatedUser user = this.getUser(channel);

            String[] array = message.split(" ");

            if (array.length > 1){

                String roomName = array[1];

                for (ChatRoom chatRoom : chatRooms){
                    if (chatRoom.getRoomName().matches(roomName)){
                        chatRoom.joinRoom(channel, user.getUsername());
                        return;
                    }
                }

                ChatRoom chatRoom = new ChatRoom(roomName);
                chatRoom.joinRoom(channel,user.getUsername());
                chatRooms.add(chatRoom);

            }

            return;
        }

        chatRooms.forEach(CR -> CR.broadcastMessage(channel, message));

    }

    private boolean isLogged(Channel channel){
        AuthenticatedUser user = this.getUser(channel);
        if (user != null){
            return user.isStatus();
        }

        return false;
    }

    private AuthenticatedUser getUser(Channel channel){
        for (AuthenticatedUser user : authenticatedUsers){
            if (user.getChannel().equals(channel)){
                return user;
            }
        }

        return null;
    }


    private AuthenticatedUser getUser(String username){
        for (AuthenticatedUser user : authenticatedUsers){
            if (user.getUsername().matches(username)){
                return user;
            }
        }

        return null;
    }

}
