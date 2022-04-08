package net.sakuragame.eternal.justmarket.ui;

import net.sakuragame.eternal.dragoncore.network.PacketSender;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SlotCache {

    private final static Map<UUID, Map<String, ItemStack>> slotCache = new HashMap<>();

    public static void putSlot(Player player, String ident, ItemStack item) {
        putSlot(player.getUniqueId(), ident, item);
    }

    public static void putSlot(UUID uuid, String ident, ItemStack item) {
        if (item.getAmount() == 0) item = new ItemStack(Material.AIR);

        Map<String, ItemStack> cache = slotCache.computeIfAbsent(uuid, k -> new HashMap<>());
        cache.put(ident, item);
    }

    public static void putSlot(Player player, String ident, ItemStack item, boolean syncClient) {
        if (item.getAmount() == 0) item = new ItemStack(Material.AIR);

        Map<String, ItemStack> cache = slotCache.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        cache.put(ident, item);

        if (!syncClient) return;
        PacketSender.putClientSlotItem(player, ident, item);
    }

    @Nullable
    public static ItemStack getSlot(Player player, String ident) {
        return getSlot(player.getUniqueId(), ident);
    }

    @Nullable
    public static ItemStack getSlot(UUID uuid, String ident) {
        Map<String, ItemStack> cache = slotCache.get(uuid);
        if (cache == null) return null;
        return cache.get(ident);
    }

    @Nullable
    public static ItemStack removeSlot(Player player, String ident) {
        return removeSlot(player.getUniqueId(), ident);
    }

    @Nullable
    public static ItemStack removeSlot(UUID uuid, String ident) {
        Map<String, ItemStack> cache = slotCache.get(uuid);
        if (cache == null) return null;
        return cache.remove(ident);
    }

    public static void backSlot(Player player, String ...keys) {
        for (String ident : keys) {
            ItemStack item = removeSlot(player, ident);
            if (item != null) {
                player.getInventory().addItem(item);
            }
            PacketSender.putClientSlotItem(player, ident, new ItemStack(Material.AIR));
        }
    }

    public static void clearSlot(Player player) {
        clearSlot(player.getUniqueId());
    }

    public static void clearSlot(UUID uuid) {
        slotCache.remove(uuid);
    }
}
