package net.sakuragame.eternal.justmarket.core.condition;

import lombok.Getter;

@Getter
public enum SortCondition {

    Default(0),
    PriceAsc(1),
    PriceDesc(2),
    UnitPriceAsc(3),
    UnitPriceDesc(4);

    private final int ID;

    SortCondition(int ID) {
        this.ID = ID;
    }

    public String getUPSortTexture() {
        switch (this) {
            case UnitPriceAsc:
                return "ui/market/arrow/t.png";
            case UnitPriceDesc:
                return "ui/market/arrow/b.png";
            default:
            case Default:
            case PriceAsc:
            case PriceDesc:
                return "ui/market/arrow/d.png";
        }
    }

    public String getTPSortTexture() {
        switch (this) {
            case PriceAsc:
                return "ui/market/arrow/t.png";
            case PriceDesc:
                return "ui/market/arrow/b.png";
            default:
            case Default:
            case UnitPriceAsc:
            case UnitPriceDesc:
                return "ui/market/arrow/d.png";
        }
    }

    public static SortCondition match(int ID) {
        for (SortCondition condition : values()) {
            if (condition.getID() == ID) return condition;
        }

        return null;
    }
}
