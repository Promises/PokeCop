package inf112.skeleton.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import inf112.skeleton.app.Action.InputHandler;
import inf112.skeleton.app.board.GameBoard;
import inf112.skeleton.app.board.entity.Robot;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.MainMenu.State_MainMenu;

public class RoboRally extends ApplicationAdapter {
    public static final int     WIDTH   = 1080,
                                HEIGHT  = 720;
    public static final String  TITLE   = "RoboRally";

    private SpriteBatch batch;
    private BitmapFont font;
    private Viewport viewport;
    public static InputHandler inputHandler;
    OrthographicCamera camera;

    public static GameBoard gameBoard;
    private GameStateManager gsm;

    Robot test1;


    @Override
    public void create() {
        batch = new SpriteBatch();
        gsm = new GameStateManager();
        gsm.push(new State_MainMenu(gsm));

        Gdx.gl.glClearColor(1,1,1,1);

        /*
        font = new BitmapFont();
        font.setColor(Color.RED);


        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();



        gameBoard = new TiledMapLoader();
        inputHandler = new InputHandler(camera);
        Gdx.input.setInputProcessor(inputHandler);

        //Testing testing, 1-2
        test1 = new Robot(10,10);
        gameBoard.addEntity(test1);
        */
    }

    @Override
    public void dispose() {
        batch.dispose();
        /*
        font.dispose();
        */
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gsm.update(0);
        gsm.render(batch);
        /*
        inputHandler.handleKeys();

        if (Gdx.input.isTouched()) {
            camera.translate((-Gdx.input.getDeltaX()) * camera.zoom, (Gdx.input.getDeltaY()) * camera.zoom);
        }
        if (Gdx.input.justTouched()) {
            Vector3 pos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            TileDefinition def = gameBoard.getTileDefinitionByLocation(0, pos.x, pos.y);
            if(def != null){
                System.out.printf("You clicked on tile %s with id %d.%n",def.getName(), def.getId());

            }
        }

        //TESTING, temp.
        if(inputHandler.keyUp(Input.Keys.NUM_1)) {
            gameBoard.moveEntityCard(test1, new Card(0, CardMove.FORWARD1));
        }
        if(inputHandler.keyUp(Input.Keys.NUM_2)) {
            gameBoard.moveEntityCard(test1, new Card(0, CardMove.FORWARD2));
        }
        if(inputHandler.keyUp(Input.Keys.NUM_3)) {
            gameBoard.moveEntityCard(test1, new Card(0, CardMove.FORWARD3));
        }
        if(inputHandler.keyUp(Input.Keys.R)) {
            gameBoard.moveEntityCard(test1, new Card(0, CardMove.BACKWARD1));
        }
        if(inputHandler.keyUp(Input.Keys.E)) {
            gameBoard.moveEntityCard(test1, new Card(0, CardMove.ROTATERIGHT));
        }
        if(inputHandler.keyUp(Input.Keys.Q)) {
            gameBoard.moveEntityCard(test1, new Card(0, CardMove.ROTATELEFT));
        }
        if(inputHandler.keyUp(Input.Keys.X)) {
            gameBoard.moveEntityCard(test1, new Card(0, CardMove.ROTATE180));
        }
        if(inputHandler.keyUp(Input.Keys.W)) {
            gameBoard.moveEntity(test1, Directions.NORTH);
        } else if(inputHandler.keyUp(Input.Keys.S)) {
            gameBoard.moveEntity(test1, Directions.SOUTH);
        }
        if(inputHandler.keyUp(Input.Keys.A)) {
            gameBoard.moveEntity(test1, Directions.WEST);
        } else if(inputHandler.keyUp(Input.Keys.D)) {
            gameBoard.moveEntity(test1, Directions.EAST);
        }

        camera.update();
        gameBoard.update();
        gameBoard.render(camera,batch);
        // 秒あたりのフレーム数
        batch.begin();
        font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight()-20);
        batch.end();
        */
    }

    @Override
    public void resize(int width, int height) {
        gsm.resize(width, height);
        /*
        viewport.update(width, height);
        */
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
