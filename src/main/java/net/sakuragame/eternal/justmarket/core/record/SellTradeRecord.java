package net.sakuragame.eternal.justmarket.core.record;

import com.alibaba.fastjson.JSON;
import net.sakuragame.eternal.justmarket.JustMarket;
import net.sakuragame.eternal.justmarket.core.TradeType;
import net.sakuragame.eternal.justmarket.file.sub.ConfigFile;
import net.sakuragame.eternal.justmarket.util.MailNotify;
import lombok.Getter;
import net.sakuragame.eternal.gemseconomy.api.GemsEconomyAPI;
import net.sakuragame.serversystems.manage.client.api.ClientManagerAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class SellTradeRecord extends TradeRecord {

    private final String itemID;
    private final int amount;
    private final double earnings;

    public SellTradeRecord(int seller, int buyer, String itemID, int amount, double earnings) {
        super(seller, buyer);
        this.itemID = itemID;
        this.amount = amount;
        this.earnings = earnings;
    }

    public SellTradeRecord(UUID uuid, int seller, int buyer, String detail) {
        super(uuid, seller, buyer);
        List<String> args = JSON.parseArray(detail, String.class);
        this.itemID = args.get(0);
        this.amount = Integer.parseInt(args.get(1));
        this.earnings = Double.parseDouble(args.get(2));
    }

    @Override
    public String geneDetail() {
        return JSON.toJSONString(Arrays.asList(itemID, String.valueOf(amount), String.valueOf(earnings)));
    }

    @Override
    public boolean settlement() {
        Player player = Bukkit.getPlayer(ClientManagerAPI.getUserUUID(this.getSeller()));
        if (player == null) return false;

        int serviceCharge = (int) (this.earnings * ConfigFile.getSellServiceCharge());
        int last = (int) this.earnings - serviceCharge;

        GemsEconomyAPI.deposit(player.getUniqueId(), last);
        MailNotify.sendSellSuccess(player, this.itemID, this.amount, (int) this.earnings, serviceCharge);

        return true;
    }

    @Override
    public void saveToDB() {
        JustMarket.getStorageManager().insertTradeRecord(
                this.getUUID(),
                this.getSeller(),
                this.getBuyer(),
                TradeType.Sell,
                this.geneDetail()
        );
    }
}
