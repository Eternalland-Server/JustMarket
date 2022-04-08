package net.sakuragame.eternal.justmarket.impl;

import net.sakuragame.eternal.justmarket.core.TradeType;
import net.sakuragame.eternal.justmarket.core.commodity.CommodityType;
import net.sakuragame.eternal.justmarket.core.condition.SortCondition;
import org.bukkit.entity.Player;

public interface ScreenManager {

    void sendNotFoundView(Player player, boolean manage);

    void sendLoadingView(Player player, boolean manage);

    int sendViewByTradeType(Player player, TradeType tradeType, CommodityType commodityType, SortCondition sort, int page);

    int sendViewBySort(Player player, TradeType tradeType, CommodityType commodityType, SortCondition sort, int page);

    int sendViewBySearch(Player player, String search, TradeType tradeType, SortCondition sort, int page);

    int sendViewByCommodity(Player player, TradeType tradeType, CommodityType commodityType, SortCondition sort, int page);

    int sendManageView(Player player, TradeType tradeType);

    int sendManageView(Player player, TradeType tradeType, int page);

    void sendPutSellView(Player player);

    void sendPutBuyView(Player player);
}
