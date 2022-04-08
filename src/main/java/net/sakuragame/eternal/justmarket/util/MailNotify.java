package net.sakuragame.eternal.justmarket.util;

import ink.ptms.zaphkiel.ZaphkielAPI;
import ink.ptms.zaphkiel.api.Item;
import net.sakuragame.eternal.mail.api.MailSystemAPI;
import net.sakuragame.eternal.mail.api.model.AnnexData;
import net.sakuragame.eternal.mail.api.model.MailContents;
import net.sakuragame.eternal.mail.api.user.MailAccount;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class MailNotify {

    private final static String TITLE = "&8&l[&a&l全球市场&8&l] &f";

    public static void sendSellSuccess(Player player, String id, int amount, int earnings, int serviceCharge) {
        sendSellSuccess(player.getUniqueId(), id, amount, earnings, serviceCharge);
    }

    public static void sendSellSuccess(UUID uuid, String id, int amount, int earnings, int serviceCharge) {
        MailContents mail = MailSystemAPI.getContentManager().createContent(
                TITLE + "出售交易通知",
                Arrays.asList(
                        "&a你出售的商品已经交易完成",
                        " ",
                        "&f商品: &6" + getName(id),
                        "&f数量: &6" + amount,
                        "&f收益: &6" + earnings + "金币",
                        "&f手续费: &6" + serviceCharge + "金币",
                        " ",
                        "&a最终收益 &b" + (earnings - serviceCharge) + " &a金币已自动转入你的账户"
                ),
                false,
                7
        );

        send(uuid, mail);
    }

    public static void sendSellFailure(Player player, AnnexData annex) {
        sendSellFailure(player.getUniqueId(), annex);
    }

    public static void sendSellFailure(UUID uuid, AnnexData annex) {
        MailContents mail = MailSystemAPI.getContentManager().createContent(
                TITLE + "出售下架通知",
                Arrays.asList(
                        "&a你上架的商品已到期",
                        " ",
                        " ",
                        " ",
                        " ",
                        " ",
                        "&f请在 &a7 &f日内领取附件"
                ),
                false,
                1,
                Collections.singletonList(annex),
                7
        );

        send(uuid, mail);
    }

    public static void sendBuySuccess(Player player, AnnexData annex) {
        sendBuySuccess(player.getUniqueId(), annex);
    }

    public static void sendBuySuccess(UUID uuid, AnnexData annex) {
        MailContents mail = MailSystemAPI.getContentManager().createContent(
                TITLE + "求购交易通知",
                Arrays.asList(
                        "&a你求购的商品已经交易完成",
                        " ",
                        " ",
                        " ",
                        " ",
                        " ",
                        "&f请在 &a7 &f日之内领取附件"
                ),
                false,
                1,
                Collections.singletonList(annex),
                7
        );

        send(uuid, mail);
    }

    public static void sendBuyFailure(Player player, String id, int amount, int price) {
        sendBuyFailure(player.getUniqueId(), id, amount, price);
    }

    public static void sendBuyFailure(UUID uuid, String id, int amount, int price) {
        MailContents mail = MailSystemAPI.getContentManager().createContent(
                TITLE + "求购下架通知",
                Arrays.asList(
                        "&a你求购的商品已到期",
                        " ",
                        "&f商品: &6" + getName(id),
                        "&f数量: &6" + amount,
                        " ",
                        " ",
                        "&a质押在系统的 &b" + price + " &a金币已自动转入你的账户"
                ),
                false,
                7
        );

        send(uuid, mail);
    }

    private static String getName(String id) {
        Item item = ZaphkielAPI.INSTANCE.getRegisteredItem().get(id);
        return item.getName().get("NAME");
    }

    private static void send(UUID uuid, MailContents mail) {
        MailAccount account = MailSystemAPI.getUserManager().getAccount(uuid);
        if (account == null) return;
        account.sendMail(mail);
    }
}
