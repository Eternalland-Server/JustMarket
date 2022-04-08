package net.sakuragame.eternal.justmarket.storage;

import net.sakuragame.eternal.dragoncore.database.mysql.DatabaseTable;
import net.sakuragame.serversystems.manage.api.database.DatabaseQuery;
import net.sakuragame.serversystems.manage.client.api.ClientManagerAPI;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public enum MarketTables {

    MARKET_SELL(new DatabaseTable("market_sell",
            new String[] {
                    "`uuid` varchar(36) NOT NULL PRIMARY KEY",
                    "`trader` int NOT NULL",
                    "`price` double NOT NULL",
                    "`type` int NOT NULL",
                    "`item_id` varchar(64) NOT NULL",
                    "`item_amount` int NOT NULL",
                    "`item_data` TEXT NOT NULL",
                    "`expire` timestamp NOT NULL",
                    "UNIQUE KEY `seller`(`uuid`,`trader`)"
            })),

    MARKET_BUY(new DatabaseTable("market_buy",
            new String[] {
                    "`uuid` varchar(36) NOT NULL PRIMARY KEY",
                    "`trader` int NOT NULL",
                    "`price` double NOT NULL",
                    "`type` int NOT NULL",
                    "`item_id` varchar(64) NOT NULL",
                    "`item_amount` int NOT NULL",
                    "`expire` timestamp NOT NULL",
                    "UNIQUE KEY `buyer`(`uuid`,`trader`)"
            })),

    MARKET_TRADE_RECORD(new DatabaseTable("market_trade_record",
            new String[]{
                    "`uuid` varchar(36) NOT NULL PRIMARY KEY",
                    "`seller` int NOT NULL",
                    "`buyer` int NOT NULL",
                    "`type` int NOT NULL",
                    "`detail` varchar(128) NOT NULL",
                    "UNIQUE KEY `record`(`uuid`, `seller`)"
            }));

    private final DatabaseTable table;

    MarketTables(DatabaseTable table) {
        this.table = table;
    }

    public String getTableName() {
        return table.getTableName();
    }

    public String[] getColumns() {
        return table.getTableColumns();
    }

    public DatabaseTable getTable() {
        return table;
    }

    public void createTable() {
        table.createTable();
    }

    public List<UUID> getOrderUUIDList(int uid) {
        List<UUID> orders = new ArrayList<>();
        try (DatabaseQuery query = ClientManagerAPI.getDataManager().sqlQuery(
                this.getTableName(),
                "trader",
                uid
        )) {
            ResultSet result = query.getResultSet();
            while (result.next()) {
                orders.add(UUID.fromString(result.getString("uuid")));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }
}
