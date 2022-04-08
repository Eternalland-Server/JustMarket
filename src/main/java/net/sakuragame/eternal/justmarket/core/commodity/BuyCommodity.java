package net.sakuragame.eternal.justmarket.core.commodity;

import net.sakuragame.eternal.justmarket.JustMarket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BuyCommodity extends Commodity {

    public BuyCommodity(int trader, double price, CommodityType type, String itemID, int amount, long expire) {
        super(UUID.randomUUID(), trader, price, type, itemID, amount, expire);
    }

    public BuyCommodity(UUID uuid, int trader, double price, CommodityType type, String itemID, int amount, long expire) {
        super(uuid, trader, price, type, itemID, amount, expire);
    }

    @Override
    public void saveToDB() {
        JustMarket.getStorageManager().insertBuyCommodity(
                this.getUUID(),
                this.getTrader(),
                this.getPrice(),
                this.getType(),
                this.getItemID(),
                this.getAmount(),
                this.getExpire()
        );
    }

    @Override
    public int compareTo(@NotNull Commodity o) {
        return (int) (this.getPrice() - o.getPrice());
    }
}
