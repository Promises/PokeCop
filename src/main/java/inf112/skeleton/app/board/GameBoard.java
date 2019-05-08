package inf112.skeleton.app.board;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.entity.*;
import inf112.skeleton.app.board.entity.Entity;
import inf112.skeleton.app.board.entity.Flag;
import inf112.skeleton.app.board.entity.Player;
import inf112.skeleton.app.board.entity.Robot;
import inf112.skeleton.app.gameStates.Playing.HUD;
import inf112.skeleton.app.gameStates.Playing.State_Playing;
import inf112.skeleton.common.packet.data.*;
import inf112.skeleton.common.specs.TileDefinition;
import inf112.skeleton.common.utility.Tools;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GameBoard {

    public HUD hud;
    protected ArrayList<Entity> entities;
    protected Map<String, Player> players;
    public Player myPlayer = null;
    private CardHandPacket foo = null;
    public ArrayList<Laser> laserSources;
    public ArrayList<Entity>[] entityLocations;
    public ArrayList<Wall>[] walls;
    public ArrayList<Entity> renderWalls;


    public GameBoard() {
        entities = new ArrayList<>();
        players = new ConcurrentHashMap<>();
    }

    /**
     * Register TileEntity to the board
     *
     * @param tile
     * @param x
     * @param y
     */
    void addTileEntity(TiledMapTile tile, int x, int y, TiledMapTileLayer.Cell cell) {

        switch (TileDefinition.getTileById(tile.getId())) {
            case LASERSOURCE:
            case DLASERSOURCE:
                laserSources.add(new Laser(tile, x, y, cell));
                break;

            case WALL:
            case LWALL:
                Wall wall = new Wall(tile, x, y, cell);
                walls[Tools.coordToIndex(x, y, getWidth())].add(wall);
                renderWalls.add(wall);
                break;



        }
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void render(OrthographicCamera camera, SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.render(batch);
        }
        for (Entity entity: laserSources){
            entity.render(batch);
        }
        for (Entity wall : renderWalls) {
            wall.render(batch);
        }
        for (Entity entity : entities) {
            entity.renderName(batch, camera.zoom);
        }


    }

    public void update(State_Playing playing) {
        for (Player player : players.values()) {
            player.update();

        }
        for (Entity entity : entities) {
            entity.update();

        }
        for (Entity entity: laserSources){
            entity.update();
        }
        if (myPlayer != null) {
            if (myPlayer.cards == null && foo != null) {
                Gdx.app.log("Gameboard clientside - update", "Trying to receive lost cardHandPacket");
                myPlayer.receiveCardHandPacket(foo);
            }
            if (myPlayer.getRobot() != null) {
                if (playing.cameraHandler.isFollowing()) {
                    playing.cameraHandler.updatePosition(myPlayer.getRobot().getPos());
                }
            }
            myPlayer.update();
        }
    }


    public abstract void dispose();


    public abstract int getWidth();

    public abstract int getHeight();

    public abstract TiledMapTile getTile(TileDefinition definition);

    public void addPlayer(PlayerInitPacket pkt) {
        if (pkt.getUUID().equalsIgnoreCase(RoboRally.clientInfo)) {
            this.myPlayer = new Player(pkt.getUUID(), pkt.getName(), pkt.getPos(), pkt.getSlot(), pkt.getFacing());
            return;
        }
        this.players.put(pkt.getUUID(), new Player(pkt.getUUID(), pkt.getName(), pkt.getPos(), pkt.getHealth(), pkt.getSlot(), pkt.getFacing()));
    }

    public void setupPlayer(PlayerInitPacket pkt) {
        this.myPlayer = new Player(pkt.getUUID(), pkt.getName(), pkt.getPos(), pkt.getHealth(), pkt.getSlot(), pkt.getFacing());
    }

    public void removePlayer(PlayerRemovePacket pkt) {
        Player leavingPlayer = this.getPlayer(pkt.getUUID());
        this.entities.remove(leavingPlayer.getRobot());
        this.players.remove(pkt.getUUID());
    }

    public Player getPlayer(String uuid) {
        if (uuid.equalsIgnoreCase(RoboRally.clientInfo)) {
            return myPlayer;
        }
        return this.players.get(uuid);
    }

    public Map getPlayers() {
        return players;
    }

}
