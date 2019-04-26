package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;


public class Flag extends Entity {
    private Animation<TextureRegion> currentAnimation;
    private BitmapFont font;
    private float stateTime;
    private int active;
    private int flagNumber;


    public Flag(float x, float y, int flagNumber) {
        super(x, y, EntityType.FLAG);
        this.active = 1;
        this.flagNumber = flagNumber;
    }

    /**
     * Disable the flag
     */
    public void disableFlag() {
        this.active = 0;
    }

    @Override
    public void update() {

    }

    @Override
    public void render(SpriteBatch batch) {
        currentAnimation = Sprites.animation_flag[active];
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, (pos.x * 64), (pos.y * 64), getWidth(), getHeight());
        renderNumber(batch);
    }

    @Override
    public void renderName(SpriteBatch batch, float scale) {
        return;
    }

    /**
     * Render the flags number
     *
     * @param batch
     */
    public void renderNumber(SpriteBatch batch) {
        if(this.font == null) {
            font = new BitmapFont();
        }
        if (active == 1) {
            final GlyphLayout layout = new GlyphLayout(font, "" + flagNumber);
            final float fontX = (pos.x * 64) + 3 + ((64 - layout.width) / 2);
            final float fontY = (pos.y * 64) + 26;
            font.setColor(255, 0, 0, 255);
            font.draw(batch, "" + flagNumber, fontX, fontY);
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Flag comparison = (Flag) obj;
        return (this.pos.dst(comparison.pos) == 0 && this.flagNumber == comparison.flagNumber);
    }

    public String toString() {
        return "[Flag number: " + flagNumber + " | x: " + getX() + ", y: " + getY() + "]";
    }
}
