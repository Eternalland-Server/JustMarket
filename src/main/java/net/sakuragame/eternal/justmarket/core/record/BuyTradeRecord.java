package net.sakuragame.eternal.justmarket.core.record;

import com.alibaba.fastjson.JSON;
import net.sakuragame.eternal.justmarket.JustMarket;
import net.sakuragame.eternal.justmarket.core.TradeType;
import net.sakuragame.eternal.justmarket.util.MailNotify;
import net.sakuragame.eternal.mail.api.model.AnnexData;
import net.sakuragame.serversystems.manage.client.api.ClientManagerAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BuyTradeRecord extends TradeRecord {

    private final String itemID;
    private final int amount;

    public BuyTradeRecord(int seller, int buyer, String itemID, int amount) {
        super(seller, buyer);
        this.itemID = itemID;
        this.amount = amount;
    }

    public BuyTradeRecord(UUID uuid, int seller, int buyer, String detail) {
        super(uuid, seller, buyer);
        List<String> args = JSON.parseArray(detail, String.class);
        this.itemID = args.get(0);
        this.amount = Integer.parseInt(args.get(1));
    }

    @Override
    public String geneDetail() {
        return JSON.toJSONString(Arrays.asList(itemID, String.valueOf(amount)));
    }

    @Override
    public boolean settlement() {
        Player player = Bukkit.getPlayer(ClientManagerAPI.getUserUUID(this.getSeller()));
        if (player == null) return false;

        MailNotify.sendBuySuccess(player, new AnnexData(this.itemID, this.amount));
        return true;
    }

    @Override
    public void saveToDB() {
        JustMarket.getStorageManager().insertTradeRecord(
                this.getUUID(),
                this.getSeller(),
                this.getBuyer(),
                TradeType.Buy,
                this.geneDetail()
        );
    }
}
