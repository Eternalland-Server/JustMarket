package net.sakuragame.eternal.justmarket.file.sub;

import net.sakuragame.eternal.justmarket.JustMarket;
import com.taylorswiftcn.justwei.util.MegumiUtil;
import net.sakuragame.eternal.justmarket.file.data.CommonConfine;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class ConfigFile {
    private static YamlConfiguration config;

    public static String prefix;

    private static CommonConfine putCountOfSell;
    private static CommonConfine putCountOfBuy;

    private static CommonConfine putDayOfSell;
    private static CommonConfine putDayOfBuy;

    private static double sellServiceCharge;
    private static double buyServiceCharge;

    public static void init() {
        config = JustMarket.getFileManager().getConfig();

        prefix = getString("prefix");

        putCountOfSell = new CommonConfine(config.getConfigurationSection("put-count.sell"));
        putCountOfBuy = new CommonConfine(config.getConfigurationSection("put-count.buy"));

        putDayOfSell = new CommonConfine(config.getConfigurationSection("put-day.sell"));
        putDayOfBuy = new CommonConfine(config.getConfigurationSection("put-day.buy"));

        sellServiceCharge = config.getDouble("service-charge.sell");
        buyServiceCharge = config.getDouble("service-charge.buy");
    }

    public static CommonConfine getPutCountOfSell() {
        return putCountOfSell;
    }

    public static CommonConfine getPutCountOfBuy() {
        return putCountOfBuy;
    }

    public static CommonConfine getPutDayOfSell() {
        return putDayOfSell;
    }

    public static CommonConfine getPutDayOfBuy() {
        return putDayOfBuy;
    }

    public static double getSellServiceCharge() {
        return sellServiceCharge;
    }

    public static double getBuyServiceCharge() {
        return buyServiceCharge;
    }

    private static String getString(String path) {
        return MegumiUtil.onReplace(config.getString(path));
    }

    private static List<String> getStringList(String path) {
        return MegumiUtil.onReplace(config.getStringList(path));
    }
}
