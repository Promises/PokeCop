package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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


        FileHandle fileList = Gdx.files.internal("graphics/sprites/humans/filelist.txt");
        String files[] = fileList.readString().split("\\n");
        textureAtlases = new TextureAtlas[files.length];
        robotAnimations = new Animation[files.length][4];
        for (int i = 0; i < files.length; i++) {
            String filename = files[i];
            FileHandle fh = Gdx.files.internal("graphics/sprites/humans/" + filename);
            textureAtlases[i] = new TextureAtlas(fh);
            robotAnimations[i][Direction.NORTH.ordinal()] = new Animation(0.2f, textureAtlases[i].findRegions("trainer_North"), Animation.PlayMode.LOOP);
            robotAnimations[i][Direction.SOUTH.ordinal()] = new Animation(0.2f, textureAtlases[i].findRegions("trainer_South"), Animation.PlayMode.LOOP);
            robotAnimations[i][Direction.EAST.ordinal()] = new Animation(0.2f, textureAtlases[i].findRegions("trainer_East"), Animation.PlayMode.LOOP);
            robotAnimations[i][Direction.WEST.ordinal()] = new Animation(0.2f, textureAtlases[i].findRegions("trainer_West"), Animation.PlayMode.LOOP);
        }



    }

}
