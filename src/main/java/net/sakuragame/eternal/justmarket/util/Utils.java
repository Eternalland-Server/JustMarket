package net.sakuragame.eternal.justmarket.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.sakuragame.eternal.justmarket.core.commodity.CommodityType;
import com.taylorswiftcn.justwei.util.MegumiUtil;
import ink.ptms.zaphkiel.ZaphkielAPI;
import ink.ptms.zaphkiel.api.Item;
import ink.ptms.zaphkiel.api.ItemStream;
import ink.ptms.zaphkiel.taboolib.module.nms.ItemTag;
import ink.ptms.zaphkiel.taboolib.module.nms.ItemTagData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.UUID;

public class Utils {

    private final static JsonParser PARSER = new JsonParser();
    private final static DecimalFormat FORMAT = new DecimalFormat("#,###");

    public static long getExpire(int day) {
        return System.currentTimeMillis() + day * 24 * 60 * 60 * 1000L;
    }

    public static JsonElement parse(String data) {
        return PARSER.parse(data);
    }

    public static String formatPrice(double price) {
        String s = FORMAT.format(price);
        String c = "&f";

        if (price >= 1000000d) c = "&a";
        if (price >= 10000000d) c = "&b";
        if (price >= 100000000d) c = "&5";
        if (price >= 1000000000d) c = "&6";
        if (price >= 10000000000d) c = "&e";

        return c + s;
    }

    public static String formatExpire(long expire) {
        long value = expire - System.currentTimeMillis();
        int hour = (int) (value / 3600000);
        if (hour >= 24) {
            return (hour / 24) + "天后";
        }

        return hour + "小时后";
    }

    public static String getUUIDPart(UUID uuid) {
        String str = uuid.toString();
        return str.substring(0, 8);
    }

    public static CommodityType getCommodityType(ItemStack itemStack) {
        ItemStream itemStream = ZaphkielAPI.INSTANCE.read(itemStack);
        if (itemStream.isVanilla()) return CommodityType.ALL;

        return getCommodityType(itemStream);
    }

    public static CommodityType getCommodityType(ItemStream itemStream) {
        ItemTag itemTag = itemStream.getZaphkielData();
        ItemTagData itemTagData = itemTag.getDeep(CommodityType.NBT_NODE);
        if (itemTagData == null) return null;

        return CommodityType.match(itemTagData.asInt());
    }

    public static int getEmptySlotCount(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (!MegumiUtil.isEmpty(item)) continue;
            count++;
        }

        return count;
    }

    public static boolean checkItem(Player player, String itemID, int amount) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (MegumiUtil.isEmpty(itemStack)) continue;

            Item item = ZaphkielAPI.INSTANCE.getItem(itemStack);
            if (item == null) continue;

            String zapID = item.getId();
            if (!zapID.equals(itemID)) continue;

            amount -= itemStack.getAmount();

            if (amount <= 0) return true;
        }

        return false;
    }

    public static void consumeItem(Player player, String itemID, int amount) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (MegumiUtil.isEmpty(itemStack)) continue;

            Item item = ZaphkielAPI.INSTANCE.getItem(itemStack);
            if (item == null) continue;

            String zapID = item.getId();
            if (!zapID.equals(itemID)) continue;

            int surplus = amount - itemStack.getAmount();
            if (surplus == 0) {
                player.getInventory().setItem(i, new ItemStack(Material.AIR));
                return;
            }

            if (surplus < 0) {
                int abs = Math.abs(surplus);
                itemStack.setAmount(abs);
                player.getInventory().setItem(i, itemStack);
            }
            else {
                player.getInventory().setItem(i, new ItemStack(Material.AIR));
            }
        }
    }
}
