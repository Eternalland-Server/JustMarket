package net.sakuragame.eternal.justmarket.ui.shelf;

import net.sakuragame.eternal.dragoncore.config.FolderType;
import net.sakuragame.eternal.justmarket.core.commodity.Commodity;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import lombok.Getter;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public abstract class CommodityShelf {

    private final ScreenUI UI;
    private final Map<String, String> placeholder;
    private final List<Commodity> commodities;

    public CommodityShelf(List<Commodity> commodities) {
        this.UI = new ScreenUI("market_commodity_shelf");
        this.placeholder = new HashMap<>();
        this.commodities = commodities;
    }

    public static void clear(Player player) {
        ItemStack air = new ItemStack(Material.AIR);
        Map<String, String> placeholder = new HashMap<>();
         for (int i = 1; i < 7; i++) {
             placeholder.put("market_g_n_" + i, "");
             placeholder.put("market_g_a_" + i, "");
             placeholder.put("market_g_u_" + i, "");
             placeholder.put("market_g_p_" + i, "");
             placeholder.put("market_g_t_" + i, "");
             PacketSender.putClientSlotItem(player, "market_g_" + i, air);
         }
         PacketSender.sendSyncPlaceholder(player, placeholder);
         PacketSender.sendYaml(player, FolderType.Gui, "market_commodity_shelf", new YamlConfiguration());
    }

    public abstract void send(Player player);

    public void clearLine(Player player, int i) {
        this.putName(i, "");
        this.putAmount(i, "");
        this.putUnitPrice(i, "");
        this.putTotalPrice(i, "");
        this.putExpireTime(i, "");
        this.putItem(player, i, new ItemStack(Material.AIR));
    }

    public void putName(int i, String s) {
        this.placeholder.put("market_g_n_" + i, s);
    }

    public void putAmount(int i, String s) {
        this.placeholder.put("market_g_a_" + i, s);
    }

    public void putUnitPrice(int i, String s) {
        this.placeholder.put("market_g_u_" + i, s);
    }

    public void putTotalPrice(int i, String s) {
        this.placeholder.put("market_g_p_" + i, s);
    }

    public void putExpireTime(int i, String s) {
        this.placeholder.put("market_g_t_" + i, s);
    }

    public void putItem(Player player, int i, ItemStack item) {
        PacketSender.putClientSlotItem(player, "market_g_" + i, item);
    }
}
