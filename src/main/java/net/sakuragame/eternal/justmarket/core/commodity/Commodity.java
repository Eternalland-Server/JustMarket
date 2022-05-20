package net.sakuragame.eternal.justmarket.core.commodity;

import com.google.gson.JsonObject;
import com.sakuragame.eternal.justattribute.core.special.EquipQuality;
import net.sakuragame.eternal.justmarket.JustMarket;
import net.sakuragame.eternal.justmarket.core.user.MarketAccount;
import net.sakuragame.eternal.justmarket.util.Utils;
import ink.ptms.zaphkiel.ZaphkielAPI;
import ink.ptms.zaphkiel.api.Item;
import ink.ptms.zaphkiel.api.ItemStream;
import lombok.Getter;
import net.sakuragame.serversystems.manage.client.api.ClientManagerAPI;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
public abstract class Commodity implements Comparable<Commodity> {

    private final UUID uuid;
    private final int trader;
    private final double price;
    private final CommodityType type;
    private final String itemID;
    private final int amount;
    private final String itemData;
    private final long expire;

    private ItemStack itemStack;

    public Commodity(UUID uuid, int trader, double price, CommodityType type, String itemID, int amount, long expire) {
        this(uuid, trader, price, type, itemID, amount, null, expire);
    }

    public Commodity(UUID uuid, int trader, double price, CommodityType type, String itemID, int amount, String itemData, long expire) {
        this.uuid = uuid;
        this.trader = trader;
        this.price = price;
        this.type = type;
        this.itemID = itemID;
        this.amount = amount;
        this.itemData = itemData;
        this.expire = expire;
    }

    public Commodity(int trader, int price, ItemStack itemStack, long expire) {
        this.uuid = UUID.randomUUID();
        this.trader = trader;
        this.price = price;
        this.expire = expire;

        ItemStream itemStream = ZaphkielAPI.INSTANCE.read(itemStack);
        this.type = CommodityType.match(itemStream.getZaphkielData().getDeep(CommodityType.NBT_NODE).asInt());
        this.itemID = itemStream.getZaphkielItem().getId();
        this.amount = itemStack.getAmount();
        this.itemData = itemStream.getZaphkielData().toJson();
        this.itemStack = itemStack;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public double getUnitPrice() {
        return this.price / this.amount;
    }

    public boolean isExpire() {
        return System.currentTimeMillis() >= expire;
    }

    public ItemStack getItemStack() {
        if (itemStack == null) {
            this.itemStack = this.buildItem();
            return this.itemStack;
        }

        return itemStack;
    }

    public MarketAccount getTraderAccount() {
        UUID uuid = ClientManagerAPI.getUserUUID(this.trader);
        return JustMarket.getUserManager().getAccount(uuid);
    }

    public String getItemName() {
        Item item = ZaphkielAPI.INSTANCE.getRegisteredItem().get(this.getItemID());
        if (item == null) return "";

        String name = item.getName().get("NAME");
        if (type == CommodityType.Clothes) {
            EquipQuality quality = EquipQuality.getQuality(item.buildItemStack(null));
            return name + "&7(" + quality.getName() + ")";
        }

        return name;
    }

    public abstract void saveToDB();

    private ItemStack buildItem() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", this.itemID);
        if (this.itemData != null) obj.add("data", Utils.parse(this.itemData));
        ItemStream itemStream = ZaphkielAPI.INSTANCE.deserialize(obj);

        ItemStack item = itemStream.rebuildToItemStack(null);
        item.setAmount(this.amount);

        return item;
    }
}
