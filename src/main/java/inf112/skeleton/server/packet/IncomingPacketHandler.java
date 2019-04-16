package inf112.skeleton.server.packet;

import com.google.gson.JsonObject;
import inf112.skeleton.common.packet.*;
import inf112.skeleton.common.packet.data.*;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.CardType;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.common.status.LoginResponseStatus;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.RoboCopServerHandler;
import inf112.skeleton.server.login.UserLogging;
import inf112.skeleton.server.user.User;
import inf112.skeleton.common.utility.StringUtilities;
import io.netty.channel.Channel;

public class IncomingPacketHandler {

    /**
     * Parse received packet and decide what to do with it.
     *
     * @param incoming
     * @param jsonObject
     * @param handler
     */
    public void handleIncomingPacket(Channel incoming, JsonObject jsonObject, RoboCopServerHandler handler) {
        ToServer packetId = ToServer.values()[jsonObject.get("id").getAsInt()];
        switch (packetId) {
            case LOGIN:
                LoginPacket loginpkt = LoginPacket.parseJSON(jsonObject);
                User loggingIn = UserLogging.login(loginpkt, incoming);
                if (loggingIn != null && !loginpkt.getUsername().contains(" ")) {
                    if (!RoboCopServerHandler.loggedInPlayers.contains(loggingIn)) {
                        if (handler.alreadyLoggedIn(loggingIn.getName())) {
                            AlreadyLoggedIn(incoming, loggingIn.getName());
                            return;
                        }


                        RoboCopServerHandler.loggedInPlayers.add(loggingIn);
                        FromServer response = FromServer.LOGINRESPONSE;
                        LoginResponseStatus status = LoginResponseStatus.LOGIN_SUCCESS;
                        LoginResponsePacket loginResponsePacket =
                                new LoginResponsePacket(status.ordinal(), loggingIn.getName(), "Success");
                        Packet responsePacket = new Packet(response.ordinal(), loginResponsePacket);
                        loggingIn.sendPacket(responsePacket);
                        loggingIn.initClient();

                        RoboCopServerHandler.connections.remove(loggingIn);
                    } else {
                        AlreadyLoggedIn(incoming, loggingIn.getName());
                    }
                } else {
                    FromServer response = FromServer.LOGINRESPONSE;
                    LoginResponseStatus status = LoginResponseStatus.WRONG_LOGINDETAILS;
                    LoginResponsePacket loginResponsePacket =
                            new LoginResponsePacket(status.ordinal(), "", "Failure");
                    Packet responsePacket = new Packet(response.ordinal(), loginResponsePacket);
                    responsePacket.sendPacket(incoming);
                }
                break;
            case CHAT_MESSAGE:
                ChatMessagePacket msgPacket = ChatMessagePacket.parseJSON(jsonObject);
                User messagingUser = handler.getEntityFromLoggedIn(incoming);
                if (msgPacket.getMessage().startsWith("!")) {
                    String[] command = msgPacket.getMessage().substring(1).split(" ");
                    handleCommand(messagingUser, command, handler);
                } else {
                    FromServer chatMessage = FromServer.CHATMESSAGE;
                    ChatMessagePacket chatMessagePacket = new ChatMessagePacket(messagingUser.getName() + ": " + msgPacket.getMessage());
                    Packet responsePacket = new Packet(chatMessage.ordinal(), chatMessagePacket);

                    if (messagingUser.isInLobby()) {
                        messagingUser.getLobby().broadcastPacket(responsePacket);
                    }
                }
                break;
            case CARD_PACKET:
                CardPacket cardPacket = CardPacket.parseJSON(jsonObject);
                User cardUser = handler.getEntityFromLoggedIn(incoming);
                cardUser.getPlayer().storeBurntCard(Tools.CARD_RECONSTRUCTOR.reconstructCard(cardPacket.getPriority()));
                break;
            case CARD_HAND_PACKET:
                User cardHandUser = handler.getEntityFromLoggedIn(incoming);
                int[] packetData = CardHandPacket.parseJSON(jsonObject).getHand();
                Card[] hand = new Card[packetData.length];
                for (int i = 0; i < hand.length; i++) {
                    hand[i] = Tools.CARD_RECONSTRUCTOR.reconstructCard(packetData[i]);
                }
                cardHandUser.getPlayer().storeSelectedCards(hand);
                break;
            case CREATE_LOBBY:
                User actionUser = handler.getEntityFromLoggedIn(incoming);
                CreateLobbyPacket lobbyPacket = CreateLobbyPacket.parseJSON(jsonObject);
                actionUser.createLobby(handler.game, lobbyPacket);
                break;
            case JOIN_LOBBY:
                User joiningUser = handler.getEntityFromLoggedIn(incoming);
                JoinLobbyPacket joinLobbyPacket = JoinLobbyPacket.parseJSON(jsonObject);
                joiningUser.joinLobby(handler.game, joinLobbyPacket.getLobbyName());
                break;
            case REQUEST_DATA:
                DataRequestPacket request = DataRequestPacket.parseJSON(jsonObject);
                User requestUser = handler.getEntityFromLoggedIn(incoming);

                switch (request.getRequest()) {
                    case LOBBY_LIST:
                        requestUser.getLobbyList(handler.game);
                        break;
                    case LOBBY_LEAVE:
                        requestUser.leaveLobby();
                        break;
                    case LOBBY_START:
                        requestUser.getLobby().startGameCountdown(requestUser);
                        break;
                    case LOG_OUT:
                        handler.logoutUser(requestUser);
                        break;
                }
                break;
            default:
                System.err.println("Unhandled packet: " + packetId.name());
                System.out.println("data: " + jsonObject.get("data"));
        }
    }

    /**
     * Parse command from packet and exectute action.
     *
     * @param messagingUser
     * @param command
     * @param handler
     */
    private void handleCommand(User messagingUser, String[] command, RoboCopServerHandler handler) {
        System.out.println(messagingUser.getName() + " sent command: " + command[0]);

        switch (command[0]) {
            case "players":
                messagingUser.sendServerMessage("There is currently " + RoboCopServerHandler.loggedInPlayers.size() + " player(s) online.");
                break;
            case "kick":
                if (StringUtilities.isStringInt(command[1])) {
                    messagingUser.getLobby().kickBySlot(Integer.parseInt(command[1]), messagingUser);
                } else {
                    messagingUser.getLobby().kickByName(command[1], messagingUser);
                }
                break;
            case "addcomp":
            case "addcpu":
            case "addai":
            case "add_ai":
                if (command.length == 2) {
                    if(StringUtilities.isStringInt(command[1])){
                        for (int i = 0; i < Integer.parseInt(command[1]); i++) {
                            messagingUser.getLobby().addArtificial(messagingUser);

                        }

                    }
                } else {
                    messagingUser.getLobby().addArtificial(messagingUser);

                }

                break;
            case "move":
                if (command.length > 2) {
                    if (Direction.fromString(command[1].toUpperCase()) != null) {
                        Direction direction = Direction.valueOf(command[1].toUpperCase());
                        if (StringUtilities.isStringInt(command[2])) {
                            messagingUser.sendServerMessage("You moved " + command[2] + " tiles.");
                            messagingUser.getPlayer().startMovement(direction, Integer.parseInt(command[2]), false);
                            return;
                        }
                    }
                }
                messagingUser.sendServerMessage("Error in command, proper usage: '!move north 3'.");

                break;
            case "card":
                if (command.length > 1) {
                    if (CardType.fromString(command[1].toUpperCase()) != null) {
                        CardType cardType = CardType.valueOf(command[1].toUpperCase());
                        Card card = new Card(999, cardType);
                        messagingUser.getLobby().getGame().handleMovement(messagingUser.getPlayer(), card);
                        return;
                    }
                }
                messagingUser.sendServerMessage("Error in command, proper usage: '!card rotateleft'.");
                messagingUser.sendServerMessage("Available cards: rotateleft, rotateright, rotate180");
                messagingUser.sendServerMessage("forward1, forward2, forward3, backward1");

                break;
            case "whisper":
            case "w":
                if (command.length > 2) {
                    StringBuilder message = new StringBuilder();
                    for (int i = 2; i < command.length; i++) {
                        message.append(command[i]).append(" ");

                    }
                    messagingUser.getFriendsList().sendWhisper(message.toString(), command[1], messagingUser, handler);
                }
                break;
            case "f":
            case "friends":
                messagingUser.getFriendsList().executeCommand(messagingUser, command, handler);
                break;
            default:
                messagingUser.sendServerMessage("Command not found \"" + command[0] + "\".");
                break;
        }
    }


    /**
     * User failed auth because a user with the same name is already loggid in, send a message to the new connection to
     * inform them.
     *
     * @param incoming
     * @param name
     */
    private void AlreadyLoggedIn(Channel incoming, String name) {
        FromServer response = FromServer.LOGINRESPONSE;
        LoginResponseStatus status = LoginResponseStatus.ALREADY_LOGGEDIN;
        LoginResponsePacket loginResponsePacket =
                new LoginResponsePacket(status.ordinal(), name, "User already logged in");
        Packet responsePacket = new Packet(response.ordinal(), loginResponsePacket);
        responsePacket.sendPacket(incoming);
    }

}
