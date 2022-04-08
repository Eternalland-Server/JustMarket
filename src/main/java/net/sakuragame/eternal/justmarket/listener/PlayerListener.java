package net.sakuragame.eternal.justmarket.listener;

import net.sakuragame.eternal.justmarket.JustMarket;
import net.sakuragame.eternal.justmarket.core.user.MarketAccount;
import net.sakuragame.eternal.justmarket.util.Scheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onFirst(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;

        UUID uuid = e.getUniqueId();
        JustMarket.getUserManager().loadAccount(uuid);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSecond(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) return;

        UUID uuid = e.getUniqueId();
        JustMarket.getUserManager().removeAccount(uuid);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent e) {
        Player player = e.getPlayer();

        MarketAccount account = JustMarket.getUserManager().getAccount(player);
        if (account != null) return;

        e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        e.setKickMessage("账户数据加载失败，请重新加入游戏");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        Scheduler.runLaterAsync(() -> {
            MarketAccount account = JustMarket.getUserManager().getAccount(uuid);
            if (account != null) {
                account.handleTradeRecord();
                account.handleExpireCommodity();
            }
        }, 20);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        JustMarket.getUserManager().removeData(player);
    }
}
