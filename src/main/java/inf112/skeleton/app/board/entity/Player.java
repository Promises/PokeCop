package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.common.packet.*;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.CardType;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.card.CardDeck;

import java.util.ArrayList;

public class Player {
    public String name;
    Robot robot = null;
    Vector2 initialPos;
    int initalHp;
    Directions initalDirection;
    public Card[] cards;
    public ArrayList<Card> selectedCards;

    /**
     * Player has its own class, which owns a robot, to avoid rendring on socket thread.
     * @param name
     * @param pos
     * @param hp
     * @param directions
     */
    public Player(String name, Vector2 pos, int hp, Directions directions) {
        this.name = name;
        this.initalHp = hp;
        this.initialPos = pos;
        this.initalDirection = directions;
        this.selectedCards = new ArrayList<>(5);
        //Tmp
        for(int i = 0; i < 5; i++) {
            selectedCards.add(new Card(790, CardType.FORWARD3));
        }
    }

    /**
     * If robot is not yet created for player it should create it.
     *
     * TODO: move packets related to player actions here.
     */
    public void update() {
        if(robot == null){
            this.robot = new Robot(initialPos.x, initialPos.y, this);
            RoboRally.gameBoard.addEntity(robot);
        }
        if(!RoboRally.gameBoard.hud.hasDeck()) {
            if (cards != null) {
                RoboRally.gameBoard.hud.addDeck();
            }
        }

    }

    /**
     * Fill in first empty slot in the players card-hand.
     * @param packet A single card to be added to the hand.
     */
    public void receiveCardPacket(CardPacket packet) {
        for(int i = 0; i < cards.length; i++) {
            if(cards[i] == null) {
                cards[i] = Tools.CARD_RECONSTRUCTOR.reconstructCard(packet.getPriority());
                return;
            }
        }
        RoboRally.gameBoard.hud.addDeck();
    }

    /**
     * Receive a fresh hand from the server. Overwrites anything the player had before.
     * @param packet An array of cards.
     */
    public void receiveCardHandPacket(CardHandPacket packet) {
        int[] foo = packet.getHand();
        if(cards == null) {
            cards = new Card[9];
        }
        if (foo.length != cards.length) {
            return;
        }
        for(int i = 0; i < foo.length; i++) {
            cards[i] = Tools.CARD_RECONSTRUCTOR.reconstructCard(foo[i]);
        }
    }

    /**
     * Creates a packet containing move instructions from the selected cards.
     * @return True if packet could be constructed and has been sent, false otherwise.
     */
    public boolean sendNextSelectedCard() {
        CardDeck deck = new CardDeck();
        CardPacket data = new CardPacket(deck.dealCard());
        Packet packet = new Packet(ToServer.CARD_PACKET.ordinal(), data);
        RoboRally.channel.writeAndFlush(Tools.GSON.toJson(packet) + "\r\n");
        return true;
    }
        /*if(selectedCards.size() == 0) {
            return false;
        }
        CardPacket data = new CardPacket(selectedCards.remove(0));
        Packet packet = new Packet(ToServer.CARD_PACKET.ordinal(), data);
        RoboRally.channel.writeAndFlush(Tools.GSON.toJson(packet) + "\r\n");
        return true;/*
    }



    /**
     * Accept packet related to any changes to this player, checks if its needed then applies changes.
     *
     * TODO: check if data needs changing
     * @param update
     */
    public void updateRobot(UpdatePlayerPacket update){
        robot.updateMovement(update);
    }


    public Robot getRobot(){
        return this.robot;
    }
}
