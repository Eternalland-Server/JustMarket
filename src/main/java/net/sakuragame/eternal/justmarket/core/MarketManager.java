package net.sakuragame.eternal.justmarket.core;

import com.taylorswiftcn.megumi.uifactory.generate.function.Statements;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justmarket.JustMarket;
import net.sakuragame.eternal.justmarket.core.commodity.BuyCommodity;
import net.sakuragame.eternal.justmarket.core.commodity.Commodity;
import net.sakuragame.eternal.justmarket.core.commodity.CommodityType;
import net.sakuragame.eternal.justmarket.core.commodity.SellCommodity;
import net.sakuragame.eternal.justmarket.core.condition.SortCondition;
import net.sakuragame.eternal.justmarket.core.record.BuyTradeRecord;
import net.sakuragame.eternal.justmarket.core.record.SellTradeRecord;
import net.sakuragame.eternal.justmarket.core.record.TradeRecord;
import net.sakuragame.eternal.justmarket.core.user.MarketAccount;
import net.sakuragame.eternal.justmarket.file.sub.ConfigFile;
import net.sakuragame.eternal.justmarket.impl.TradeManager;
import net.sakuragame.eternal.justmarket.ui.MarketUIManager;
import net.sakuragame.eternal.justmarket.util.SortUtils;
import net.sakuragame.eternal.justmarket.util.Utils;
import ink.ptms.zaphkiel.ZaphkielAPI;
import ink.ptms.zaphkiel.api.Item;
import ink.ptms.zaphkiel.api.ItemStream;
import net.sakuragame.eternal.gemseconomy.api.GemsEconomyAPI;
import net.sakuragame.serversystems.manage.client.api.ClientManagerAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class MarketManager implements TradeManager {

    private final Map<UUID, Commodity> sellMap;
    private final Map<UUID, Commodity> buyMap;
    private final Map<UUID, TradeRecord> recordMap;

    private final Map<String, String> searchMap;

    private final List<UUID> sellLock;
    private final List<UUID> buyLock;

    public MarketManager() {
        this.sellMap = JustMarket.getStorageManager().getSellCommodity();
        this.buyMap = JustMarket.getStorageManager().getBuyCommodity();
        this.recordMap = JustMarket.getStorageManager().getTradeRecord();
        this.searchMap = new HashMap<>();
        this.sellLock = new ArrayList<>();
        this.buyLock = new ArrayList<>();
    }

    @Override
    public Commodity getCommodity(UUID uuid, TradeType tradeType) {
        return tradeType == TradeType.Sell ? this.sellMap.get(uuid) : this.buyMap.get(uuid);
    }

    @Override
    public List<Commodity> getCommodity(List<UUID> uuids, TradeType tradeType) {
        List<Commodity> commodities = new ArrayList<>();
        uuids.forEach(key -> commodities.add(this.getCommodity(key, tradeType)));

        return commodities;
    }

    @Override
    public List<Commodity> getCommodity(TradeType tradeType) {
        return new ArrayList<>(tradeType == TradeType.Sell ? sellMap.values() : buyMap.values())
                .stream().filter(elm -> !elm.isExpire())
                .collect(Collectors.toList());
    }

    @Override
    public PageResult getCommodity(TradeType tradeType, SortCondition sort, int page) {
        List<Commodity> commodities = this.getCommodity(tradeType);
        return getCommodity(commodities, sort, page);
    }

    @Override
    @Nullable
    public PageResult getCommodity(String search, TradeType tradeType, SortCondition sort, int page) {
        List<String> scope = this.getSimilarID(search);
        if (scope.size() == 0) return null;

        List<Commodity> commodities = this.getCommodity(tradeType)
                .stream().filter(elm -> scope.contains(elm.getItemID()))
                .collect(Collectors.toList());

        return this.getCommodity(commodities, sort, page);
    }

    private List<String> getSimilarID(String name) {
        List<String> list = new ArrayList<>();
        this.searchMap.entrySet().stream()
                .filter(elm -> elm.getKey().contains(name))
                .forEach(elm -> list.add(elm.getValue()));

        return list;
    }

    @Override
    public PageResult getCommodity(TradeType tradeType, CommodityType commodityType, SortCondition sort, int page) {
        List<Commodity> commodities = this.getCommodity(tradeType);

        if (commodityType == CommodityType.ALL) {
            return this.getCommodity(commodities, sort, page);
        }

        commodities = commodities
                .stream().filter(elm -> elm.getType() == commodityType)
                .collect(Collectors.toList());

        return this.getCommodity(commodities, sort, page);
    }

    private PageResult getCommodity(List<Commodity> commodities, SortCondition sort, int page) {
        switch (sort) {
            case PriceAsc:
                return SortUtils.orderByPrice(commodities, page, true);
            case PriceDesc:
                return SortUtils.orderByPrice(commodities, page);
            case UnitPriceAsc:
                return SortUtils.orderByUnitPrice(commodities, page, true);
            case UnitPriceDesc:
                return SortUtils.orderByUnitPrice(commodities, page);
            default:
                return SortUtils.getPagePart(commodities, page);
        }
    }

    @Override
    public void loadCommodity(UUID uuid, TradeType tradeType) {
        switch (tradeType) {
            case Sell:
                Commodity sell = JustMarket.getStorageManager().getSellCommodity(uuid);
                if (sell == null) return;
                this.sellMap.put(uuid, sell);
                return;
            case Buy:
                Commodity buy = JustMarket.getStorageManager().getBuyCommodity(uuid);
                if (buy == null) return;
                this.buyMap.put(uuid, buy);
        }
    }

    @Override
    public Commodity removeCommodity(UUID uuid, TradeType tradeType) {
        switch (tradeType) {
            case Sell:
                return this.sellMap.remove(uuid);
            case Buy:
                return this.buyMap.remove(uuid);
        }
        return null;
    }

    @Override
    public void loadTradeRecord(UUID uuid) {
        this.recordMap.put(uuid, JustMarket.getStorageManager().getTradeRecord(uuid));
    }

    @Override
    public void removeTradeRecord(UUID uuid) {
        this.recordMap.remove(uuid);
    }

    @Override
    public TradeRecord getTradeRecord(UUID uuid) {
        return this.recordMap.get(uuid);
    }

    @Override
    public boolean tradeSellCommodity(Player player, UUID commodityUUID) {
        UUID uuid = player.getUniqueId();
        Commodity commodity = this.sellMap.get(commodityUUID);

        if (commodity == null) {
            player.sendMessage(ConfigFile.prefix + "该商品已经被其他人购买了");
            return false;
        }

        double balance = GemsEconomyAPI.getBalance(uuid);
        if (balance < commodity.getPrice()) {
            player.sendMessage(ConfigFile.prefix + "你没有足够的金币购买");
            return false;
        }

        if (Utils.getEmptySlotCount(player) < 1) {
            player.sendMessage(ConfigFile.prefix + "购买失败！你的背包没有空闲槽位");
            return false;
        }

        int i = JustMarket.getStorageManager().deleteSellCommodity(commodityUUID);
        this.sellMap.remove(commodityUUID);
        if (i == 0) {
            player.sendMessage(ConfigFile.prefix + "该商品已经被其他人购买了");
            return false;
        }
        JustMarket.getUpdater().delCommodity(commodityUUID, TradeType.Sell);

        MarketAccount account = commodity.getTraderAccount();
        if (account != null) {
            account.removeSellCommodity(commodityUUID);
        }

        GemsEconomyAPI.withdraw(uuid, commodity.getPrice());
        player.getInventory().addItem(commodity.getItemStack());
        player.sendMessage(ConfigFile.prefix + "购买成功");

        this.sendTradeSuccessStyle(player, commodityUUID);

        int buyer = ClientManagerAPI.getUserID(uuid);
        SellTradeRecord record = new SellTradeRecord(
                commodity.getTrader(), buyer,
                commodity.getItemID(), commodity.getAmount(), commodity.getPrice()
        );
        if (!record.settlement()) {
            record.saveToDB();
            this.recordMap.put(record.getUUID(), record);
            JustMarket.getUpdater().createTradeRecord(record.getUUID());
        }

        return true;
    }

    @Override
    public boolean tradeBuyCommodity(Player player, UUID commodityUUID) {
        UUID uuid = player.getUniqueId();
        Commodity commodity = this.buyMap.get(commodityUUID);

        if (commodity == null) {
            player.sendMessage(ConfigFile.prefix + "该求购已经被其他人抢先一步了");
            return false;
        }

        if (!Utils.checkItem(player, commodity.getItemID(), commodity.getAmount())) {
            player.sendMessage(ConfigFile.prefix + "出售失败，你背包内没有足够数量的物品");
            return false;
        }
        
        int i = JustMarket.getStorageManager().deleteBuyCommodity(commodityUUID);
        this.buyMap.remove(commodityUUID);
        if (i == 0) {
            player.sendMessage(ConfigFile.prefix + "该求购已经被其他人抢先一步了");
            return false;
        }
        JustMarket.getUpdater().delCommodity(commodityUUID, TradeType.Buy);

        MarketAccount account = commodity.getTraderAccount();
        if (account != null) {
            account.removeBuyCommodity(commodityUUID);
        }
        
        Utils.consumeItem(player, commodity.getItemID(), commodity.getAmount());
        GemsEconomyAPI.deposit(uuid, commodity.getPrice());
        player.sendMessage(ConfigFile.prefix + "出售成功");

        this.sendTradeSuccessStyle(player, commodityUUID);
        
        int buyer = ClientManagerAPI.getUserID(uuid);
        BuyTradeRecord record = new BuyTradeRecord(
                commodity.getTrader(), buyer,
                commodity.getItemID(), commodity.getAmount()
        );
        if (!record.settlement()) {
            record.saveToDB();
            this.recordMap.put(record.getUUID(), record);
            JustMarket.getUpdater().createTradeRecord(record.getUUID());
        }

        return true;
    }

    private void sendTradeSuccessStyle(Player player, UUID uuid) {
        String code = Utils.getUUIDPart(uuid);
        PacketSender.sendRunFunction(player, MarketUIManager.TRADE_UI_ID, new Statements()
                        .add("func.Component_Set('c_" + code + "', 'texture', 'ui/common/text_background.png');")
                        .add("func.Component_Set('c_" + code + "', 'actions.click_left', '');")
                        .add("func.Component_Set('c_" + code + "', 'actions.release_left', '');")
                        .add("func.Component_Set('c_" + code + "', 'text', '&6&l交易成功');")
                        .build(),
                false
        );
    }

    @Override
    public boolean putSellCommodity(Player player, ItemStack item, double price, int day) {
        UUID uuid = player.getUniqueId();
        int uid = ClientManagerAPI.getUserID(uuid);
        if (uid == -1) return false;

        MarketAccount account = JustMarket.getUserManager().getAccount(uuid);

        ItemStream itemStream = ZaphkielAPI.INSTANCE.read(item);
        String itemID = itemStream.getZaphkielItem().getId();
        int amount = item.getAmount();
        String itemData = itemStream.getZaphkielData().toJson();
        CommodityType type = Utils.getCommodityType(itemStream);
        long expire = Utils.getExpire(day);

        SellCommodity commodity = new SellCommodity(uid, price, type, itemID, amount, itemData, expire);
        commodity.saveToDB();
        UUID commodityUUID = commodity.getUUID();

        account.addSell(commodityUUID);
        this.sellMap.put(commodityUUID, commodity);
        JustMarket.getUpdater().putCommodity(commodityUUID, TradeType.Sell);
        return true;
    }

    @Override
    public boolean putBuyCommodity(Player player, ItemStack item, double price, int day) {
        UUID uuid = player.getUniqueId();
        int uid = ClientManagerAPI.getUserID(uuid);
        if (uid == -1) return false;

        MarketAccount account = JustMarket.getUserManager().getAccount(uuid);

        ItemStream itemStream = ZaphkielAPI.INSTANCE.read(item);
        String itemID = itemStream.getZaphkielItem().getId();
        int amount = item.getAmount();
        CommodityType type = Utils.getCommodityType(itemStream);
        long expire = Utils.getExpire(day);

        BuyCommodity commodity = new BuyCommodity(uid, price, type, itemID, amount, expire);
        commodity.saveToDB();
        UUID commodityUUID = commodity.getUUID();

        account.addBuy(commodityUUID);
        this.buyMap.put(commodityUUID, commodity);
        JustMarket.getUpdater().putCommodity(commodityUUID, TradeType.Buy);
        return true;
    }

    @Override
    public void addSellLock(UUID uuid) {
        this.sellLock.add(uuid);
    }

    @Override
    public void addBuyLock(UUID uuid) {
        this.buyLock.add(uuid);
    }

    @Override
    public void delSellLock(UUID uuid) {
        this.sellLock.remove(uuid);
    }

    @Override
    public void delBuyLock(UUID uuid) {
        this.buyLock.remove(uuid);
    }

    @Override
    public boolean isSellLock(UUID uuid) {
        return this.sellLock.contains(uuid);
    }

    @Override
    public boolean isBuyLock(UUID uuid) {
        return this.buyLock.contains(uuid);
    }

    @Override
    public void reloadSearchMap() {
        this.searchMap.clear();

        for (Item item : ZaphkielAPI.INSTANCE.getRegisteredItem().values()) {
            int typeID = item.getData().getInt(CommodityType.NBT_NODE, -1);
            CommodityType type = CommodityType.match(typeID);
            if (type == CommodityType.ALL) continue;

            this.searchMap.put(ChatColor.stripColor(item.getName().get("NAME")), item.getId());
        }
    }
}
