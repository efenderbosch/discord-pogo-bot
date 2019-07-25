package net.fender.pvpoke;

public class Move {

    private String moveId;
    private String name;
    private String type;
    // power
    // energy
    // energyGain
    // cooldown

    public String getMoveId() {
        return moveId;
    }

    public void setMoveId(String moveId) {
        this.moveId = moveId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
