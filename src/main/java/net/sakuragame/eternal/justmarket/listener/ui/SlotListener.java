package net.sakuragame.eternal.justmarket.listener.ui;

import com.sakuragame.eternal.justattribute.core.soulbound.SoulBound;
import net.sakuragame.eternal.dragoncore.api.event.PlayerSlotUpdateEvent;
import net.sakuragame.eternal.justmarket.core.commodity.CommodityType;
import net.sakuragame.eternal.justmarket.ui.MarketUIManager;
import net.sakuragame.eternal.justmarket.ui.SlotCache;
import net.sakuragame.eternal.justmarket.util.Utils;
import com.taylorswiftcn.justwei.util.MegumiUtil;
import com.taylorswiftcn.megumi.uifactory.event.screen.UIFScreenCloseEvent;
import net.sakuragame.eternal.dragoncore.api.event.slot.PlayerSlotClickEvent;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justmessage.api.MessageAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class SlotListener implements Listener {

    @EventHandler
    public void onClose(UIFScreenCloseEvent e) {
        Player player = e.getPlayer();
        String screenID = e.getScreenID();

        if (screenID.equals(MarketUIManager.PUT_SELL_UI_ID)) {
            SlotCache.backSlot(player, MarketUIManager.SELL_SLOT);
            return;
        }

        if (screenID.equals(MarketUIManager.PUT_BUY_UI_ID)) {
            SlotCache.backSlot(player, MarketUIManager.BUY_SLOT);
        }
    }

    @EventHandler
    public void onClick(PlayerSlotClickEvent e) {
        Player player = e.getPlayer();
        String identifier = e.getIdentifier();

        ItemStack handItem = player.getItemOnCursor();

        if (!(identifier.equals(MarketUIManager.BUY_SLOT) ||
                identifier.equals(MarketUIManager.SELL_SLOT))
        ) return;


        if (!MegumiUtil.isEmpty(handItem)) {
            CommodityType type = Utils.getCommodityType(handItem);
            if (type == null) {
                MessageAPI.sendActionTip(player, "&c&l该物品不能上架出售");
                e.setCancelled(true);
                return;
            }

            if (type == CommodityType.Equip && !SoulBound.isSeal(handItem)) {
                MessageAPI.sendActionTip(player, "&c&l该物品需要在铁匠铺 &6&l封印 &c&l后才能上架");
                e.setCancelled(true);
                return;
            }

            e.setSlotItem(SlotCache.removeSlot(player, identifier));
            return;
        }

        e.setSlotItem(SlotCache.removeSlot(player, identifier));
    }

    @EventHandler
    public void onUpdate(PlayerSlotUpdateEvent e) {
        Player player = e.getPlayer();
        String identifier = e.getIdentifier();
        ItemStack item = e.getItemStack();

        if (!(identifier.equals(MarketUIManager.BUY_SLOT) ||
                identifier.equals(MarketUIManager.SELL_SLOT))
        ) return;

        String gui = identifier.equals(MarketUIManager.SELL_SLOT) ? MarketUIManager.PUT_SELL_UI_ID : MarketUIManager.PUT_BUY_UI_ID;
        if (item.getType() != Material.AIR) {
            SlotCache.putSlot(player, identifier, item);

            String name = item.getItemMeta().getDisplayName();
            this.setCommodityName(player, gui, name);
        }

        this.setCommodityName(player, gui, "&9商品名称");
    }

    private void setCommodityName(Player player, String gui, String name) {
        PacketSender.sendRunFunction(
                player,
                gui,
                "func.Component_Set('name', 'texts', '" + name + "');",
                false
        );
    }
}
