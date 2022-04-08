package net.sakuragame.eternal.justmarket.core.record;

import net.sakuragame.eternal.justmarket.core.TradeType;
import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class TradeRecord {

    private final UUID uuid;
    private final int seller;
    private final int buyer;

    public TradeRecord(int seller, int buyer) {
        this.uuid = UUID.randomUUID();
        this.seller = seller;
        this.buyer = buyer;
    }

    public TradeRecord(UUID uuid, int seller, int buyer) {
        this.uuid = uuid;
        this.seller = seller;
        this.buyer = buyer;
    }

    public static TradeRecord newInstance(UUID uuid, int seller, int buyer, String detail, TradeType type) {
        switch (type) {
            case Sell:
                return new SellTradeRecord(uuid, seller, buyer, detail);
            case Buy:
                return new BuyTradeRecord(uuid, seller, buyer, detail);
        }
        return null;
    }

    public UUID getUUID() {
        return uuid;
    }

    public abstract String geneDetail();

    public abstract boolean settlement();

    public abstract void saveToDB();
}
