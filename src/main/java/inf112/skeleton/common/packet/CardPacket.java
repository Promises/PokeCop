package inf112.skeleton.common.packet;

import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.CardType;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.common.specs.Card;

public class CardPacket implements PacketData {
    int priority;

    public CardPacket(int priority) {
        this.priority = priority;
    }

    public CardPacket(Card card) {
        this.priority = card.getPriority();
    }

    public static CardPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), CardPacket.class);
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setPriority(Card card) { this.priority = card.getPriority(); }
}
