package inf112.skeleton.app.gameStates.MainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.ToServer;
import inf112.skeleton.common.packet.data.CreateLobbyPacket;
import inf112.skeleton.common.specs.MapFile;
import inf112.skeleton.common.utility.Tools;
import io.netty.channel.Channel;

public class Tab_CreateLobby extends MenuTab {
    TextField lobbyName;
    SelectBox<String> selectBox;
    MapFile[] maps;


    public Tab_CreateLobby(GameStateManager gameStateManager, Channel ch) {
        super(gameStateManager, ch);

        Skin skin = new Skin(Gdx.files.internal("graphics/ui/uiskin.json"));
        TextField tmp;


        tmp = new TextField("Lobby name:", skin);
        tmp.setDisabled(true);
        display.add(tmp).size(200, 40);

        lobbyName = new TextField("", skin);
        display.add(lobbyName).size(500, 40).row();

        lobbyName.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                // 10 = Keys.ENTER (Libgdx uses a different keyboard system)
                if ((int)c == 10) {
                    lobby_create();
                }
            }
        });

        tmp = new TextField("Choose map: ", skin);
        tmp.setDisabled(true);
        display.add(tmp).size(200, 40);

        selectBox = new SelectBox<>(skin);

        maps = MapFile.values();
        String[] mapNames = new String[maps.length];
        for (int i = 0 ; i < maps.length ; i++) {
            mapNames[i] = maps[i].name;
        }

        selectBox.setItems(mapNames);
        selectBox.setSize(500, 300);
        display.add(selectBox).size(500, 40).row();

        TextButton tmp2 = new TextButton("Create new lobby", skin);
        tmp2.addListener(new ChangeListener() {
             @Override
             public void changed(ChangeEvent changeEvent, Actor actor) {
                 lobby_create();
             }
         });

        display.add(tmp2).size(400, 50).padTop(5).colspan(2);

        ((State_MainMenu)gsm.peek()).stage.setKeyboardFocus(lobbyName);
    }

    public void lobby_create() {
        String selected = selectBox.getSelected();
        for (MapFile map : maps) {
            if (map.name == selected) {
                Packet packet = new Packet(ToServer.CREATE_LOBBY.ordinal(), new CreateLobbyPacket(lobbyName.getText(), map));
                channel.writeAndFlush(Tools.GSON.toJson(packet) + "\r\n");

                State_MainMenu menu = ((State_MainMenu)gsm.peek());
                menu.setFreeze(false);
                menu.removeCurrentTab();

                return;
            }
        }
    }
}
