package inf112.skeleton.app.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

/*
 * Circular message-box.
 */
public class ScrollableTextbox {
    TextField[] lines;
    int         lineAmount = 0,
                lineLimit,
                displayFrom = 0,
                numFields = 5;

    Stage stage;
    Table display;

    ImageButton button_up;
    ImageButton button_down;

    TextField.TextFieldStyle txtStyle;
    TextField inputField;
    Actor emptyField;

    int tableWidth = 600,
        tableHeight = 140;

    public ScrollableTextbox(int limit, InputMultiplexer inputMultiplexer) {
        lineLimit = limit;

        init_inputField();
        init_scrollButtons();
        emptyField = new Actor();

        txtStyle = new TextField.TextFieldStyle();
        txtStyle.font = new BitmapFont();
        txtStyle.fontColor = Color.YELLOW;

        lines = new TextField[limit];
        for (int i = 0 ; i < limit ; i++) {
            lines[i] = new TextField("", txtStyle);
            lines[i].setDisabled(true);
        }

        display = new Table();
        display.setDebug(false);
        display.setSize(tableWidth,tableHeight);
        display.background(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("chatStyleOpac.png")))));
        updateDisplay();

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        stage.addActor(display);
        inputMultiplexer.addProcessor(stage);
    }

    private void init_inputField() {
        inputField = new TextField("", new Skin(Gdx.files.internal("uiskin.json")));
        inputField.setMessageText("Type a message...");
        inputField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                if ((key == '\r' || key == '\n')) {
                    String inputText = inputField.getText();
                    if (!inputText.equals("")) {
                        push(inputText);
                    }
                    inputField.setText("");
                    stage.setKeyboardFocus(null);
                }
            }
        });
    }

    private void init_scrollButtons() {
        button_up   = new ImageButton(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("triangleBlack.png")))));
        button_down = new ImageButton(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("triangleBlackRot.png")))));

        button_up.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        scrollDisplay(-1);
                    }
                }
        );
        button_down.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        scrollDisplay(1);
                    }
                }
        );
    }


    public void scrollDisplay(int scrollAmount) {
        if (scrollAmount < 0) {
            scrollAmount = -((-scrollAmount) % lineLimit);
        }
        displayFrom = (displayFrom + scrollAmount + lineLimit) % lineLimit;

        updateDisplay();
    }


    public void push(String str) {
        lines[++lineAmount % lineLimit].setText(str);
        if (lineAmount % lineLimit == (displayFrom + 1) % lineLimit)
            scrollDisplay(1);
    }


    public void updateDisplay() {
        int arrowButtonSize = (tableHeight - 10) / numFields;

        display.clearChildren();

        // Add empty cells to the table.
        display.add(lines[(displayFrom-numFields+2+lineLimit) % lineLimit]).width(tableWidth-arrowButtonSize - 20).padLeft(10).padBottom(1).padTop(3).uniform();
        display.add(button_up).width(arrowButtonSize).height(arrowButtonSize).padRight(4);
        display.row();
        for (int i = numFields-3 ; i >= 0 ; i--) {
            display.add(lines[(displayFrom-i+lineLimit) % lineLimit]).width(tableWidth-arrowButtonSize - 20).padLeft(10).padBottom(1).uniform();
            display.add(emptyField);
            display.row();
        }

        display.add(inputField).width(tableWidth-arrowButtonSize - 30).height(arrowButtonSize-4).padLeft(5).padBottom(4).uniform();
        display.add(button_down).width(arrowButtonSize).height(arrowButtonSize).padRight(4);
        display.row();
    }

    public void render(Batch sb) {
        stage.draw();
    }

    public void setPosition(int x, int y) {
        display.setPosition(x,y);
    }
}
