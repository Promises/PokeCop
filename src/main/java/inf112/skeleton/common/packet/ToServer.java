package inf112.skeleton.common.packet;

public enum ToServer {
    LOGIN,
    CREATE_LOBBY,
    JOIN_LOBBY,
    MOVEMENT_ACTION, //TODO: IMplement
    CHAT_MESSAGE,
    CARD_PACKET,
    CARD_HAND_PACKET,
    REQUEST_DATA,
    KEY_PACKET,
}
