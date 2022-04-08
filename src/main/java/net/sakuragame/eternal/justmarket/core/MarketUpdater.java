package net.sakuragame.eternal.justmarket.core;

import net.sakuragame.eternal.justmarket.JustMarket;
import net.sakuragame.eternal.justmarket.impl.DataUpdater;
import net.sakuragame.serversystems.manage.api.redis.RedisMessageListener;
import net.sakuragame.serversystems.manage.client.api.ClientManagerAPI;

import java.util.UUID;

public class MarketUpdater extends RedisMessageListener implements DataUpdater {

    private final JustMarket plugin;

    private final String COMMODITY_CHANNEL = "commodity_channel";
    private final String TRADE_RECORD_CHANNEL = "trade_record_channel";

    public MarketUpdater(JustMarket plugin) {
        super(false, plugin.getName());
        this.plugin = plugin;

        ClientManagerAPI.getRedisManager().subscribe(plugin.getName());
        ClientManagerAPI.getRedisManager().registerListener(this);
    }

    @Override
    public void onMessage(String serviceName, String sourceServer, String channel, String[] messages) {
        if (sourceServer.equals(ClientManagerAPI.getServerID())) return;
        switch (channel) {
            case COMMODITY_CHANNEL:
                this.handleCommodityChannel(messages);
                return;
            case TRADE_RECORD_CHANNEL:
        }
    }

    private void handleCommodityChannel(String[] messages) {
        String operate = messages[0];
        UUID uuid = UUID.fromString(messages[1]);
        TradeType type = TradeType.match(Integer.parseInt(messages[2]));

        if (operate.equals("put")) {
            JustMarket.getTradeManager().loadCommodity(uuid, type);
            return;
        }

        if (operate.equals("del")) {
            JustMarket.getTradeManager().removeCommodity(uuid, type);
        }
    }

    private void handleTradeRecordChannel(String[] messages) {
        String operate = messages[0];
        UUID uuid = UUID.fromString(messages[1]);

        if (operate.equals("create")) {
            JustMarket.getTradeManager().loadTradeRecord(uuid);
            return;
        }

        if (operate.equals("remove")) {
            JustMarket.getTradeManager().removeTradeRecord(uuid);
        }
    }

    @Override
    public void putCommodity(UUID uuid, TradeType type) {
        ClientManagerAPI.getRedisManager().publishAsync(
                plugin.getName(),
                COMMODITY_CHANNEL,
                "put",
                uuid.toString(),
                type.getID() + ""
        );
    }

    @Override
    public void delCommodity(UUID uuid, TradeType type) {
        ClientManagerAPI.getRedisManager().publishAsync(
                plugin.getName(),
                COMMODITY_CHANNEL,
                "del",
                uuid.toString(),
                type.getID() + ""
        );
    }

    @Override
    public void createTradeRecord(UUID uuid) {
        ClientManagerAPI.getRedisManager().publishAsync(
                plugin.getName(),
                TRADE_RECORD_CHANNEL,
                "create",
                uuid.toString()
        );
    }

    @Override
    public void removeTradeRecord(UUID uuid) {
        ClientManagerAPI.getRedisManager().publishAsync(
                plugin.getName(),
                TRADE_RECORD_CHANNEL,
                "remove",
                uuid.toString()
        );
    }
}
