package inf112.skeleton.app.GUI;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class StatusBar extends Table {

    private LinkedHashMap<String, Table> rows;

    private HashMap<String, Image[]> lifeButtons;
    private HashMap<String, Image[]> damageButtons;
    private HashMap<String, Image> powerDownButtons;
    private HashMap<String, int[]> ldp;

    private int iconSize = 20;
    private int height = 26;


    private static final int username_width = 150,
            pad = 5;

    private int cardHeight = iconSize;
    private int cardWidth = (int) (128f / 192f * iconSize); // Card width / card height * scale

    private int width = username_width + pad + (pad + iconSize) + (pad + cardWidth);
    private boolean bigMode = false;

    private ArrayList<Player> players = new ArrayList<>();

    public StatusBar() {
        super();
        clear();
    }

    @Override
    public float getWidth() {
        return (float) width;
    }

    @Override
    public float getHeight() {
        return (float) (height * rows.size());
    }

    /**
     * Add the status of a new user to the bar.
     *
     * @param Player player
     */
    public void addStatus(Player player) {
        this.players.add(player);
    }

    public void createStatus(Player player) {
        Table userRow = new Table();
        userRow.setBackground(RoboRally.graphics.sb_bg);

        userRow.add(new Label(player.name + ":", RoboRally.graphics.labelStyle_markup_enabled)).size(username_width, height).pad(0, pad, 0, pad);

        // Power down
        Image powerDownButton = new Image(RoboRally.graphics.sb_powerDownInactive);
        userRow.add(powerDownButton).size(iconSize, iconSize).padRight(pad);
        powerDownButtons.put(player.name, powerDownButton);

        // Set initial life/damage/powerdown count.
        ldp.put(player.name, new int[]{3, 0, 0});

        add(userRow).size(width, height).row();
        rows.put(player.name, userRow);
    }


    public void addLife(String name) {
        lifeButtons.get(name)[ldp.get(name)[0]++ - 1].setDrawable(RoboRally.graphics.sb_life);
    }

    public void removeLife(String name) {
        lifeButtons.get(name)[ldp.get(name)[0]-- - 1].setDrawable(RoboRally.graphics.sb_lifeInactive);
    }

    /**
     * Notify that a powerdown is to happen next round.
     *
     * @param name      of user
     * @param powerDown or not
     */
    public void powerDown(String name, boolean powerDown) {
        powerDownButtons.get(name).setDrawable(powerDown ? RoboRally.graphics.sb_powerDown : RoboRally.graphics.sb_powerDownInactive);
        ldp.get(name)[2] = powerDown ? 1 : 0;
    }

    public int size() {
        return rows.size();
    }

    public void clear() {
        clearChildren();

        ldp = new HashMap<>();
        rows = new LinkedHashMap<>();
        lifeButtons = new HashMap<>();
        damageButtons = new HashMap<>();
        powerDownButtons = new HashMap<>();
    }

    public void setBig() {
        iconSize = 40;
        cardHeight = iconSize;
        cardWidth = (int) (128f / 192f * iconSize); // Card width / card height * scale
        width = username_width + pad + (pad + iconSize) + (pad + cardWidth);
        height = 46;
        bigMode = true;
    }

    public void setSmall() {
        iconSize = 20;
        cardHeight = iconSize;
        cardWidth = (int) (128f / 192f * iconSize); // Card width / card height * scale
        width = username_width + pad + (pad + iconSize) + (pad + cardWidth);
        height = 26;
        bigMode = false;

    }
}
