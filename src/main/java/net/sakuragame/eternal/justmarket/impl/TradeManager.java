package net.sakuragame.eternal.justmarket.impl;

import net.sakuragame.eternal.justmarket.core.PageResult;
import net.sakuragame.eternal.justmarket.core.TradeType;
import net.sakuragame.eternal.justmarket.core.commodity.Commodity;
import net.sakuragame.eternal.justmarket.core.commodity.CommodityType;
import net.sakuragame.eternal.justmarket.core.condition.SortCondition;
import net.sakuragame.eternal.justmarket.core.record.TradeRecord;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface TradeManager {

    Commodity getCommodity(UUID uuid, TradeType tradeType);

    List<Commodity> getCommodity(List<UUID> uuids, TradeType tradeType);

    List<Commodity> getCommodity(TradeType tradeType);

    PageResult getCommodity(TradeType tradeType, SortCondition sort, int page);

    @Nullable PageResult getCommodity(String search, TradeType tradeType, SortCondition sort, int page);

    PageResult getCommodity(TradeType tradeType, CommodityType commodityType, SortCondition sort, int page);

    void loadCommodity(UUID uuid, TradeType tradeType);

    void removeCommodity(UUID uuid, TradeType tradeType);

    void loadTradeRecord(UUID uuid);

    void removeTradeRecord(UUID uuid);

    TradeRecord getTradeRecord(UUID uuid);

    boolean tradeSellCommodity(Player player, UUID commodityUUID);

    boolean tradeBuyCommodity(Player player, UUID commodityUUID);

    boolean putSellCommodity(Player player, ItemStack item, double price, int day);

    boolean putBuyCommodity(Player player, ItemStack item, double price, int day);

    void addSellLock(UUID uuid);

    void addBuyLock(UUID uuid);

    void delSellLock(UUID uuid);

    void delBuyLock(UUID uuid);

    boolean isSellLock(UUID uuid);

    boolean isBuyLock(UUID uuid);

    void reloadSearchMap();
}
