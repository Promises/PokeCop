package inf112.skeleton.app.gameStates.Playing;

import com.badlogic.gdx.Input.Keys;
import inf112.skeleton.app.Action.InputContainer;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.ToServer;
import inf112.skeleton.common.packet.data.ChatMessagePacket;

public class MovementHandler {
    private InputContainer inputContainer;

    public MovementHandler(InputContainer inputContainer) {
        this.inputContainer = inputContainer;
    }

    public void handle() {
        handleKeys();
    }

    private boolean isPressed(int key) {
        return this.inputContainer.keys[key];
    }

    private void handleKeys() {
        if(isPressed(Keys.W)) {
            ChatMessagePacket cmp = new ChatMessagePacket("!move north 1");
            Packet packet = new Packet(ToServer.CHAT_MESSAGE.ordinal(), cmp);
            packet.sendPacket(RoboRally.channel);
        }
        if(isPressed(Keys.A)) {
            ChatMessagePacket cmp = new ChatMessagePacket("!move west 1");
            Packet packet = new Packet(ToServer.CHAT_MESSAGE.ordinal(), cmp);
            packet.sendPacket(RoboRally.channel);
        }
        if(isPressed(Keys.S)) {
            ChatMessagePacket cmp = new ChatMessagePacket("!move south 1");
            Packet packet = new Packet(ToServer.CHAT_MESSAGE.ordinal(), cmp);
            packet.sendPacket(RoboRally.channel);
        }
        if(isPressed(Keys.D)) {
            ChatMessagePacket cmp = new ChatMessagePacket("!move east 1");
            Packet packet = new Packet(ToServer.CHAT_MESSAGE.ordinal(), cmp);
            packet.sendPacket(RoboRally.channel);
        }
    }
}
