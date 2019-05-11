package inf112.skeleton.app.Socket;


import com.google.gson.JsonObject;
import inf112.skeleton.app.GUI.ChatBox;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.entity.Player;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.LoginScreen.State_Login;
import inf112.skeleton.app.gameStates.MainMenu.State_MainMenu;
import inf112.skeleton.app.gameStates.Playing.State_Playing;
import inf112.skeleton.common.packet.FromServer;
import inf112.skeleton.common.packet.data.*;
import inf112.skeleton.common.status.LoginResponseStatus;
import inf112.skeleton.common.utility.Tools;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class GameSocketHandler extends SimpleChannelInboundHandler<String> {
    private RoboRally game;

    public GameSocketHandler(RoboRally game) {
        this.game = game;
    }


    /**
     * Read message from server and decide what to do with it
     *
     * @param jsonObject
     */
    public void handleIncomingPacket(JsonObject jsonObject) {
        FromServer packetId = FromServer.values()[jsonObject.get("id").getAsInt()];
        switch (packetId) {
            case LOGINRESPONSE:
                GameState currentGameState = game.gsm.peek();
                if (currentGameState instanceof State_Login) {
                    // Read login-packet response and update the loginStatus variable in main menu.
                    ((State_Login) currentGameState).loginStatus = LoginResponseStatus.values()[
                            LoginResponsePacket.parseJSON(jsonObject).getStatusCode()];
                }
                break;
            case INIT_CLIENT:
                RoboRally.setClientInfo(ClientInitPacket.parseJSON(jsonObject));
                break;
            case INIT_MAP:
                RoboRally.roboRally.setBoard(InitMapPacket.parseJSON(jsonObject));
                break;
            case INIT_PLAYER:
                RoboRally.gameBoard.addPlayer(PlayerInitPacket.parseJSON(jsonObject));
                break;
            case INIT_LOCALPLAYER:
                RoboRally.gameBoard.setupPlayer(PlayerInitPacket.parseJSON(jsonObject));
                break;
            case TIMER:
                RoboRally.roboRally.timerPacket = TimerPacket.parseJSON(jsonObject);
                break;
            case CHATMESSAGE:
                if (ChatBox.chatBox != null) {
                    ChatMessagePacket chatMessagePacket = ChatMessagePacket.parseJSON(jsonObject);
                    ChatBox.chatBox.addMessage(chatMessagePacket);
                }
                break;
            case PLAYER_UPDATE:
                UpdatePlayerPacket playerUpdate = UpdatePlayerPacket.parseJSON(jsonObject);
                Player toUpdate = RoboRally.gameBoard.getPlayer(playerUpdate.getUUID());
                toUpdate.update();
                toUpdate.receiveUpdatePacket(playerUpdate); //TODO refactor.
                break;
            case REMOVE_PLAYER:
                PlayerRemovePacket playerRemovePacket = PlayerRemovePacket.parseJSON(jsonObject);
                if (RoboRally.gameBoard != null) {
                    RoboRally.gameBoard.removePlayer(playerRemovePacket);
                }
                break;
            case JOIN_LOBBY_RESPONSE:
                LobbyJoinResponsePacket lobbyJoinResponsePacket = LobbyJoinResponsePacket.parseJSON(jsonObject);

                if (RoboRally.roboRally.gsm.peek() instanceof State_MainMenu) {
                    ((State_MainMenu) RoboRally.roboRally.gsm.peek()).packets_LobbyJoin.add(lobbyJoinResponsePacket);
                }
                break;
            case LIST_LOBBIES:
                LobbiesListPacket lobbiesListPacket = LobbiesListPacket.parseJSON(jsonObject);

                if (RoboRally.roboRally.gsm.peek() instanceof State_MainMenu) {
                    ((State_MainMenu) RoboRally.roboRally.gsm.peek()).packets_LobbyList.add(lobbiesListPacket);
                }
                break;
            case STATE_CHANGED:
                StateChangePacket stateChangePacket = StateChangePacket.parseJSON(jsonObject);

                switch (stateChangePacket.getState()) {
                    case PLAYER_KICKED:
                    case PLAYER_WINNER:
                        if (RoboRally.roboRally.gsm.peek() instanceof State_MainMenu) {
                            ((State_MainMenu) RoboRally.roboRally.gsm.peek()).leaveLobby();
                            ((State_MainMenu) RoboRally.roboRally.gsm.peek()).setFreeze(false);
                        }
                        if (RoboRally.roboRally.gsm.peek() instanceof State_Playing) {
                            ((State_Playing) RoboRally.roboRally.gsm.peek()).stateChange = stateChangePacket.getState();

                        }
                        break;
                    case GAME_START:
                        if (RoboRally.roboRally.gsm.peek() instanceof State_MainMenu) {
                            ((State_MainMenu) RoboRally.roboRally.gsm.peek()).packets_GameStart.add(Boolean.TRUE);
                        }
                        break;
                }
                break;
            case LOBBY_UPDATE:
                LobbyUpdatePacket lobbyUpdatePacket = LobbyUpdatePacket.parseJSON(jsonObject);

                if (RoboRally.roboRally.gsm.peek() instanceof State_MainMenu) {
                    ((State_MainMenu) RoboRally.roboRally.gsm.peek()).packets_LobbyUpdates.add(lobbyUpdatePacket);
                }
                break;
            case ERROR_LOBBY_RESPONSE:
                RoboRally.roboRally.gsm.peek().addMessageToScreen("Lobby already exists...");
                break;
            default:
                System.err.println("Unhandled packet: " + packetId.name());
                System.out.println("data: " + jsonObject.get("data"));
                break;
        }
    }

    /**
     * Read and incoming packet, check if it follows the correct specifications, parse and send it to the packet handler.
     *
     * @param arg0
     * @param arg1
     */
    @Override
    protected void channelRead0(ChannelHandlerContext arg0, String arg1) {
        if (arg1.startsWith("{")) {
            JsonObject jsonObject = Tools.GSON.fromJson(arg1, JsonObject.class);
            handleIncomingPacket(jsonObject);
        }
    }
}