package inf112.skeleton.common.packet;

public enum FromServer {
    LOGINRESPONSE,
    INIT_CLIENT,
    INIT_PLAYER,
    INIT_ALLPLAYERS, //TODO: IMplement
    INIT_LOBBY, //TODO: IMplement
    INIT_GAME, //TODO: IMplement
    REMOVE_PLAYER,
    CHATMESSAGE,
    PLAYER_UPDATE,
    CARD_PACKET,
    CARD_HAND_PACKET,
    JOIN_LOBBY_RESPONSE,
    ERROR_LOBBY_RESPONSE,
    LIST_LOBBIES,
    STATE_CHANGED,
    LOBBY_UPDATE,
    INIT_MAP,
    INIT_LOCALPLAYER, // TODO: DO IT NOW!
}