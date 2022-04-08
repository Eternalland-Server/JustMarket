package net.sakuragame.eternal.justmarket.util;

import net.sakuragame.eternal.justmarket.JustMarket;
import org.bukkit.Bukkit;

public class Scheduler {
    public static void run(Runnable runnable) {
        Bukkit.getScheduler().runTask(JustMarket.getInstance(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(JustMarket.getInstance(), runnable);
    }

    public static void runLater(Runnable runnable, int tick) {
        Bukkit.getScheduler().runTaskLater(JustMarket.getInstance(), runnable, tick);
    }

    public static void runLaterAsync(Runnable runnable, int tick) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(JustMarket.getInstance(), runnable, tick);
    }
}
