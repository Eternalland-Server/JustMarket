package net.sakuragame.eternal.justmarket.core.commodity;

import net.sakuragame.eternal.justmarket.JustMarket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SellCommodity extends Commodity {

    public SellCommodity(int trader, double price, CommodityType type, String itemID, int amount, String itemData, long expire) {
        super(UUID.randomUUID(), trader, price, type, itemID, amount, itemData, expire);
    }

    public SellCommodity(UUID uuid, int trader, double price, CommodityType type, String itemID, int amount, String itemData, long expire) {
        super(uuid, trader, price, type, itemID, amount, itemData, expire);
    }

    @Override
    public void saveToDB() {
        JustMarket.getStorageManager().insertSellCommodity(
                this.getUUID(),
                this.getTrader(),
                this.getPrice(),
                this.getType(),
                this.getItemID(),
                this.getAmount(),
                this.getItemData(),
                this.getExpire()
        );
    }

    @Override
    public int compareTo(@NotNull Commodity o) {
        return (int) (this.getPrice() - o.getPrice());
    }
}
