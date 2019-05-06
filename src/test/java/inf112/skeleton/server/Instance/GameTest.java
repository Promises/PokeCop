package inf112.skeleton.server.Instance;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.*;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.GameWorldInstance;
import inf112.skeleton.server.WorldMap.entity.Flag;
import inf112.skeleton.server.WorldMap.entity.TileEntity;
import inf112.skeleton.server.card.CardDeck;
import inf112.skeleton.server.user.User;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Random;

public class GameTest {
    private static Application application;
    @BeforeClass
    public static void init() {
        application = new HeadlessApplication(new ApplicationListener() {
            @Override public void create() {}
            @Override public void resize(int i, int i1) {}
            @Override public void render() {}
            @Override public void pause() {}
            @Override public void resume() {}
            @Override public void dispose() {}
        });
        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl = Gdx.gl20;
    }

    Random random = new Random();

    GameWorldInstance dummyGWI = new GameWorldInstance();
    MapFile dummyMap = MapFile.GEAR_BOX;
    User dummyUser = new User("DummyUser", "Dummy", "Password1", null);
    Lobby dummyLobby = new Lobby("DummyLobby", dummyMap, dummyUser, dummyGWI);
    Game game = new Game(dummyLobby, dummyMap);


    @Test
    public void testFlags() {
        for (int i = 0; i < 300; i++) {
            game = new Game(dummyLobby, dummyMap);
            Flag[] gameFlags = game.getFlags();
            for (int j = 0; j < gameFlags.length; j++) {
                //Check for black holes.
                Vector2 loc = gameFlags[j].getPos();
                for (TileEntity entity : game.getGameBoard().getTileEntityAtPosition(loc)) {
                    if (entity != null) {
                        assertTrue(entity.canContinueWalking());
                    }
                }
                //Check for duplicates.
                for (int k = j+1; k < gameFlags.length; k++) {
                    assertNotEquals(gameFlags[j], gameFlags[k]);
                }
            }
        }
    }


    @AfterClass
    public static void cleanUp() {
        application.exit();
        application = null;
    }
}
