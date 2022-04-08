package net.sakuragame.eternal.justmarket.ui;

import net.sakuragame.eternal.justmarket.JustMarket;
import net.sakuragame.eternal.justmarket.core.TradeType;
import net.sakuragame.eternal.justmarket.core.commodity.CommodityType;
import net.sakuragame.eternal.justmarket.core.condition.SortCondition;
import net.sakuragame.eternal.justmarket.impl.ScreenView;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
public class UserView implements ScreenView {

    private UUID UUID;
    private TradeType topCategory;
    private CommodityType leftCategory;
    private SortCondition sort;
    private int page;

    private String searchCache;

    public UserView(UUID UUID) {
        this.UUID = UUID;
        this.topCategory = TradeType.Sell;
        this.leftCategory = CommodityType.ALL;
        this.sort = SortCondition.Default;
        this.page = 0;
        this.searchCache = null;
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(this.UUID);
    }

    @Override
    public void open() {
        this.open(TradeType.Sell);
    }

    @Override
    public void open(TradeType tradeType) {
        this.topCategory = tradeType;
        this.leftCategory = CommodityType.ALL;
        this.page = 0;
        this.searchCache = null;

        JustMarket.getScreenManager().sendViewByTradeType(
                this.getBukkitPlayer(),
                this.topCategory,
                this.leftCategory,
                this.sort,
                this.page
        );
    }

    @Override
    public void open(CommodityType commodityType) {
        this.leftCategory = commodityType;

        switch (commodityType) {
            case Mine_Buy:
                this.openBuyManage(0);
                return;
            case Mine_Sell:
                this.openSellManage(0);
                return;
            case Put_Buy:
                this.openPutBuy();
                return;
            case Put_Sell:
                this.openPutSell();
                return;
        }

        this.searchCache = null;
        this.open(0);
    }

    @Override
    public void open(SortCondition sort) {
        this.sort = sort;
        this.page = 0;

        if (this.searchCache == null) {
            JustMarket.getScreenManager().sendViewBySort(
                    this.getBukkitPlayer(),
                    this.topCategory,
                    this.leftCategory,
                    this.sort,
                    this.page
            );
        }
        else {
            this.open(this.searchCache, this.page);
        }
    }

    @Override
    public void open(int page) {
        if (this.searchCache != null) {
            this.open(this.searchCache, page);
            return;
        }

        switch (this.leftCategory) {
            case Mine_Buy:
                this.openBuyManage(page);
                return;
            case Mine_Sell:
                this.openSellManage(page);
                return;
            case Put_Buy:
                this.openPutBuy();
                return;
            case Put_Sell:
                this.openPutSell();
                return;
        }

        this.page = JustMarket.getScreenManager().sendViewByCommodity(
                this.getBukkitPlayer(),
                this.topCategory,
                this.leftCategory,
                this.sort,
                page
        );
    }

    @Override
    public void open(String search) {
        this.open(search, 0);
    }

    @Override
    public void open(String search, int page) {
        this.searchCache = search;

        this.page = JustMarket.getScreenManager().sendViewBySearch(
                this.getBukkitPlayer(),
                this.searchCache,
                this.topCategory,
                this.sort,
                page
        );
    }

    @Override
    public void openManage() {
        this.topCategory = TradeType.Mine;
        this.page = JustMarket.getScreenManager().sendManageView(
                this.getBukkitPlayer(),
                TradeType.Sell
        );
    }

    @Override
    public void openSellManage(int page) {
        this.page = JustMarket.getScreenManager().sendManageView(
                this.getBukkitPlayer(),
                TradeType.Sell,
                page
        );
    }

    @Override
    public void openBuyManage(int page) {
        this.page = JustMarket.getScreenManager().sendManageView(
                this.getBukkitPlayer(),
                TradeType.Buy,
                page
        );
    }

    @Override
    public void openPutSell() {
        JustMarket.getScreenManager().sendPutSellView(this.getBukkitPlayer());
    }

    @Override
    public void openPutBuy() {
        JustMarket.getScreenManager().sendPutBuyView(this.getBukkitPlayer());
    }
}
