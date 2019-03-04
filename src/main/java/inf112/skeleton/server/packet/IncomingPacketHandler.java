package inf112.skeleton.server.packet;

import com.google.gson.JsonObject;
import inf112.skeleton.common.packet.*;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.status.LoginResponseStatus;
import inf112.skeleton.server.ChatServerHandler;
import inf112.skeleton.server.login.UserLogging;
import inf112.skeleton.server.user.User;
import inf112.skeleton.server.util.Utility;
import io.netty.channel.Channel;

import javax.rmi.CORBA.Util;

public class IncomingPacketHandler {
    public void handleIncomingPacket(Channel incoming, JsonObject jsonObject, ChatServerHandler handler) {
        IncomingPacket packetId = IncomingPacket.values()[jsonObject.get("id").getAsInt()];
        switch (packetId) {
            case LOGIN:
                LoginPacket loginpkt = handler.gson.fromJson(jsonObject.get("data"), LoginPacket.class);
                User loggingIn = UserLogging.login(loginpkt, incoming);
                if (loggingIn != null) {
                    if (!handler.loggedInPlayers.contains(loggingIn)) {
                        if (handler.alreadyLoggedIn(loggingIn.getName())) {
                            AlreadyLoggedIn(incoming, handler, loggingIn.name);
                            return;
                        }

                        handler.loggedInPlayers.add(loggingIn);
                        OutgoingPacket response = OutgoingPacket.LOGINRESPONSE;
                        LoginResponseStatus status = LoginResponseStatus.LOGIN_SUCCESS;
                        LoginResponsePacket loginResponsePacket =
                                new LoginResponsePacket(status.ordinal(), loggingIn.name, "Success");
                        Packet responsePacket = new Packet(response.ordinal(), loginResponsePacket);
                        incoming.writeAndFlush(handler.gson.toJson(responsePacket) + "\r\n");
                        loggingIn.setLoggedIn(true);

                        loggingIn.player.sendInit();

                        for (User user : handler.loggedInPlayers) {
                            if (user.getChannel() == incoming)
                                continue;
                            user.player.sendToNewClient(incoming);
                        }
                        handler.connections.remove(loggingIn);
                    } else {
                        AlreadyLoggedIn(incoming, handler, loggingIn.name);
                    }
                }
                break;
            case CHAT_MESSAGE:

                ChatMessagePacket msgPacket = handler.gson.fromJson(jsonObject.get("data"), ChatMessagePacket.class);
                User messagingUser = handler.getEntityFromLoggedIn(incoming);
                if (msgPacket.getMessage().startsWith("!")) {
                    String[] command = msgPacket.getMessage().substring(1).split(" ");
                    handleCommand(messagingUser, command, handler);
                } else {
                    OutgoingPacket chatMessage = OutgoingPacket.CHATMESSAGE;
                    ChatMessagePacket chatMessagePacket = new ChatMessagePacket(messagingUser.getName() + ": " + msgPacket.getMessage());
                    Packet responsePacket = new Packet(chatMessage.ordinal(), chatMessagePacket);

                    for (User entity : handler.loggedInPlayers) {
                        entity.getChannel().writeAndFlush(handler.gson.toJson(responsePacket) + "\r\n");
                    }
                }
                break;
            default:
                System.err.println("Unhandled packet: " + packetId.name());
                System.out.println("data: " + jsonObject.get("data"));
        }
    }

    private void handleCommand(User messagingUser, String[] command, ChatServerHandler handler) {
        System.out.println(messagingUser.getName() + " sent command: " + command[0]);

        switch (command[0]) {
            case "players":
                sendMessage("There is currently " + handler.loggedInPlayers.size() + " player(s) online.", messagingUser, handler);
                break;
            case "move":
                if(command.length > 2){
                    if(Directions.fromString(command[1].toUpperCase()) != null){
                        Directions direction = Directions.valueOf(command[1].toUpperCase());
                        if(Utility.isStringInt(command[2])){
                            messagingUser.player.startMovement(direction, Integer.parseInt(command[2]));
                            return;
                        }
                    }
                }
                sendMessage("Error in command, proper usage: '!move north 3'.", messagingUser, handler);

                break;
            default:
                sendMessage("Command not found \"" + command[0] + "\".", messagingUser, handler);
                break;
        }
    }

    private void sendMessage(String message, User user, ChatServerHandler handler){
        Packet responsePacket = new Packet(
                OutgoingPacket.CHATMESSAGE.ordinal(),
                new ChatMessagePacket("[SERVER]: " +message));
        user.getChannel().writeAndFlush(handler.gson.toJson(responsePacket) + "\r\n");
    }

    private void AlreadyLoggedIn(Channel incoming, ChatServerHandler handler, String name) {
        OutgoingPacket response = OutgoingPacket.LOGINRESPONSE;
        LoginResponseStatus status = LoginResponseStatus.ALREADY_LOGGEDIN;
        LoginResponsePacket loginResponsePacket =
                new LoginResponsePacket(status.ordinal(), name, "User already logged in");
        Packet responsePacket = new Packet(response.ordinal(), loginResponsePacket);
        incoming.writeAndFlush(handler.gson.toJson(responsePacket) + "\r\n");
    }

}
