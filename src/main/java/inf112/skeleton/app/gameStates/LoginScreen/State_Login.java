package inf112.skeleton.app.gameStates.LoginScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.MainMenu.State_MainMenu;
import inf112.skeleton.common.packet.data.LoginPacket;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.status.LoginResponseStatus;
import inf112.skeleton.common.utility.Tools;
import io.netty.channel.Channel;

import static inf112.skeleton.common.status.LoginResponseStatus.NO_RESPONSE_YET;

public class State_Login extends GameState {
    private final Color color_primary   = new Color(0.6f,0.4f,0.2f,1);
    private String username, password;
    private TextField usernameField, passwordField;
    Stage stage;
    TextField messageToUser;
    public LoginResponseStatus loginStatus;
    long loginRequestTime, loginRequestNextStop;
    private Channel channel;

    public State_Login(GameStateManager gsm, Channel channel) {
        super(gsm, channel);
        this.channel = channel;

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        Skin skin = new Skin(Gdx.files.internal("graphics/ui/uiskin.json"));
        Table loginDetails = new Table();


        loginDetails.add(new Image(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/robocop_logo.png")))))).size(500,190).padBottom(30).colspan(2).row();

        // Add a field to display error messages to user...
        TextField.TextFieldStyle txtStyle = new TextField.TextFieldStyle();
        txtStyle.font = skin.getFont("default-font");
        txtStyle.fontColor = Color.RED;
        messageToUser = new TextField("", txtStyle);
        messageToUser.setDisabled(true);
        messageToUser.setAlignment(Align.center);
        loginDetails.add(messageToUser).size(600, 30).colspan(2).row();

        TextField tmp;

        // Add "username" text-box.
        tmp = new TextField("Username", skin);
        tmp.setDisabled(true);
        loginDetails.add(tmp).right();

        // Add input-field for typing username.
        usernameField = new TextField("", skin);
        usernameField.setMessageText("Username");
        usernameField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                username = textField.getText();
            }
        });
        loginDetails.add(usernameField).left().row();

        // Add "password" text-box.
        tmp = new TextField("Password", skin);
        tmp.setDisabled(true);
        loginDetails.add(tmp).right();

        // Add input-field for typing password.
        passwordField = new TextField("", skin);
        passwordField.setMessageText("********");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                // 10 = Keys.ENTER (Libgdx uses a different keyboard system)
                if ((int)c == 10)
                    enterLobby();
                else
                    password = textField.getText();
            }
        });
        loginDetails.add(passwordField).left().row();

        // Add login button.
        tmp = new TextField("Login ", skin);
        tmp.setDisabled(true);
        tmp.setAlignment(Align.center);
        tmp.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                enterLobby();
            }
        });
        loginDetails.add(tmp).colspan(2).center().padTop(5).size(300, 40).row();

        tmp = new TextField("Register", skin);
        tmp.setDisabled(true);
        tmp.setAlignment(Align.center);
        tmp.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked register button...");
            }
        });
        loginDetails.add(tmp).colspan(2).center().padTop(2).width(300).row();


        loginDetails.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + 100, Align.center);

        stage.addActor(loginDetails);
        Gdx.input.setInputProcessor(stage);

        enterLobby();
    }

    // Send login request to server and change game-state if login successful
    protected void enterLobby() {
        // If nothing typed, focus username field and return without contacting server.
        if (username == null || password == null) {
            stage.setKeyboardFocus(usernameField);
            loginStatus = null;
            return;
        }

        if (username.length() < 3) {
            stage.setKeyboardFocus(usernameField);
            messageToUser.getStyle().fontColor = Color.RED;
            messageToUser.setText("Username must have at least 3 characters...");
            loginStatus = null;
        return;
    }

        if (password.length() < 5) {
            stage.setKeyboardFocus(passwordField);
            messageToUser.getStyle().fontColor = Color.RED;
            messageToUser.setText("Password must have at least 5 characters...");
            loginStatus = null;
            return;
        }

        // Create login request packet
        String packetData = Tools.GSON.toJson(new Packet(0, new LoginPacket(username, password)));
        System.out.println("sending: " + packetData);

        // Send login request and wait for a response (Will be handled in update)
        loginStatus = NO_RESPONSE_YET;
        channel.writeAndFlush(packetData+"\r\n");

        loginRequestTime = System.currentTimeMillis();
        loginRequestNextStop = 0;
        messageToUser.getStyle().fontColor = Color.YELLOW;
        Gdx.input.setInputProcessor(null);
        stage.setKeyboardFocus(null);
        return;
    }




    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        if (loginStatus != null) {
            // Change game-state if successful login.
            switch(loginStatus) {
                case NO_RESPONSE_YET:
                    if (Gdx.input.isTouched()) {
                        messageToUser.setText("");
                        break;
                    }

                    long currentTime = System.currentTimeMillis();
                    if (currentTime >= loginRequestTime + loginRequestNextStop * 1000) {
                        messageToUser.setText("Logging in... (waited " + loginRequestNextStop + " seconds)");
                        loginRequestNextStop++;
                    }
                    return;
                case LOGIN_SUCCESS:
                    RoboRally.username = username;
                    gsm.set(new State_MainMenu(gsm, channel));
                    return;
                case ALREADY_LOGGEDIN:
                    messageToUser.getStyle().fontColor = Color.RED;
                    messageToUser.setText("User already logged in");

                    break;
                case WRONG_LOGINDETAILS:
                    messageToUser.getStyle().fontColor = Color.RED;
                    messageToUser.setText("Wrong username or password");
                    break;
                default:
                    messageToUser.getStyle().fontColor = Color.YELLOW;
                    messageToUser.setText("Cannot log in yet, this function has not yet been implemented.");
                    break;
            }
            Gdx.input.setInputProcessor(stage);
            stage.setKeyboardFocus(messageToUser);
            loginStatus = null;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(color_primary.r,color_primary.g, color_primary.b, color_primary.a);
        stage.draw();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void focus() {

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }
}
