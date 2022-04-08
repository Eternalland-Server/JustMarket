package net.sakuragame.eternal.justmarket.impl;

import net.sakuragame.eternal.justmarket.core.TradeType;
import net.sakuragame.eternal.justmarket.core.commodity.CommodityType;
import net.sakuragame.eternal.justmarket.core.condition.SortCondition;

public interface ScreenView {

    void open();

    void open(TradeType tradeType);

    void open(CommodityType commodityType);

    void open(SortCondition sort);

    void open(int page);

    void open(String search);

    void open(String search, int page);

    void openManage();

    void openSellManage(int page);

    void openBuyManage(int page);

    void openPutSell();

    void openPutBuy();
}
