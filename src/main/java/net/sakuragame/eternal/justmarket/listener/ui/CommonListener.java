package net.sakuragame.eternal.justmarket.listener.ui;

import net.sakuragame.eternal.gemseconomy.api.GemsEconomyAPI;
import net.sakuragame.eternal.justmarket.JustMarket;
import net.sakuragame.eternal.justmarket.core.TradeType;
import net.sakuragame.eternal.justmarket.core.commodity.Commodity;
import net.sakuragame.eternal.justmarket.core.commodity.CommodityType;
import net.sakuragame.eternal.justmarket.core.condition.SortCondition;
import net.sakuragame.eternal.justmarket.core.user.MarketAccount;
import net.sakuragame.eternal.justmarket.file.sub.ConfigFile;
import net.sakuragame.eternal.justmarket.ui.MarketUIManager;
import net.sakuragame.eternal.justmarket.ui.OperateCode;
import net.sakuragame.eternal.justmarket.ui.SlotCache;
import net.sakuragame.eternal.justmarket.ui.UserView;
import net.sakuragame.eternal.justmarket.util.Scheduler;
import com.taylorswiftcn.justwei.util.MegumiUtil;
import com.taylorswiftcn.megumi.uifactory.event.comp.UIFCompSubmitEvent;
import com.taylorswiftcn.megumi.uifactory.generate.function.Statements;
import com.taylorswiftcn.megumi.uifactory.generate.function.SubmitParams;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justmarket.util.Utils;
import net.sakuragame.eternal.justmessage.api.MessageAPI;
import net.sakuragame.serversystems.manage.client.api.ClientManagerAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommonListener implements Listener {

    @EventHandler
    public void onSubmit(UIFCompSubmitEvent e) {
        Player player = e.getPlayer();
        String screenID = e.getScreenID();

        if (!(screenID.equals(MarketUIManager.MANAGE_UI_ID) ||
                screenID.equals(MarketUIManager.TRADE_UI_ID) ||
                screenID.equals(MarketUIManager.PUT_SELL_UI_ID) ||
                screenID.equals(MarketUIManager.PUT_BUY_UI_ID))) {
            return;
        }

        String compID = e.getCompID();
        SubmitParams params = e.getParams();
        OperateCode code = OperateCode.match(params.getParamI(0));
        if (code == null) return;

        switch (code) {
            case TopCategory:
                this.onHandleTopCategory(player, compID);
                return;
            case LeftCategory:
                this.onHandleLeftCategory(player, params);
                return;
            case SellTrade:
                this.onHandleSellTrade(player, params);
                return;
            case BuyTrade:
                this.onHandleBuyTrade(player, params);
                return;
            case UpPage:
                this.onHandleUpPage(player);
                return;
            case NextPage:
                this.onHandleNextPage(player);
                return;
            case UnitPriceSort:
                this.onHandleUnitPriceSort(player);
                return;
            case TotalPriceSort:
                this.onHandleTotalPriceSort(player);
                return;
            case PutSell:
                this.onHandlePutSell(player, params);
                return;
            case PutBuy:
                this.onHandlePutBuy(player, params);
                return;
            case unShelveSell:
                this.onHandleUnShelveSell(player, params);
                return;
            case unShelveBuy:
                this.onHandleUnShelveBuy(player, params);
                return;
            case Search:
                this.onHandleSearch(player, params);
        }
    }

    public void onHandleTopCategory(Player player, String compID) {
        UserView view = JustMarket.getUserManager().getCache(player);
        switch (compID) {
            case "sell":
                view.open(TradeType.Sell);
                return;
            case "buy":
                view.open(TradeType.Buy);
                return;
            case "mine":
                view.openManage();
        }
    }

    public void onHandleLeftCategory(Player player, SubmitParams params) {
        UserView view = JustMarket.getUserManager().getCache(player);
        CommodityType type = CommodityType.match(params.getParamI(1));
        view.open(type);
    }

    public void onHandleSellTrade(Player player, SubmitParams params) {
        int uid = ClientManagerAPI.getUserID(player.getUniqueId());
        UUID uuid = UUID.fromString(params.getParam(1));

        Commodity commodity = JustMarket.getTradeManager().getCommodity(uuid, TradeType.Sell);
        if (commodity.getTrader() == uid) {
            MessageAPI.sendActionTip(player, "&a&l你不能交易自己的商品");
            return;
        }

        if (JustMarket.getTradeManager().isSellLock(uuid)) {
            MessageAPI.sendActionTip(player, "&a&l其他人正在交易这个商品，请稍后再试");
            return;
        }

        JustMarket.getTradeManager().addSellLock(uuid);
        Scheduler.runAsync(() -> {
            JustMarket.getTradeManager().tradeSellCommodity(player, uuid);
            JustMarket.getTradeManager().delSellLock(uuid);
        });
    }

    public void onHandleBuyTrade(Player player, SubmitParams params) {
        int uid = ClientManagerAPI.getUserID(player.getUniqueId());
        UUID uuid = UUID.fromString(params.getParam(1));

        Commodity commodity = JustMarket.getTradeManager().getCommodity(uuid, TradeType.Sell);
        if (commodity.getTrader() == uid) {
            MessageAPI.sendActionTip(player, "&a&l你不能交易自己的商品");
            return;
        }

        if (JustMarket.getTradeManager().isBuyLock(uuid)) {
            MessageAPI.sendActionTip(player, "&a&l其他人正在交易这个商品，请稍后再试");
            return;
        }

        JustMarket.getTradeManager().addBuyLock(uuid);
        Scheduler.runAsync(() -> {
            JustMarket.getTradeManager().tradeBuyCommodity(player, uuid);
            JustMarket.getTradeManager().delBuyLock(uuid);
        });
    }

    public void onHandleUnitPriceSort(Player player) {
        UserView view = JustMarket.getUserManager().getCache(player);
        SortCondition sort = view.getSort();
        switch (sort) {
            case UnitPriceDesc:
                view.open(SortCondition.UnitPriceAsc);
                return;
            case UnitPriceAsc:
                view.open(SortCondition.Default);
                return;
            default:
            case Default:
                view.open(SortCondition.UnitPriceDesc);
        }
    }

    public void onHandleTotalPriceSort(Player player) {
        UserView view = JustMarket.getUserManager().getCache(player);
        SortCondition sort = view.getSort();
        switch (sort) {
            case PriceDesc:
                view.open(SortCondition.PriceAsc);
                return;
            case PriceAsc:
                view.open(SortCondition.Default);
                return;
            default:
            case Default:
                view.open(SortCondition.PriceDesc);
        }
    }

    public void onHandleUpPage(Player player) {
        UserView view = JustMarket.getUserManager().getCache(player);
        int page = view.getPage();
        if (page == 0) return;
        view.open(page - 1);
    }

    public void onHandleNextPage(Player player) {
        UserView view = JustMarket.getUserManager().getCache(player);
        int page = view.getPage();
        view.open(page + 1);
    }

    public void onHandleSearch(Player player, SubmitParams params) {
        UserView view = JustMarket.getUserManager().getCache(player);
        String search = params.getParam(1);
        view.open(search);
    }

    public void onHandlePutSell(Player player, SubmitParams params) {
        MarketAccount account = JustMarket.getUserManager().getAccount(player);
        int count = account.getRemainSellPutCount();
        if (count == 0) {
            player.sendMessage(ConfigFile.prefix + "你不能够再上架商品了");
            return;
        }

        ItemStack item = SlotCache.getSlot(player, MarketUIManager.SELL_SLOT);
        if (item == null) {
            MessageAPI.sendActionTip(player, "&c&l请先放入需要出售的物品");
            return;
        }

        String choose = params.getParam(1);
        String s = params.getParam(2);
        int day = ConfigFile.getPutDayOfSell().getUserCount(player, choose);

        if (!player.hasPermission(choose)) {
            MessageAPI.sendActionTip(player, "&c&l你无法上架 &a&l" + day + " &c&l天");
            return;
        }

        if (!MegumiUtil.isNumber(s)) {
            MessageAPI.sendActionTip(player, "&c&l价格只支持整数单位");
            return;
        }

        int price = Integer.parseInt(s);

        JustMarket.getTradeManager().putSellCommodity(player, item, price, day);
        SlotCache.removeSlot(player, MarketUIManager.SELL_SLOT);
        PacketSender.putClientSlotItem(player, MarketUIManager.SELL_SLOT, new ItemStack(Material.AIR));

        PacketSender.sendRunFunction(player, MarketUIManager.PUT_SELL_UI_ID,
                new Statements()
                        .add("func.Component_Set('name', 'texts', '&9商品名称');")
                        .add("func.Component_Set('i_input1', 'text', '');")
                        .build(),
                false
        );

        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("market_buy_count", count + "");
        PacketSender.sendSyncPlaceholder(player, placeholder);

        player.sendMessage(ConfigFile.prefix + "上架出售商品成功");
    }

    public void onHandlePutBuy(Player player, SubmitParams params) {
        MarketAccount account = JustMarket.getUserManager().getAccount(player);
        int count = account.getRemainBuyPutCount();
        if (count == 0) {
            player.sendMessage(ConfigFile.prefix + "你不能够再上架商品了");
            return;
        }

        ItemStack item = SlotCache.getSlot(player, MarketUIManager.BUY_SLOT);
        if (item == null) {
            MessageAPI.sendActionTip(player, "&c&l请先放入想要求购的物品");
            return;
        }

        String choose = params.getParam(1);
        String s1 = params.getParam(2);
        String s2 = params.getParam(3);
        int day = ConfigFile.getPutDayOfSell().getUserCount(player, choose);

        if (!player.hasPermission(choose)) {
            MessageAPI.sendActionTip(player, "&c&l你无法上架 &a&l" + day + " &c&l天");
            return;
        }

        if (!MegumiUtil.isNumber(s1)) {
            MessageAPI.sendActionTip(player, "&c&l价格只支持整数单位");
            return;
        }

        if (!MegumiUtil.isNumber(s2)) {
            MessageAPI.sendActionTip(player, "&c&l数量只支持整数单位");
            return;
        }

        int price = Integer.parseInt(s1);
        int serviceCharge = (int) (price * ConfigFile.getBuyServiceCharge());
        double balance = GemsEconomyAPI.getBalance(player.getUniqueId());
        if (balance < price + serviceCharge) {
            player.sendMessage(ConfigFile.prefix + "你需要 §a" + price + "(+" + serviceCharge + "手续费) §7金币才能上架求购");
            return;
        }

        int amount = Integer.parseInt(s2);

        item = item.clone();
        item.setAmount(amount);

        JustMarket.getTradeManager().putBuyCommodity(player, item, price, day);

        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("market_buy_count", count + "");
        PacketSender.sendSyncPlaceholder(player, placeholder);

        player.sendMessage(ConfigFile.prefix + "上架求购商品成功,已扣除 §a" + price + "(+" + serviceCharge + "手续费) §7金币");
    }

    public void onHandleUnShelveSell(Player player, SubmitParams params) {
        UUID uuid = UUID.fromString(params.getParam(1));

        MarketAccount account = JustMarket.getUserManager().getAccount(player);
        if (account.unShelveSellCommodity(uuid)) {
            player.sendMessage(ConfigFile.prefix + "下架成功");
            this.sendUnshelveSuccessStyle(player, uuid);
        }
        else {
            player.sendMessage(ConfigFile.prefix + "下架失败，请稍后再试");
        }
    }

    public void onHandleUnShelveBuy(Player player, SubmitParams params) {
        UUID uuid = UUID.fromString(params.getParam(1));

        MarketAccount account = JustMarket.getUserManager().getAccount(player);
        if (account.unShelveBuyCommodity(uuid)) {
            player.sendMessage(ConfigFile.prefix + "下架成功");
            this.sendUnshelveSuccessStyle(player, uuid);
        }
        else {
            player.sendMessage(ConfigFile.prefix + "下架失败，请稍后再试");
        }
    }

    private void sendUnshelveSuccessStyle(Player player, UUID uuid) {
        String code = Utils.getUUIDPart(uuid);
        PacketSender.sendRunFunction(player, MarketUIManager.MANAGE_UI_ID, new Statements()
                        .add("func.Component_Set('c_" + code + "', 'actions.click_left', '');")
                        .add("func.Component_Set('c_" + code + "', 'actions.release_left', '');")
                        .add("func.Component_Set('c_" + code + "', 'text', '&6&l下架成功');")
                        .build(),
                false
        );
    }
}
