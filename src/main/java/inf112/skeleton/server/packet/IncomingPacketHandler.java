package inf112.skeleton.server.packet;

import com.google.gson.JsonObject;
import inf112.skeleton.common.packet.*;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.status.LoginResponseStatus;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.RoboCopServerHandler;
import inf112.skeleton.server.login.UserLogging;
import inf112.skeleton.server.user.User;
import inf112.skeleton.server.util.Utility;
import io.netty.channel.Channel;

public class IncomingPacketHandler {

    /**
     * Parse received packet and decide what to do with it.
     * @param incoming
     * @param jsonObject
     * @param handler
     */
    public void handleIncomingPacket(Channel incoming, JsonObject jsonObject, RoboCopServerHandler handler) {
        IncomingPacket packetId = IncomingPacket.values()[jsonObject.get("id").getAsInt()];
        switch (packetId) {
            case LOGIN:
                LoginPacket loginpkt = Tools.GSON.fromJson(jsonObject.get("data"), LoginPacket.class);
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
                        incoming.writeAndFlush(Tools.GSON.toJson(responsePacket) + "\r\n");
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

                ChatMessagePacket msgPacket = Tools.GSON.fromJson(jsonObject.get("data"), ChatMessagePacket.class);
                User messagingUser = handler.getEntityFromLoggedIn(incoming);
                if (msgPacket.getMessage().startsWith("!")) {
                    String[] command = msgPacket.getMessage().substring(1).split(" ");
                    handleCommand(messagingUser, command, handler);
                } else {
                    OutgoingPacket chatMessage = OutgoingPacket.CHATMESSAGE;
                    ChatMessagePacket chatMessagePacket = new ChatMessagePacket(messagingUser.getName() + ": " + msgPacket.getMessage());
                    Packet responsePacket = new Packet(chatMessage.ordinal(), chatMessagePacket);

                    for (User entity : handler.loggedInPlayers) {
                        entity.getChannel().writeAndFlush(Tools.GSON.toJson(responsePacket) + "\r\n");
                    }
                }
                break;
            case MOVEMENT_ACTION:

                break;
            default:
                System.err.println("Unhandled packet: " + packetId.name());
                System.out.println("data: " + jsonObject.get("data"));
        }
    }

    /**
     * Parse command from packet and exectute action.
     * @param messagingUser
     * @param command
     * @param handler
     */
    private void handleCommand(User messagingUser, String[] command, RoboCopServerHandler handler) {
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

    /**
     * Send a message to a user, the message will show up in the chatbox with a "[SERVER]: " prefix.
     * @param message
     * @param user
     * @param handler
     */
    private void sendMessage(String message, User user, RoboCopServerHandler handler){
        Packet responsePacket = new Packet(
                OutgoingPacket.CHATMESSAGE.ordinal(),
                new ChatMessagePacket("[SERVER]: " +message));
        user.getChannel().writeAndFlush(Tools.GSON.toJson(responsePacket) + "\r\n");
    }

    /**
     * User failed auth because a user with the same name is already loggid in, send a message to the new connection to
     * inform them.
     * @param incoming
     * @param handler
     * @param name
     */
    private void AlreadyLoggedIn(Channel incoming, RoboCopServerHandler handler, String name) {
        OutgoingPacket response = OutgoingPacket.LOGINRESPONSE;
        LoginResponseStatus status = LoginResponseStatus.ALREADY_LOGGEDIN;
        LoginResponsePacket loginResponsePacket =
                new LoginResponsePacket(status.ordinal(), name, "User already logged in");
        Packet responsePacket = new Packet(response.ordinal(), loginResponsePacket);
        incoming.writeAndFlush(Tools.GSON.toJson(responsePacket) + "\r\n");
    }

}
