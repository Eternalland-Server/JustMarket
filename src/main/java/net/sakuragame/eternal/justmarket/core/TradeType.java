package net.sakuragame.eternal.justmarket.core;

import lombok.Getter;

@Getter
public enum TradeType {

    Mine(-1),
    Sell(0),
    Buy(1);

    private final int ID;

    TradeType(int ID) {
        this.ID = ID;
    }

    public static TradeType match(int id) {
        for (TradeType type : values()) {
            if (type.getID() == id) return type;
        }

        return null;
    }
}
