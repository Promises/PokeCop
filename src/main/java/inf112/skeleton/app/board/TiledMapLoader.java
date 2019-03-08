package inf112.skeleton.app.board;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import inf112.skeleton.common.specs.TileDefinition;

public class TiledMapLoader extends GameBoard {

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    public TiledMapLoader() {
        super();
        tiledMap = new TmxMapLoader().load("board/Board.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

    }

    @Override
    public void render(OrthographicCamera camera, SpriteBatch batch) {
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        super.render(camera, batch);
        batch.end();
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void dispose() {
        tiledMap.dispose();

    }

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

    @Override
    public int getWidth() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getWidth();
    }

    @Override
    public int getHeight() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getHeight();
    }

    @Override
    public int getLayers() {
        return tiledMap.getLayers().getCount();
    }
}
