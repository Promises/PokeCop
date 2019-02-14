package inf112.skeleton.app.card;

public class Card {
    private int priority;
    //private String type;
    private CardMove type;

    public Card(int priority, CardMove type) {
        this.priority = priority;
        this.type = type;
    }

    public CardMove getType() {
        return type;
    }

    public int getPriority() {
        return priority;
    }

    public String toString() {
        return "Type: " + type + " | Priority: " + priority;
    }
}
