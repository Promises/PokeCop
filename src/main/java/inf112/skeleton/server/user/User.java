package inf112.skeleton.server.user;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.server.WorldMap.entity.Player;
import io.netty.channel.Channel;

public class User {
    public String name;
    public Channel channel;
    private boolean isLoggedIn;
    public String password;
    public UserPrivilege userRights;
    public Player player;


    public User(Channel channel) {
        this.channel = channel;
    }

    public User(String username, String password, Channel channel) {
        this.name = username;
        this.password = password;
        this.channel = channel;
        this.player = new Player(name, new Vector2(10,10), 10, Directions.SOUTH, this);
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public UserPrivilege getRights() {
        return userRights;
    }

    public void setRights(UserPrivilege rights) {
        userRights = rights;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}