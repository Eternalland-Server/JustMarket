package net.sakuragame.eternal.justmarket.listener;

import net.sakuragame.eternal.justmarket.JustMarket;
import ink.ptms.zaphkiel.api.event.PluginReloadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MarketListener implements Listener {

    @EventHandler
    public void onReload(PluginReloadEvent.Item e) {
        JustMarket.getTradeManager().reloadSearchMap();
    }
}
