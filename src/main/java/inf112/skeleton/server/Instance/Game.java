package inf112.skeleton.server.Instance;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.*;
import static inf112.skeleton.common.specs.Direction.values;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.WorldMap.TiledMapLoader;
import inf112.skeleton.server.WorldMap.entity.ForceMovement;
import inf112.skeleton.server.WorldMap.entity.Player;
import inf112.skeleton.server.user.User;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import static inf112.skeleton.server.Instance.GameStage.*;


public class Game {
    private final int ROUND_SELECT_TIMER = 30; //The time in seconds the player will have to select their cards.
    private Random random = new Random();

    private Lobby lobby;
    private ArrayList<Player> players = new ArrayList<>();
    public Stack<ForceMovement> movementStack = new Stack<>();
    private GameBoard gameBoard;

    private int tickCountdown = 0;  //If greater than 0, the server will not check or perform any action other than count down this number.
    private long timerStarted = 0;
    private long timerCountdownSeconds = 0;

    private GameStage gameStage = LOBBY;

    Game(Lobby lobby, MapFile mapFile) {
        this.lobby = lobby;
        gameBoard = new TiledMapLoader(mapFile);
    }


    /**
     * Main game loop
     */
    public void update() {
        //Update each player.
        for (Player player : players) {
            player.update();
        }

        gameBoard.update();

    }

    /**

     * Initialise the players

     */

    public void initPlayers() {
        System.out.println("[Game serverside - initPlayers] called initPlayers in game");
        User[] users = lobby.getUsers();
        for (int i = 0; i < users.length; i++) {
            if (users[i] != null) {
                Direction randomDir = values()[random.nextInt(values().length)];
                boolean suitableLocation = false;
                Vector2 loc = new Vector2(0, 0);

                whileloop:
                while (!suitableLocation) {
                    loc = new Vector2(random.nextInt(gameBoard.getWidth()), random.nextInt(gameBoard.getHeight()));
                    for (Player player : players) {
                        if (player.getCurrentPos().dst(loc) == 0) {
                            break whileloop;
                        }
                    }
                    suitableLocation = gameBoard.isTileWalkable(loc);

                }

                Player player = new Player(users[i].getName(), loc, 9, i, randomDir, users[i]);

                this.players.add(player);

                player.sendInit();

                player.initAll(lobby);

            }

        }

    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Set the amount of ticks (loops of update-method) that the server will skip.
     *
     * @param ticks
     */
    private void setTimerTicks(int ticks) {
        this.tickCountdown += ticks;
    }

    /**
     * Set a timer in seconds for the server while in WAITING-stage.
     *
     * @param seconds
     */
    private void setTimer(int seconds) {
        this.timerStarted = System.currentTimeMillis();
        this.timerCountdownSeconds = seconds * 1000;
    }

    /**
     * Check if server has waited for the set amount.
     *
     * @return
     */
    private boolean checkTimer() {
        if (timerStarted == 0)
            return true;
        return System.currentTimeMillis() >= timerStarted + timerCountdownSeconds;
    }

    public int getTimeToSelect() {
        return ROUND_SELECT_TIMER;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }
}
