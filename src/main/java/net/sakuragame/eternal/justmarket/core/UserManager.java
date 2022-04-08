package net.sakuragame.eternal.justmarket.core;

import net.sakuragame.eternal.justmarket.JustMarket;
import net.sakuragame.eternal.justmarket.core.user.MarketAccount;
import net.sakuragame.eternal.justmarket.ui.UserView;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    private final Map<UUID, MarketAccount> accounts;
    private final Map<UUID, UserView> caches;

    public UserManager() {
        this.accounts = new HashMap<>();
        this.caches = new HashMap<>();
    }

    public void loadAccount(Player player) {
        this.loadAccount(player.getUniqueId());
    }

    public void loadAccount(UUID uuid) {
        MarketAccount account = JustMarket.getStorageManager().getMarketAccount(uuid);
        if (account == null) return;
        accounts.put(uuid, account);
    }

    public MarketAccount getAccount(Player player) {
        return this.getAccount(player.getUniqueId());
    }

    public MarketAccount getAccount(UUID uuid) {
        return this.accounts.get(uuid);
    }

    public void removeAccount(Player player) {
        this.removeAccount(player.getUniqueId());
    }

    public void removeAccount(UUID uuid) {
        this.accounts.remove(uuid);
    }

    public UserView getCache(Player player) {
        return this.getCache(player.getUniqueId());
    }

    public UserView getCache(UUID uuid) {
        return this.caches.computeIfAbsent(uuid, key -> new UserView(uuid));
    }

    public void removeCache(Player player) {
        this.removeCache(player.getUniqueId());
    }

    public void removeCache(UUID uuid) {
        this.caches.remove(uuid);
    }

    public void removeData(Player player) {
        this.removeData(player.getUniqueId());
    }

    public void removeData(UUID uuid) {
        this.removeAccount(uuid);
        this.removeCache(uuid);
    }
}
