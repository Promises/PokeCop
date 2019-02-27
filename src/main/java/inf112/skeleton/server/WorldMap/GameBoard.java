package inf112.skeleton.server.WorldMap;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import inf112.skeleton.app.card.Card;
import inf112.skeleton.app.card.CardMove;
import inf112.skeleton.server.WorldMap.entity.Directions;
import inf112.skeleton.server.WorldMap.entity.Entity;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public abstract class GameBoard {

    protected ArrayList<Entity> entities;

    public GameBoard() {
        entities = new ArrayList<>();
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void render(OrthographicCamera camera, SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.render(batch);

        }
    }

    public void update() {
        for (Entity entity : entities) {
            entity.update();

        }
    }

    public void moveEntity(Entity e, Directions dir) throws NoSuchElementException {
        if(entities.contains(e)) {
            switch(dir) {
                case NORTH:
                    e.moveY(1);
                    break;
                case SOUTH:
                    e.moveY(-1);
                    break;
                case WEST:
                    e.moveX(-1);
                    break;
                case EAST:
                    e.moveX(1);
                    break;
            }
        } else {
            throw new NoSuchElementException("Entity does not exist on this gameboard");
        }
    }

    public void moveEntityCard(Entity e, Card card) throws NoSuchElementException {
        if(entities.contains(e)) {
            CardMove type = card.getType();
            if(type == CardMove.ROTATERIGHT) {
                e.rotateRight();
            } else if(type == CardMove.ROTATELEFT) {
                e.rotateLeft();
            } else if(type == CardMove.ROTATE180) {
                e.rotate180();
            } else if(type == CardMove.FORWARD1) {
                e.moveForwardBackward(1);
            } else if(type == CardMove.FORWARD2) {
                e.moveForwardBackward(2);
            } else if(type == CardMove.FORWARD3) {
                e.moveForwardBackward(3);
            } else if(type == CardMove.BACKWARD1) {
                e.moveForwardBackward(-1);
            }
        } else {
            throw new NoSuchElementException("Entity does not exist on this gameboard");
        }
    }

    public abstract void dispose();

    /**
     * Gets a tile by pixel position within the board, at a specified layer.
     * 指定したレイヤーのボード上のピクセル位置を使ってタイルを見つけます。
     *
     * @param layer
     * @param x
     * @param y
     * @return
     */
    public TileDefinition getTileDefinitionByLocation(int layer, float x, float y) {
        return this.getTileDefinitionByCoordinate(
                layer,
                (int) (x / TileDefinition.TILE_SIZE),
                (int) (y / TileDefinition.TILE_SIZE)
        );
    }

    /**
     * Gets a tile at a specified coordinate on the game board.
     * ボード上の座標を使ってタイルを見つけます。
     *
     * @param layer
     * @param col
     * @param row
     * @return
     */
    public abstract TileDefinition getTileDefinitionByCoordinate(int layer, int col, int row);


    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getLayers();


}
