package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.packet.FromServer;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.data.PlayerInitPacket;
import inf112.skeleton.common.packet.data.UpdatePlayerPacket;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.server.Instance.Game;
import inf112.skeleton.server.Instance.Lobby;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.user.User;

import java.util.ArrayList;


public class Player {
    private String name;
    private Vector2 currentPos;
    private Vector2 movingTo;
    private User owner;
    private PlayerBackup backup;

    private int slot;
    private Direction direction;
    private int movingTiles = 0;


    private int delayMove = 400;
    private int delayMessage = 1000;
    private long timeInit;
    private long timeMoved = 0;

    public Player(String name, Vector2 pos, int hp, int slot, Direction direction, User owner) {
        this.name = name;
        this.currentPos = pos;
        this.movingTo = new Vector2(currentPos.x, currentPos.y);
        this.slot = slot;
        this.direction = direction;
        this.owner = owner;

        owner.setPlayer(this);
        this.timeInit = System.currentTimeMillis();
        createBackup(currentPos);
    }

    /**
     * Get the current facing direction of player
     *
     * @return facing Direction
     */
    public Direction getDirection() {
        return this.direction;
    }


    /**
     * Get the current position of the player.
     *
     * @return Vector2 object with coordinates.
     */
    public Vector2 getCurrentPos() {
        return this.currentPos;
    }


    /**
     * Run tick based actions
     */
    public void update() {
        processingMovement(System.currentTimeMillis());
        if ((System.currentTimeMillis() - this.timeInit) >= this.delayMessage) {
            this.timeInit = System.currentTimeMillis();
        }

    }

    /**
     * Check if the player is ready to move or has finished moving
     *
     * @param currentTime the current time
     * @return boolean false if no movement is occurring
     */
    private boolean processingMovement(long currentTime) {
        if (this.currentPos.x == this.movingTo.x && this.currentPos.y == this.movingTo.y) {
            return false;
        }

        if ((currentTime - this.timeMoved) >= this.delayMove * movingTiles) {
            this.placeAt(this.movingTo.x, this.movingTo.y);
        }
        return true;

    }

    private boolean delayForMovement(long targetTime) {
        if(System.currentTimeMillis() < targetTime) {
            return false;
        }
        return true;
    }

    /**
     * Force place the player at a specific location
     *
     * @param x coordinate
     * @param y coordinate
     */
    private void placeAt(float x, float y) {
        this.currentPos.x = x;
        this.currentPos.y = y;
        this.movingTo.x = x;
        this.movingTo.y = y;
    }


    public void createBackup(Vector2 pos) {
        this.owner.sendChatMessage("Creating backup.");
        this.backup = new PlayerBackup(this, pos);

    }

    /**
     * Initialise the client who is linked to the player
     */
    public void sendInit() {
        System.out.println("called sendInit");

        FromServer initPlayer = FromServer.INIT_LOCALPLAYER;
        PlayerInitPacket playerInitPacket =
                new PlayerInitPacket(owner.getUUID(), name, currentPos, slot, direction);
        Packet initPacket = new Packet(initPlayer.ordinal(), playerInitPacket);
        owner.sendPacket(initPacket);
    }

    /**
     * Initialise the player with all clients in the game
     *
     * @param lobby of the game
     */
    public void initAll(Lobby lobby) {
        FromServer initPlayer = FromServer.INIT_PLAYER;
        PlayerInitPacket playerInitPacket =
                new PlayerInitPacket(owner.getUUID(), name, currentPos, slot, direction);
        Packet initPacket = new Packet(initPlayer.ordinal(), playerInitPacket);
        lobby.broadcastPacket(initPacket);
    }

    /**
     * Send an update pack of all changes that needs to be reflected in the client
     */
    public void sendUpdate() {
        if (owner.getLobby() != null) {
            FromServer pktId = FromServer.PLAYER_UPDATE;
            UpdatePlayerPacket updatePlayerPacket = new UpdatePlayerPacket(owner.getUUID(), direction, movingTiles, currentPos, movingTo);
            Packet updatePacket = new Packet(pktId.ordinal(), updatePlayerPacket);
            owner.getLobby().broadcastPacket(updatePacket);
        }
    }

    /**
     * Initialise movement for the player
     *
     * @param direction     to move in
     * @param initialAmount to move
     * @param pushed        player is being moving by robot or moved by conveyor-belt.
     * @return The amount of tiles the player moved.
     */
    public int startMovement(Direction direction, int initialAmount, boolean pushed) {
        if (!processingMovement(System.currentTimeMillis())) {
            int dx = 0;
            int dy = 0;
            int actual = initialAmount;     // The actual amount the player moved.
            Game game = owner.getLobby().getGame();
            GameBoard gameBoard = game.getGameBoard();
            ArrayList<Player> players = game.getPlayers();
            ArrayList<TileEntity> walls;

            this.timeMoved = System.currentTimeMillis();

            if (!pushed) {
                this.direction = direction;

            }

            switch (direction) {
                case SOUTH:
                    dy = -1;
                    break;
                case NORTH:
                    dy = 1;
                    break;
                case EAST:
                    dx = 1;
                    break;
                case WEST:
                    dx = -1;
                    break;
            }

            outerloop:
            for (int i = 0; i <= initialAmount; i++) {
                Vector2 toCheck = new Vector2(this.movingTo.x + dx * i, this.movingTo.y + dy * i);
                walls = gameBoard.getWallsAtPosition(toCheck);
                if (i == 0) {   //Our current tile. Check if we can leave.
                    for (TileEntity wall : walls) {
                        if (!wall.canLeave(direction)) {
                            actual = i;
                            break outerloop;
                        }
                    }

                } else {        //All other tiles in our path.
                    for (TileEntity wall : walls) {
                        if (!wall.canLeave(direction)) {
                            actual = i;
                            break outerloop;
                        }
                        if (!wall.canEnter(direction)) {
                            actual = i - 1;
                            break outerloop;
                        }
                    }

                    if (!gameBoard.isTileWalkable(toCheck)) {   //This is currently used for the outside edges of the map if I'm not mistaken, should be removed "Soon"TM
                        actual = i - 1;
                        break;
                    }

                    ArrayList<TileEntity> entities = gameBoard.getTileEntityAtPosition(toCheck);
                    for (TileEntity entity : entities) {
                        if(!entity.canContinueWalking()) {
                            actual = i;
                            break outerloop;
                        }
                    }

                    for (Player player : players) {
                        if (toCheck.dst(player.currentPos) == 0 && player != this) {
                            int delta = i - 1;    //Open tiles between the two robots.
                            actual = delta;

                            //Add other robot to queue with remaining amount and who it is moving by.
                            int remainder = initialAmount-delta;
                            game.movementStack.add(new ForceMovement(direction, remainder, player,this, true));
                            break outerloop;
                        }
                    }
                }
            }
            this.movingTo.add(dx * actual, dy * actual);
            this.movingTiles = actual;
            sendUpdate();
            return actual;
        }
        return 0;
    }

    public User getOwner() {
        return this.owner;
    }

    @Override
    public String toString() {
        return owner.getName();
    }

    public boolean isArtificial() {
        return owner.getChannel() == null;
    }

    public int forceMove(ForceMovement poll, Game game) {
        int amount = startMovement(poll.getDirection(), poll.getAmount(), poll.isPushed());
        if (poll.isPushed() && !poll.isTileAction()) {
            game.movementStack.add(new ForceMovement(poll.getDirection(), amount, poll.getAction(), poll.getMoving(), false));
        }
        return amount;
    }
}
