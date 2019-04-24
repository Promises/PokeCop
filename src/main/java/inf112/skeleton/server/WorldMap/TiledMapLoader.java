package inf112.skeleton.server.WorldMap;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import inf112.skeleton.common.specs.MapFile;
import inf112.skeleton.common.specs.TileDefinition;
import inf112.skeleton.server.WorldMap.entity.TileEntity;

import java.util.ArrayList;

public class TiledMapLoader extends GameBoard {

    private TiledMap tiledMap;

    public TiledMapLoader(MapFile file) {
        super();
        tiledMap = new TmxMapLoader().load(file.filename);
        walls = new ArrayList[getHeight() * getWidth()];
        tileEntities = new ArrayList[getHeight() * getWidth()];
        for (int i = 0; i < walls.length; i++) {
            walls[i] = new ArrayList<TileEntity>();
            tileEntities[i] = new ArrayList<TileEntity>();
        }
        // CHeck for tile entities like lasers and black holes
        for (int i = 0; i < tiledMap.getLayers().getCount(); i++) {
            TiledMapTileLayer layer = ((TiledMapTileLayer) tiledMap.getLayers().get(i));
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                    if (cell != null) {
                        TiledMapTile tile = cell.getTile();
                        if (tile != null) {
                            addTileEntity(tile, x, y, cell);
                        }
                    }
                }
            }
        }

    }


    /**
     * Clock based events owned by the board.
     */
    @Override
    public void update() {
        super.update();
    }


    /**
     * Gets a tile at a specified coordinate on the game board.
     *
     * @param layer
     * @param col
     * @param row
     * @return TileDefinition of given tileCoords
     */
    @Override
    public TileDefinition getTileDefinitionByCoordinate(int layer, int col, int row) {
        TiledMapTileLayer.Cell cell = ((TiledMapTileLayer) tiledMap.getLayers().get(layer)).getCell(col, row);
        if (cell != null) {

            TiledMapTile tile = cell.getTile();
            if (tile != null) {
                int id = tile.getId();
                return TileDefinition.getTileById(id);
            }
        }
        return null;
    }

    /**
     * Get TiledMapTileLayer Cell at a given coordinate
     *
     * @param layer
     * @param col
     * @param row
     * @return Cell if found, null if not found
     */
    @Override
    public TiledMapTileLayer.Cell getCellByCoordinate(int layer, int col, int row) {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(layer)).getCell(col, row);
    }

    /**
     * Gets the rotation of a tile,
     * useful for checking which way a belt might push a player
     *
     * @param layer
     * @param col
     * @param row
     * @return rotation
     */
    @Override
    public int getTileRotationByCoordinate(int layer, int col, int row) {
        TiledMapTileLayer.Cell tmt = getCellByCoordinate(0, col, row);
        if (tmt != null) {
            return tmt.getRotation();
        }
        return 0;
    }

    /**
     * Get the board width
     *
     * @return width
     */
    @Override
    public int getWidth() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getWidth();
    }

    /**
     * Get the board height
     *
     * @return height
     */
    @Override
    public int getHeight() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getHeight();
    }

    /**
     * Get the count of board layers
     *
     * @return layer count
     */
    @Override
    public int getLayers() {
        return tiledMap.getLayers().getCount();
    }
}
