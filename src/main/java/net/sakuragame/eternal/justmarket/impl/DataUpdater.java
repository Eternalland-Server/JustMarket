package net.sakuragame.eternal.justmarket.impl;

import net.sakuragame.eternal.justmarket.core.TradeType;

import java.util.UUID;

public interface DataUpdater {

    void putCommodity(UUID uuid, TradeType type);

    void delCommodity(UUID uuid, TradeType type);

    void createTradeRecord(UUID uuid);

    void removeTradeRecord(UUID uuid);
}
