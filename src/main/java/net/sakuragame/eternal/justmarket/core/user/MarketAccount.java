package net.sakuragame.eternal.justmarket.core.user;

import net.sakuragame.eternal.justmarket.JustMarket;
import net.sakuragame.eternal.justmarket.core.TradeType;
import net.sakuragame.eternal.justmarket.core.commodity.Commodity;
import net.sakuragame.eternal.justmarket.core.record.TradeRecord;
import net.sakuragame.eternal.justmarket.file.sub.ConfigFile;
import net.sakuragame.eternal.justmarket.util.MailNotify;
import net.sakuragame.eternal.mail.api.model.AnnexData;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

public class MarketAccount {

    private final UUID uuid;
    private final List<UUID> sell;
    private final List<UUID> buy;

    public MarketAccount(UUID uuid, List<UUID> sell, List<UUID> buy) {
        this.uuid = uuid;
        this.sell = sell;
        this.buy = buy;
    }

    public UUID getUUID() {
        return uuid;
    }

    public List<UUID> getSell() {
        return sell;
    }

    public List<UUID> getBuy() {
        return buy;
    }

    public void addSell(UUID uuid) {
        this.sell.add(uuid);
    }

    public void addBuy(UUID uuid) {
        this.buy.add(uuid);
    }

    public List<Commodity> getSellCommodity() {
        return JustMarket.getTradeManager().getCommodity(this.sell, TradeType.Sell);
    }

    public List<Commodity> getBuyCommodity() {
        return JustMarket.getTradeManager().getCommodity(this.buy, TradeType.Buy);
    }

    public void removeSellCommodity(UUID uuid) {
        this.sell.remove(uuid);
    }

    public void removeBuyCommodity(UUID uuid) {
        this.buy.remove(uuid);
    }

    public int getRemainSellPutCount() {
        int total = ConfigFile.getPutCountOfSell().getUserCount(Bukkit.getPlayer(this.uuid));
        int current = this.sell.size();

        return Math.max(0, total - current);
    }

    public int getRemainBuyPutCount() {
        int total = ConfigFile.getPutCountOfBuy().getUserCount(Bukkit.getPlayer(this.uuid));
        int current = this.buy.size();

        return Math.max(0, total - current);
    }

    public boolean unShelveSellCommodity(UUID uuid) {
        if (!this.sell.remove(uuid)) return false;
        if (JustMarket.getTradeManager().isSellLock(uuid)) {
            return false;
        }

        JustMarket.getStorageManager().deleteSellCommodity(uuid);
        JustMarket.getTradeManager().removeCommodity(uuid, TradeType.Sell);
        JustMarket.getUpdater().delCommodity(uuid, TradeType.Sell);
        return true;
    }

    public boolean unShelveBuyCommodity(UUID uuid) {
        if (!this.buy.remove(uuid)) return false;
        if (JustMarket.getTradeManager().isBuyLock(uuid)) {
            return false;
        }

        JustMarket.getStorageManager().deleteBuyCommodity(uuid);
        JustMarket.getTradeManager().removeCommodity(uuid, TradeType.Buy);
        JustMarket.getUpdater().delCommodity(uuid, TradeType.Buy);
        return true;
    }

    public void handleTradeRecord() {
        List<UUID> records = JustMarket.getStorageManager().getUserTradeRecord(this.uuid);
        if (records.size() == 0) return;

        records.forEach(key -> {
            TradeRecord record = JustMarket.getTradeManager().getTradeRecord(key);
            if (record != null) {
                record.settlement();
                JustMarket.getStorageManager().deleteTradeRecord(key);
                JustMarket.getTradeManager().removeTradeRecord(key);
                JustMarket.getUpdater().removeTradeRecord(key);
            }
        });
    }

    public void handleExpireCommodity() {
        this.sell.forEach(Key -> {
            Commodity commodity = JustMarket.getTradeManager().getCommodity(Key, TradeType.Sell);
            if (commodity.isExpire()) {
                JustMarket.getStorageManager().deleteSellCommodity(Key);
                JustMarket.getTradeManager().removeCommodity(Key, TradeType.Sell);
                MailNotify.sendSellFailure(this.uuid, new AnnexData(commodity.getItemID(), commodity.getAmount(), commodity.getItemData()));
            }
        });

        this.buy.forEach(key -> {
            Commodity commodity = JustMarket.getTradeManager().getCommodity(key, TradeType.Buy);
            if (commodity.isExpire()) {
                JustMarket.getStorageManager().deleteBuyCommodity(key);
                JustMarket.getTradeManager().removeCommodity(key, TradeType.Buy);
                MailNotify.sendBuyFailure(this.uuid, commodity.getItemID(), commodity.getAmount(), (int) commodity.getPrice());
            }
        });
    }
}
