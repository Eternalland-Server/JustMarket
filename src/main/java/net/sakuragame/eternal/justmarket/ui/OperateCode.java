package net.sakuragame.eternal.justmarket.ui;

import lombok.Getter;

@Getter
public enum OperateCode {

    TopCategory(100),
    LeftCategory(101),
    SellTrade(200),
    BuyTrade(201),
    UpPage(300),
    NextPage(301),
    UnitPriceSort(302),
    TotalPriceSort(303),
    PutSell(400),
    PutBuy(401),
    unShelveSell(402),
    unShelveBuy(403),
    Search(500);

    private final int ID;

    OperateCode(int ID) {
        this.ID = ID;
    }

    public static OperateCode match(int ID) {
        for (OperateCode code : values()) {
            if (code.getID() == ID) return code;
        }

        return null;
    }
}
