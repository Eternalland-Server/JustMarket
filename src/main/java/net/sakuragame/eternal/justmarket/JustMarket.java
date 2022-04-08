package net.sakuragame.eternal.justmarket;

import net.sakuragame.eternal.justmarket.commands.MainCommand;
import net.sakuragame.eternal.justmarket.core.UserManager;
import net.sakuragame.eternal.justmarket.core.MarketManager;
import net.sakuragame.eternal.justmarket.core.MarketUpdater;
import net.sakuragame.eternal.justmarket.file.FileManager;
import net.sakuragame.eternal.justmarket.impl.DataUpdater;
import net.sakuragame.eternal.justmarket.impl.ScreenManager;
import net.sakuragame.eternal.justmarket.impl.StorageManager;
import net.sakuragame.eternal.justmarket.impl.TradeManager;
import net.sakuragame.eternal.justmarket.listener.MarketListener;
import net.sakuragame.eternal.justmarket.listener.PlayerListener;
import net.sakuragame.eternal.justmarket.listener.ui.CommonListener;
import net.sakuragame.eternal.justmarket.listener.ui.SlotListener;
import net.sakuragame.eternal.justmarket.storage.DatabaseManager;
import net.sakuragame.eternal.justmarket.ui.MarketUIManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class JustMarket extends JavaPlugin {
    @Getter private static JustMarket instance;

    @Getter private static DataUpdater updater;

    @Getter private static FileManager fileManager;
    @Getter private static StorageManager storageManager;
    @Getter private static UserManager userManager;
    @Getter private static TradeManager tradeManager;
    @Getter private static ScreenManager screenManager;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        instance = this;

        updater = new MarketUpdater(this);

        fileManager = new FileManager(this);
        fileManager.init();

        storageManager = new DatabaseManager();
        userManager = new UserManager();
        tradeManager = new MarketManager();
        screenManager = new MarketUIManager();

        this.registerListener(new PlayerListener());
        this.registerListener(new MarketListener());
        this.registerListener(new CommonListener());
        this.registerListener(new SlotListener());
        getCommand("jmarket").setExecutor(new MainCommand());

        long end = System.currentTimeMillis();

        getLogger().info("加载成功! 用时 %time% ms".replace("%time%", String.valueOf(end - start)));
    }

    @Override
    public void onDisable() {
        getLogger().info("卸载成功!");
    }

    public String getVersion() {
        String packet = Bukkit.getServer().getClass().getPackage().getName();
        return packet.substring(packet.lastIndexOf('.') + 1);
    }

    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public void reload() {
        fileManager.init();
    }
}
