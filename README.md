# JustMarket

> 市场系统

![img](./.img/0.png)

#### API
```yaml
`JustMarketAPI`

# 上架出售商品
shelveSell(Player player, ItemStack item, double price, int day);
# 上架求购商品
shelveBuy(Player player, ItemStack item, double price, int day);
# 下架出售商品
unshelveSell(Player player, UUID commodityUUID);
# 下架求购商品
unshelveBuy(Player player, UUID commodityUUID);
```

#### Event
```yaml
# 商品上架事件
MarketShelveEvent
# 商品下架事件
MarketUnshelveEvent
# 商品交易完成事件
MarketTradeEvent
# 商品过期自动下架事件
MarketCommodityExpiredEvent
```