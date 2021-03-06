package inf112.skeleton.app.Action;

import com.badlogic.gdx.scenes.scene2d.Stage;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.ToServer;
import inf112.skeleton.common.packet.data.KeyPacket;

/**
 * Each key on the keyboard (with some exceptions) are represented in Keys.class as an int between 0 and 255.
 * To update key status, you change the specific index. To check if a key is pressed,
 * you look up the boolean value at the key's index.
 * This approach makes it much easier to implement additional keys,
 * as you only need to add an additional line to the handleKeys method.
*/
public class InputContainer extends Stage {
    public boolean[] keys;
    public int scrollAmount;

    public InputContainer() {
        super();
        this.keys = new boolean[255];
        this.scrollAmount = 0;
    }

    @Override
    public boolean keyDown(int i) {
        keys[i] = true;
        if(18 < i && i < 23) {
            KeyPacket keyPacket = new KeyPacket(i, true);
            Packet packet = new Packet(ToServer.KEY_PACKET, keyPacket);
            if (RoboRally.gameBoard != null) {
                packet.sendPacket(RoboRally.channel);
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(int i) {
        keys[i] = false;
        if(18 < i && i < 23) {
            KeyPacket keyPacket = new KeyPacket(i, false);
            Packet packet = new Packet(ToServer.KEY_PACKET, keyPacket);
            if (RoboRally.gameBoard != null) {
                packet.sendPacket(RoboRally.channel);
            }
        }
        return true;
    }

    @Override
    public boolean scrolled(int i) {
        this.scrollAmount += i;
        return true;
    }
}
