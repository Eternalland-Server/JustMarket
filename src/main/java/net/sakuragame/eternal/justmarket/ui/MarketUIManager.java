package net.sakuragame.eternal.justmarket.ui;

import net.sakuragame.eternal.justmarket.JustMarket;
import net.sakuragame.eternal.justmarket.core.PageResult;
import net.sakuragame.eternal.justmarket.core.TradeType;
import net.sakuragame.eternal.justmarket.core.commodity.Commodity;
import net.sakuragame.eternal.justmarket.core.commodity.CommodityType;
import net.sakuragame.eternal.justmarket.core.condition.SortCondition;
import net.sakuragame.eternal.justmarket.core.user.MarketAccount;
import net.sakuragame.eternal.justmarket.impl.ScreenManager;
import net.sakuragame.eternal.justmarket.ui.shelf.TradeShelf;
import net.sakuragame.eternal.justmarket.ui.shelf.CommodityShelf;
import net.sakuragame.eternal.justmarket.ui.shelf.ManageShelf;
import net.sakuragame.eternal.justmarket.util.Scheduler;
import net.sakuragame.eternal.justmarket.util.SortUtils;
import com.taylorswiftcn.megumi.uifactory.generate.function.Statements;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketUIManager implements ScreenManager {

    public final static String TRADE_UI_ID = "market_trade";
    public final static String MANAGE_UI_ID = "market_manage";
    public final static String PUT_SELL_UI_ID = "market_sell";
    public final static String PUT_BUY_UI_ID = "market_buy";

    public final static String SELL_SLOT = "market_sell_item";
    public final static String BUY_SLOT = "market_buy_item";

    @Override
    public void sendNotFoundView(Player player, boolean manage) {
        Scheduler.runLater(() -> {
            String label = manage ? "&7&l你还没有上架任何商品" : "&7&l没有找到任何商品...";
            Statements statements = new Statements()
                    .add("func.Component_Set('preview', 'text', '" + label + "');")
                    .add("func.Component_Set('area', 'visible', 0);");

            PacketSender.sendRunFunction(player, manage ? MANAGE_UI_ID : TRADE_UI_ID, statements.build(), false);
        }, 1);
    }

    @Override
    public void sendLoadingView(Player player, boolean manage) {
        Scheduler.runLater(() -> {
            Statements statements = new Statements()
                    .add("func.Component_Set('preview', 'text', '&7&l加载中...');")
                    .add("func.Component_Set('area', 'visible', 0);");

            PacketSender.sendRunFunction(player, manage ? MANAGE_UI_ID : TRADE_UI_ID, statements.build(), false);
        }, 1);
    }

    @Override
    public int sendViewByTradeType(Player player, TradeType tradeType, CommodityType commodityType, SortCondition sort, int page) {
        CommodityCategory category = new CommodityCategory(tradeType);
        category.send(player);

        PacketSender.sendRunFunction(player, "default", "global.market_category = " + tradeType.getID() + ";", false);

        return this.sendViewByCommodity(player, tradeType, commodityType, sort, page);
    }

    @Override
    public int sendViewBySort(Player player, TradeType tradeType, CommodityType commodityType, SortCondition sort, int page) {
        Map<String, String> placeholder = new HashMap<>();

        placeholder.put("market_sort_1", sort.getUPSortTexture());
        placeholder.put("market_sort_2", sort.getTPSortTexture());

        PacketSender.sendSyncPlaceholder(player, placeholder);

        return this.sendViewByCommodity(player, tradeType, commodityType, sort, page);
    }

    @Override
    public int sendViewBySearch(Player player, String search, TradeType tradeType, SortCondition sort, int page) {
        Map<String, String> placeholder = new HashMap<>();

        PageResult result = JustMarket.getTradeManager().getCommodity(search, tradeType, sort, page);
        if (result == null) {
            this.sendNotFoundView(player, false);
            return 0;
        }

        int current = result.getCurrent();
        int totalPage = result.getTotal();

        List<Commodity> commodities = result.getCommodities();
        if (commodities.size() == 0) {
            this.sendNotFoundView(player, false);
            return 0;
        }

        CommodityShelf shelf = new TradeShelf(commodities, tradeType);
        shelf.send(player);

        placeholder.put("market_current_page", current + "");
        placeholder.put("market_max_page", (totalPage + 1) + "");
        placeholder.put("market_sort_1", sort.getUPSortTexture());
        placeholder.put("market_sort_2", sort.getTPSortTexture());

        PacketSender.sendSyncPlaceholder(player, placeholder);
        PacketSender.sendOpenGui(player, TRADE_UI_ID);

        return current;
    }

    @Override
    public int sendViewByCommodity(Player player, TradeType tradeType, CommodityType commodityType, SortCondition sort, int page) {
        Map<String, String> placeholder = new HashMap<>();

        PageResult result = JustMarket.getTradeManager().getCommodity(tradeType, commodityType, sort, page);
        int current = result.getCurrent();
        int totalPage = result.getTotal();

        placeholder.put("market_current_page", current + "");
        placeholder.put("market_max_page", (totalPage + 1) + "");
        placeholder.put("market_sort_1", sort.getUPSortTexture());
        placeholder.put("market_sort_2", sort.getTPSortTexture());

        List<Commodity> commodities = result.getCommodities();
        if (commodities.size() == 0) {
            CommodityShelf.clear(player);
            PacketSender.sendSyncPlaceholder(player, placeholder);
            PacketSender.sendOpenGui(player, TRADE_UI_ID);
            this.sendNotFoundView(player, false);
            return 0;
        }

        CommodityShelf shelf = new TradeShelf(commodities, tradeType);
        shelf.send(player);

        PacketSender.sendSyncPlaceholder(player, placeholder);
        PacketSender.sendOpenGui(player, TRADE_UI_ID);

        return current;
    }

    @Override
    public int sendManageView(Player player, TradeType tradeType) {
        CommodityCategory category = new CommodityCategory(TradeType.Mine);
        category.send(player);

        this.sendManageView(player, tradeType, 0);
        return 0;
    }

    @Override
    public int sendManageView(Player player, TradeType tradeType, int page) {
        MarketAccount account = JustMarket.getUserManager().getAccount(player);

        Map<String, String> placeholder = new HashMap<>();

        PageResult result = SortUtils.getPagePart(
                tradeType == TradeType.Sell ? account.getSellCommodity() : account.getBuyCommodity(),
                page
        );
        int current = result.getCurrent();
        int totalPage = result.getTotal();

        placeholder.put("market_current_page", current + "");
        placeholder.put("market_max_page", (totalPage + 1) + "");

        List<Commodity> commodities = result.getCommodities();
        if (commodities.size() == 0) {
            CommodityShelf.clear(player);
            PacketSender.sendSyncPlaceholder(player, placeholder);
            PacketSender.sendOpenGui(player, MANAGE_UI_ID);
            this.sendNotFoundView(player, true);
            return 0;
        }

        CommodityShelf shelf = new ManageShelf(commodities, tradeType == TradeType.Sell);
        shelf.send(player);

        PacketSender.sendSyncPlaceholder(player, placeholder);
        PacketSender.sendOpenGui(player, MANAGE_UI_ID);

        return current;
    }

    @Override
    public void sendPutSellView(Player player) {
        MarketAccount account = JustMarket.getUserManager().getAccount(player);

        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("market_sell_count", account.getRemainSellPutCount() + "");

        PacketSender.sendSyncPlaceholder(player, placeholder);
        PacketSender.sendOpenGui(player, PUT_SELL_UI_ID);
    }

    @Override
    public void sendPutBuyView(Player player) {
        MarketAccount account = JustMarket.getUserManager().getAccount(player);

        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("market_buy_count", account.getRemainBuyPutCount() + "");

        PacketSender.sendSyncPlaceholder(player, placeholder);
        PacketSender.sendOpenGui(player, PUT_BUY_UI_ID);
    }
}
