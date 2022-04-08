package net.sakuragame.eternal.justmarket.storage;

import net.sakuragame.eternal.justmarket.core.user.MarketAccount;
import net.sakuragame.eternal.justmarket.core.TradeType;
import net.sakuragame.eternal.justmarket.core.commodity.BuyCommodity;
import net.sakuragame.eternal.justmarket.core.commodity.Commodity;
import net.sakuragame.eternal.justmarket.core.commodity.CommodityType;
import net.sakuragame.eternal.justmarket.core.commodity.SellCommodity;
import net.sakuragame.eternal.justmarket.core.record.TradeRecord;
import net.sakuragame.eternal.justmarket.impl.StorageManager;
import net.sakuragame.serversystems.manage.api.database.DataManager;
import net.sakuragame.serversystems.manage.api.database.DatabaseQuery;
import net.sakuragame.serversystems.manage.client.api.ClientManagerAPI;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.util.*;

public class DatabaseManager implements StorageManager {

    private final DataManager dataManager;

    public DatabaseManager() {
        this.dataManager = ClientManagerAPI.getDataManager();
        this.init();
    }

    @Override
    public void init() {
        for (MarketTables table : MarketTables.values()) {
            table.createTable();
        }
    }

    @Override
    public Map<UUID, Commodity> getSellCommodity() {
        Map<UUID, Commodity> commodities = new HashMap<>();

        try (DatabaseQuery query = dataManager.sqlQueryInTable(
                MarketTables.MARKET_SELL.getTableName()
        )) {
            ResultSet result = query.getResultSet();
            while (result.next()) {
                UUID uuid = UUID.fromString(result.getString("uuid"));
                int trader = result.getInt("trader");
                double price = result.getDouble("price");
                CommodityType type = CommodityType.match(result.getInt("type"));
                String itemID = result.getString("item_id");
                int amount = result.getInt("item_amount");
                String itemData = result.getString("item_data");
                long expire = result.getTimestamp("expire").getTime();

                commodities.put(uuid, new SellCommodity(uuid, trader, price, type, itemID, amount, itemData, expire));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return commodities;
    }

    @Override
    public Map<UUID, Commodity> getBuyCommodity() {
        Map<UUID, Commodity> commodities = new HashMap<>();

        try (DatabaseQuery query = dataManager.sqlQueryInTable(
                MarketTables.MARKET_BUY.getTableName()
        )) {
            ResultSet result = query.getResultSet();
            while (result.next()) {
                UUID uuid = UUID.fromString(result.getString("uuid"));
                int trader = result.getInt("trader");
                double price = result.getDouble("price");
                CommodityType type = CommodityType.match(result.getInt("type"));
                String itemID = result.getString("item_id");
                int amount = result.getInt("item_amount");
                long expire = result.getTimestamp("expire").getTime();

                commodities.put(uuid, new BuyCommodity(uuid, trader, price, type, itemID, amount, expire));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return commodities;
    }

    @Override
    public Map<UUID, TradeRecord> getTradeRecord() {
        Map<UUID, TradeRecord> records = new HashMap<>();

        try (DatabaseQuery query = dataManager.sqlQueryInTable(
                MarketTables.MARKET_TRADE_RECORD.getTableName()
        )) {
            ResultSet result = query.getResultSet();
            while (result.next()) {
                UUID uuid = UUID.fromString(result.getString("uuid"));
                int seller = result.getInt("seller");
                int buyer = result.getInt("buyer");
                String detail = result.getString("detail");

                TradeType type = TradeType.match(result.getInt("type"));
                if (type == null) continue;

                TradeRecord record = TradeRecord.newInstance(uuid, seller, buyer, detail, type);
                if (record == null) continue;

                records.put(uuid, record);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }

    @Override
    public MarketAccount getMarketAccount(UUID uuid) {
        int uid = ClientManagerAPI.getUserID(uuid);
        if (uid == -1) return null;

        return new MarketAccount(uuid,
                MarketTables.MARKET_SELL.getOrderUUIDList(uid),
                MarketTables.MARKET_BUY.getOrderUUIDList(uid)
        );
    }

    @Override
    public List<UUID> getUserTradeRecord(UUID uuid) {
        List<UUID> uuidList = new ArrayList<>();

        int uid = ClientManagerAPI.getUserID(uuid);
        if (uid == -1) return uuidList;

        try (DatabaseQuery query = ClientManagerAPI.getDataManager().sqlQuery(
                MarketTables.MARKET_TRADE_RECORD.getTableName(),
                "seller",
                uid
        )) {
            ResultSet result = query.getResultSet();
            while (result.next()) {
                uuidList.add(UUID.fromString(result.getString("uuid")));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return uuidList;
    }

    @Override
    public void insertSellCommodity(UUID uuid, int trader, double price, CommodityType type, String itemID, int amount, String itemData, long expire) {
        dataManager.executeInsert(
                MarketTables.MARKET_SELL.getTableName(),
                new String[]{"uuid", "trader", "price", "type", "item_id", "item_amount", "item_data", "expire"},
                new Object[]{uuid.toString(), trader, price, type.getID(), itemID, amount, itemData, new Date(expire)}
        );
    }

    @Override
    public int deleteSellCommodity(UUID uuid) {
        return dataManager.executeDelete(
                MarketTables.MARKET_SELL.getTableName(),
                "uuid",
                uuid.toString()
        );
    }

    @Override
    public void insertBuyCommodity(UUID uuid, int trader, double price, CommodityType type, String itemID, int amount, long expire) {
        dataManager.executeInsert(
                MarketTables.MARKET_BUY.getTableName(),
                new String[]{"uuid", "trader", "price", "type", "item_id", "item_amount", "expire"},
                new Object[]{uuid.toString(), trader, price, type.getID(), itemID, amount, new Date(expire)}
        );
    }

    @Override
    public int deleteBuyCommodity(UUID uuid) {
        return dataManager.executeDelete(
                MarketTables.MARKET_BUY.getTableName(),
                "uuid",
                uuid.toString()
        );
    }

    @Override
    public void insertTradeRecord(UUID uuid, int seller, int buyer, TradeType type, String detail) {
        dataManager.executeInsert(
                MarketTables.MARKET_TRADE_RECORD.getTableName(),
                new String[]{"uuid", "seller", "buyer", "type", "detail"},
                new Object[]{uuid.toString(), seller, buyer, type.getID(), detail}
        );
    }

    @Override
    public int deleteTradeRecord(UUID uuid) {
        return dataManager.executeDelete(
                MarketTables.MARKET_TRADE_RECORD.getTableName(),
                "uuid",
                uuid.toString()
        );
    }

    @Override
    public @Nullable Commodity getSellCommodity(UUID uuid) {
        try (DatabaseQuery query = dataManager.sqlQuery(
                MarketTables.MARKET_SELL.getTableName(),
                "uuid",
                uuid.toString()
        )) {
            ResultSet result = query.getResultSet();
            if (result.next()) {
                int trader = result.getInt("trader");
                double price = result.getDouble("price");
                CommodityType type = CommodityType.match(result.getInt("type"));
                String itemID = result.getString("item_id");
                int amount = result.getInt("item_amount");
                String itemData = result.getString("item_data");
                long expire = result.getTimestamp("expire").getTime();

                return new SellCommodity(uuid, trader, price, type, itemID, amount, itemData, expire);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public @Nullable Commodity getBuyCommodity(UUID uuid) {
        try (DatabaseQuery query = dataManager.sqlQuery(
                MarketTables.MARKET_BUY.getTableName(),
                "uuid",
                uuid.toString()
        )) {
            ResultSet result = query.getResultSet();
            if (result.next()) {
                int trader = result.getInt("trader");
                double price = result.getDouble("price");
                CommodityType type = CommodityType.match(result.getInt("type"));
                String itemID = result.getString("item_id");
                int amount = result.getInt("item_amount");
                long expire = result.getTimestamp("expire").getTime();

                return new BuyCommodity(uuid, trader, price, type, itemID, amount, expire);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public @Nullable TradeRecord getTradeRecord(UUID uuid) {
        try (DatabaseQuery query = dataManager.sqlQuery(
                MarketTables.MARKET_TRADE_RECORD.getTableName(),
                "uuid",
                uuid.toString()
        )) {
            ResultSet result = query.getResultSet();
            if (result.next()) {
                int seller = result.getInt("seller");
                int buyer = result.getInt("buyer");
                String detail = result.getString("detail");

                TradeType type = TradeType.match(result.getInt("type"));
                if (type == null) return null;

                return TradeRecord.newInstance(uuid, seller, buyer, detail, type);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
