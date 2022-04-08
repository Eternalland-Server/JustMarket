package net.sakuragame.eternal.justmarket.impl;

import net.sakuragame.eternal.justmarket.core.user.MarketAccount;
import net.sakuragame.eternal.justmarket.core.TradeType;
import net.sakuragame.eternal.justmarket.core.commodity.Commodity;
import net.sakuragame.eternal.justmarket.core.commodity.CommodityType;
import net.sakuragame.eternal.justmarket.core.record.TradeRecord;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StorageManager {

    void init();

    Map<UUID, Commodity> getSellCommodity();

    Map<UUID, Commodity> getBuyCommodity();

    Map<UUID, TradeRecord> getTradeRecord();

    MarketAccount getMarketAccount(UUID uuid);

    List<UUID> getUserTradeRecord(UUID uuid);

    void insertSellCommodity(UUID uuid, int trader, double price, CommodityType type, String itemID, int amount, String itemData, long expire);

    int deleteSellCommodity(UUID uuid);

    void insertBuyCommodity(UUID uuid, int trader, double price, CommodityType type, String itemID, int amount, long expire);

    int deleteBuyCommodity(UUID uuid);

    void insertTradeRecord(UUID uuid, int seller, int buyer, TradeType type, String detail);

    int deleteTradeRecord(UUID uuid);

    @Nullable Commodity getSellCommodity(UUID uuid);

    @Nullable Commodity getBuyCommodity(UUID uuid);

    @Nullable TradeRecord getTradeRecord(UUID uuid);
}
