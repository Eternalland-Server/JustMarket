package net.sakuragame.eternal.justmarket.file.data;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommonConfine {

    private final Map<String, Integer> privilege;
    private int base;

    public CommonConfine(ConfigurationSection section) {
        this.privilege = new LinkedHashMap<>();
        this.base = 0;

        if (section == null) return;

        for (String key : section.getKeys(false)) {
            int value = section.getInt(key);
            if (key.equals("default")) {
                this.base = value;
                continue;
            }

            this.privilege.put(key, value);
        }
    }

    public int getUserCount(Player player) {
        for (String key : this.privilege.keySet()) {
            if (!player.hasPermission(key)) continue;
            return this.privilege.get(key);
        }

        return this.base;
    }

    public int getUserCount(Player player, String key) {
        if (this.privilege.containsKey(key)) return this.privilege.get(key);
        return this.base;
    }
}
