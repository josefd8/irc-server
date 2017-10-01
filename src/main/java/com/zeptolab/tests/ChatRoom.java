package com.zeptolab.tests;

import io.netty.channel.Channel;
import io.netty.channel.group.DefaultChannelGroup;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jose on 1/10/17.
 */
public class ChatRoom {

    private final DefaultChannelGroup room;
    private Queue<String> messages = new LinkedList<>();
    private ConcurrentHashMap<String, String> userMapping = new ConcurrentHashMap<>();


    public ChatRoom(String name) {
        this.room = new DefaultChannelGroup(name);
    }

    public String getRoomName() {
        return this.room.name();
    }

    public void joinRoom(Channel user, String username){

        if (room.size() == 10) {
            throw new IndexOutOfBoundsException("Cannot add user to chat room. Room is already full! \n");
        }
        room.add(user);
        userMapping.put(user.localAddress().toString(), username);
        room.write("[" + username + "] joined the room! \n");

        for( String item : messages ){
            user.write(item + "\n");
        }
    }

    public void leaveRoom(Channel user){
        room.remove(user);
        room.write("[" + userMapping.get(user.remoteAddress().toString()) + "] left the room \n");
    }

    public void broadcastMessage(Channel sender, String message){
        if (room.contains(sender)) {
            for (Channel channel : room) {
                if (!channel.equals(sender)) {
                    channel.write("[" + userMapping.get(sender.remoteAddress().toString()) + "]: " + message + "\n");
                }
            }

            if (messages.size() >= 10) {
                messages.remove();
            }

            messages.add("[" + userMapping.get(sender.remoteAddress().toString()) + "]: " + message);
        }
    }

    public void showUsers(Channel requester){
        if (room.contains(requester)){
            for (Channel channel : room){
                requester.write(channel.remoteAddress() + "\n");
            }
        }
    }

}
