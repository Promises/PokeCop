package inf112.skeleton.app.board.entity;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.GameBoard;
import inf112.skeleton.common.packet.data.UpdatePlayerPacket;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.common.utility.Tools;

public class testTrainer extends Entity {
    private Direction facing;
    private int[] position;
    private int delayMove = 400;
    private long timeMoved = 0;
    private Vector2 tileTo;

    private int movementLength = 1;
    private int colour;

    private float stateTime = 0f;
    private BitmapFont font = new BitmapFont();

    private int movementDirection = 1;

    private boolean movedLastTick;
    private int index;
    private int stage = 0;

    public testTrainer(float x, float y, int slot, GameBoard gameBoard) {
        super(x, y, EntityType.ROBOT);
        this.tileTo = new Vector2(x, y);
        this.position = new int[2];
        this.position[0] = (int) x * 32;
        this.position[1] = (int) y * 32;
        this.colour = slot;
        this.facing = Direction.EAST;
        this.index = Tools.coordToIndex(pos.x, pos.y, gameBoard.getWidth());
        gameBoard.entityLocations[index].add(this);
    }


    /**
     * Get the current direction the robot is facing.
     *
     * @return A direction.
     */
    public Direction getFacingDirection() {
        return facing;
    }


    /**
     * Calculate current pixel to render entity at, based on time started moving, currenttime, location to and from,
     * the length of the movement, and the time it should take to move from a tile to another.
     *
     * @param currentTime
     * @return true if currently moving, false if not moving.
     */
    private boolean processingMovement(long currentTime) {
        if (this.pos.x == this.tileTo.x && this.pos.y == this.tileTo.y) {
            placeAt(this.pos.x, this.pos.y);
            return false;
        }
        int movementDelay = this.delayMove * movementLength;
        if ((currentTime - this.timeMoved) >= movementDelay) {
            this.placeAt(this.tileTo.x, this.tileTo.y);
        } else {
            int tileHeight = 32;
            int tileWidth = 32;
            switch (getFacingDirection()) {
                case NORTH:
                case SOUTH:
                    tileHeight = tileHeight * movementLength;
                    break;
                case WEST:
                case EAST:
                    tileWidth = tileWidth * movementLength;

            }
            this.position[0] = (int) (this.pos.x * 32);
            this.position[1] = (int) (this.pos.y * 32);
            if (this.tileTo.x != this.pos.x) {
                long diff = (long) ((((float) tileWidth) / movementDelay) * (currentTime - this.timeMoved));
                this.position[0] += (this.tileTo.x < this.pos.x ? 0 - diff : diff);
            }
            if (this.tileTo.y != this.pos.y) {
                long diff = (long) ((((float) tileHeight) / movementDelay) * (currentTime - this.timeMoved));
                this.position[1] += (this.tileTo.y < this.pos.y ? 0 - diff : diff);
            }


            this.position[0] = Math.round(this.position[0]);
            this.position[1] = Math.round(this.position[1]);


        }
        return true;

    }

    /**
     * Apply movement update.
     *
     * @param updatePlayerPacket
     */
    void updateMovement(UpdatePlayerPacket updatePlayerPacket) {
        this.tileTo = updatePlayerPacket.getToTile();
        this.facing = updatePlayerPacket.getDirection();
        this.movementDirection = updatePlayerPacket.getMovingTiles();
        this.movementLength = Math.abs(updatePlayerPacket.getMovingTiles());
        this.pos = updatePlayerPacket.getFromTile().cpy();
        int oldindex = index;
        index = Tools.coordToIndex(pos.x, pos.y, RoboRally.gameBoard.getWidth());
        RoboRally.gameBoard.entityLocations[oldindex].remove(this);
        RoboRally.gameBoard.entityLocations[index].add(this);
        this.timeMoved = System.currentTimeMillis();
        processingMovement(System.currentTimeMillis());

        movedLastTick = true;
    }

    /**
     * Set the current entity location.
     *
     * @param x
     * @param y
     */
    private void placeAt(float x, float y) {
        this.pos.x = x;
        this.pos.y = y;
        int oldindex = index;
        index = Tools.coordToIndex(pos.x, pos.y, RoboRally.gameBoard.getWidth());
        RoboRally.gameBoard.entityLocations[oldindex].remove(this);
        RoboRally.gameBoard.entityLocations[index].add(this);
        this.tileTo.x = x;
        this.tileTo.y = y;
        this.position[0] = (int) (this.pos.x * 32);
        this.position[1] = (int) (this.pos.y * 32);

    }

    @Override
    public void update() {
        boolean isMoving = processingMovement(System.currentTimeMillis());
        if (!isMoving) {
            int x = (int) getX();
            int y = (int) getY();
            switch (stage) {
                case 0:
                    facing = Direction.EAST;
                    x += 4;
                    break;
                case 1:
                    facing = Direction.NORTH;
                    y+=2;
                    break;
                case 2:
                    facing = Direction.EAST;
                    x+=2;
                    break;
                case 3:
                    facing = Direction.NORTH;
                    y+=1;
                    break;
                case 4:
                    facing = Direction.EAST;
                    x+=2;
                    break;
                case 5:
                    facing = Direction.SOUTH;
                    y-=3;
                    break;
                case 6:
                    facing = Direction.EAST;
                    x = RoboRally.gameBoard.getWidth() - 3;
                    break;
                case 7:
                    facing = Direction.NORTH;
                    y = RoboRally.gameBoard.getHeight() - 3;
                    break;
                case 8:
                    facing = Direction.WEST;
                    x = 2;
                    break;

                case 9:
                    facing = Direction.SOUTH;
                    y = 0;
                    break;

            }
            stage++;
            if (stage == 10  ) {
                stage = 0;
            }

            this.tileTo = new Vector2(x, y);
//            this.facing = updatePlayerPacket.getDirection();
//            this.movementDirection = updatePlayerPacket.getMovingTiles();
            this.movementLength = Math.abs(x - (int) getX()) + Math.abs(y - (int) getY());
            this.timeMoved = System.currentTimeMillis();
            processingMovement(System.currentTimeMillis());

        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Animation<TextureRegion> currentAnimation = Sprites.robotAnimations[colour][facing.ordinal()];

        stateTime += Gdx.graphics.getDeltaTime();

        int drawOff = -16;

        boolean isMoving = processingMovement(System.currentTimeMillis());

        //Is the robot currently moving
        if (isMoving) {
            //Yes it is moving, render animated frames.
            if (movementDirection >= 0) {
                currentAnimation.setPlayMode(Animation.PlayMode.NORMAL);
            } else {
                currentAnimation.setPlayMode(Animation.PlayMode.REVERSED);
            }
            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

            batch.draw(currentFrame, position[0] + drawOff, position[1], getWidth(), getHeight());

        } else {
            //No it is not moving, render static frame(first frame) statically.
            batch.draw(currentAnimation.getKeyFrames()[0], position[0] + drawOff, position[1], getWidth(), getHeight());

        }
        movedLastTick = isMoving;
    }


    /**
     * Renders the name seprately from sprite, avoids rendering entities ontop of name.
     *
     * @param batch
     */
    @Override
    public void renderName(SpriteBatch batch, float scale) {
        int x = position[0]/32;
        int y = position[1]/32;
        String name = "("+x+", "+y+")";
        final GlyphLayout layout = new GlyphLayout(font, name);
        final float fontX = position[0] + (32 - layout.width) / 2;
        font.setColor(Color.RED);
        font.getData().setScale(scale);
        font.draw(batch, name, fontX, position[1] + (78 + (10 * scale - 1) - 10));
    }

    @Override
    protected boolean isBlocking() {
        return true;
    }


    public Vector2 getPos() {
        return new Vector2(position[0], position[1]);
    }
}
