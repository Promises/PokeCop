package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import inf112.skeleton.common.specs.Direction;

/**
 * Static load sprites to avoid loading them multiple times
 */
public class Sprites {
    public static TextureAtlas[] textureAtlases;
    public static Animation[][] robotAnimations;
    public static Animation[] animation_flag;
    @SuppressWarnings("unchecked")
    public static void setup(){
        textureAtlases = new TextureAtlas[9];
        robotAnimations = new Animation[9][4];

        textureAtlases[0] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesBlue2.atlas"));
        textureAtlases[1] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesDarkGreen.atlas"));
        textureAtlases[2] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesGreen.atlas"));
        textureAtlases[3] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesOrange.atlas"));
        textureAtlases[4] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesPink.atlas"));
        textureAtlases[5] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesRed.atlas"));
        textureAtlases[6] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSides.atlas"));
        textureAtlases[7] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesBrown.atlas"));
        for (int i = 0; i < 8; i++) {
            robotAnimations[i][Direction.NORTH.ordinal()] = new Animation(0.1f, textureAtlases[i].findRegions("robotAllSides_North"), Animation.PlayMode.LOOP);
            robotAnimations[i][Direction.SOUTH.ordinal()] = new Animation(0.1f, textureAtlases[i].findRegions("robotAllSides_South"), Animation.PlayMode.LOOP);
            robotAnimations[i][Direction.EAST.ordinal()] = new Animation(0.1f, textureAtlases[i].findRegions("robotAllSides_East"), Animation.PlayMode.LOOP);
            robotAnimations[i][Direction.WEST.ordinal()] = new Animation(0.1f, textureAtlases[i].findRegions("robotAllSides_West"), Animation.PlayMode.LOOP);
        }
        textureAtlases[8] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/trainer.atlas"));
        robotAnimations[8][Direction.NORTH.ordinal()] = new Animation(0.2f, textureAtlases[8].findRegions("trainer_North"), Animation.PlayMode.LOOP);
        robotAnimations[8][Direction.SOUTH.ordinal()] = new Animation(0.2f, textureAtlases[8].findRegions("trainer_South"), Animation.PlayMode.LOOP);
        robotAnimations[8][Direction.EAST.ordinal()] = new Animation(0.2f, textureAtlases[8].findRegions("trainer_East"), Animation.PlayMode.LOOP);
        robotAnimations[8][Direction.WEST.ordinal()] = new Animation(0.2f, textureAtlases[8].findRegions("trainer_West"), Animation.PlayMode.LOOP);
    }

}
