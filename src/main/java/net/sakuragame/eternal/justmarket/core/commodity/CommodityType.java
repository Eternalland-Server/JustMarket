package net.sakuragame.eternal.justmarket.core.commodity;

import net.sakuragame.eternal.justmarket.core.TradeType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum CommodityType {

    ALL(-1, "全部"),
    Equip(0, "装备", true),
    Pet(1, "宠物", true),
    Ore(2, "矿石"),
    Clothes(3, "时装", true),
    Mine_Sell(100, "我的出售"),
    Mine_Buy(101, "我的求购"),
    Put_Sell(200, "上架商品"),
    Put_Buy(201, "发起求购");

    private final int ID;
    private final String name;

    private final boolean needSeal;

    public final static String NBT_NODE = "market.type";

    CommodityType(int ID, String name) {
        this.ID = ID;
        this.name = name;
        this.needSeal = false;
    }

    CommodityType(int ID, String name, boolean needSeal) {
        this.ID = ID;
        this.name = name;
        this.needSeal = needSeal;
    }

    public static CommodityType match(int id) {
        for (CommodityType type : values()) {
            if (type.getID() == id) return type;
        }

        return ALL;
    }

    public static List<CommodityType> getCategory(TradeType tradeType) {
        switch (tradeType) {
            case Buy:
                return getBuyCategory();
            case Mine:
                return getMineCategory();
            default:
                return getSellCategory();
        }
    }

    public static List<CommodityType> getSellCategory() {
        return new ArrayList<CommodityType>() {{
            add(ALL);
            add(Equip);
            add(Pet);
            add(Ore);
            add(Clothes);
        }};
    }

    public static List<CommodityType> getBuyCategory() {
        return new ArrayList<CommodityType>() {{
            add(ALL);
            add(Pet);
            add(Ore);
            add(Clothes);
        }};
    }

    public static List<CommodityType> getMineCategory() {
        return new ArrayList<CommodityType>() {{
            add(Mine_Sell);
            add(Mine_Buy);
            add(Put_Sell);
            add(Put_Buy);
        }};
    }
}
