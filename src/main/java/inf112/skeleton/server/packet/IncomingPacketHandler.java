package inf112.skeleton.server.packet;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonObject;
import inf112.skeleton.common.packet.*;
import inf112.skeleton.common.status.LoginResponseStatus;
import inf112.skeleton.server.ChatServerHandler;
import inf112.skeleton.server.login.UserLogging;
import inf112.skeleton.server.user.User;
import inf112.skeleton.server.util.Utility;
import io.netty.channel.Channel;

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
        }
        System.out.println(jsonObject.get("data"));
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
