package net.sakuragame.eternal.justmarket.api;

import net.sakuragame.eternal.justmarket.JustMarket;
import net.sakuragame.eternal.justmarket.core.user.MarketAccount;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class JustMarketAPI {

    public static UUID shelveSell(Player player, ItemStack item, double price, int day) {
        return JustMarket.getTradeManager().putSellCommodity(player, item, price, day);
    }

    public static UUID shelveBuy(Player player, ItemStack item, double price, int day) {
        return JustMarket.getTradeManager().putBuyCommodity(player, item, price, day);
    }

    public static boolean unshelveSell(Player player, UUID commodityUUID) {
        MarketAccount account = JustMarket.getUserManager().getAccount(player);
        return account.unShelveSellCommodity(commodityUUID);
    }

    public static boolean unshelveBuy(Player player, UUID commodityUUID) {
        MarketAccount account = JustMarket.getUserManager().getAccount(player);
        return account.unShelveBuyCommodity(commodityUUID);
    }
}
