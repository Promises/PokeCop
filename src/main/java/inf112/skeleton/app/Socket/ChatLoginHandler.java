package inf112.skeleton.app.Socket;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import inf112.skeleton.app.GUI.ScrollableTextbox;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.entity.Player;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.MainMenu.State_MainMenu;
import inf112.skeleton.app.gameStates.Playing.HUD;
import inf112.skeleton.app.gameStates.Playing.State_Playing;
import inf112.skeleton.common.packet.*;
import inf112.skeleton.common.status.LoginResponseStatus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatLoginHandler extends SimpleChannelInboundHandler<String> {

    private String[] args = {"", ""};
    private Gson gson = new Gson();
    private RoboRally game;

    public ChatLoginHandler(RoboRally game) {
        this.game = game;
        game.setSocketHandler(this);
    }


    public void handleIncomingPacket(JsonObject jsonObject) throws Exception {
        System.out.println("Handling incoming packet...");
        OutgoingPacket packetId = OutgoingPacket.values()[jsonObject.get("id").getAsInt()];
        switch (packetId) {
            case LOGINRESPONSE:
                GameState currentGameState = game.gsm.peek();
                if (currentGameState instanceof State_MainMenu) {
                    // Read login-packet response and update the loginStatus variable in main menu.
                    ((State_MainMenu) currentGameState).loginStatus = LoginResponseStatus.values()[
                            gson.fromJson(jsonObject.get("data"), LoginResponsePacket.class).getStatusCode()];
                }
                break;

            case INIT_PLAYER:
                RoboRally.gameBoard.addPlayer(gson.fromJson(jsonObject.get("data"), PlayerInitPacket.class));
                break;

            case CHATMESSAGE:
                if (ScrollableTextbox.textbox != null) {
                    ChatMessagePacket chatMessagePacket = gson.fromJson(jsonObject.get("data"), ChatMessagePacket.class);
                    ScrollableTextbox.textbox.push(chatMessagePacket);
                }
                break;

            case PLAYER_UPDATE:
                UpdatePlayerPacket playerUpdate = gson.fromJson(jsonObject.get("data"), UpdatePlayerPacket.class);
                Player toUpdate = RoboRally.gameBoard.getPlayer(playerUpdate.getName());
                toUpdate.updateRobot(playerUpdate);
                break;

            default:
//                ChatGUI.textArea.append("resp: " + jsonObject.get("data") + "\n");
                break;
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext arg0, String arg1) throws Exception {
        System.out.println(arg1);
        if (arg1.startsWith("{")) {
//                GsonBuilder gsonBuilder = new GsonBuilder();
//                gsonBuilder.registerTypeAdapter(PacketReciever.class, new PacketTypeAdapter());
            JsonObject jsonObject = gson.fromJson(arg1, JsonObject.class);
            handleIncomingPacket(jsonObject);
        } else {
            if (arg1.equalsIgnoreCase("loginsuccess")) {
//                ChatGUI.main(args);

                ChatLogin.window.frmChat.dispose();
            } else if (arg1.equalsIgnoreCase("wrongpassword")) {
                ChatLogin.label_3.setText("Invalid password, try again!");
            } else if (arg1.equalsIgnoreCase("loggedinalready"))
                ChatLogin.label_3.setText("This account is already logged in!");
            else {
                if (arg1.startsWith("list:")) {
//                    ChatGUI.list.add(arg1.replaceAll("list:", ""));
                } else if (arg1.startsWith("listremove:")) {
//                    ChatGUI.list.remove(arg1.replaceAll("listremove:", ""));
                } else {
//                    ChatGUI.textArea.append(arg1 + "\n");
                }
            }
        }
    }
}