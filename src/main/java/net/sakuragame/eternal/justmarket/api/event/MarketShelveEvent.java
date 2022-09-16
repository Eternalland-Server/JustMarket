package net.sakuragame.eternal.justmarket.api.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class MarketShelveEvent extends JustEvent {

    private final ItemStack item;
    private final int amount;
    private final double price;
    private final long expire;

    public MarketShelveEvent(Player who, ItemStack item, int amount, double price, long expire) {
        super(who);
        this.item = item;
        this.amount = amount;
        this.price = price;
        this.expire = expire;
    }
}
