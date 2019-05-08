package inf112.skeleton.server.Instance;

import inf112.skeleton.common.specs.*;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.WorldMap.TiledMapLoader;
import inf112.skeleton.server.WorldMap.entity.ForceMovement;
import inf112.skeleton.server.WorldMap.entity.Player;

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
