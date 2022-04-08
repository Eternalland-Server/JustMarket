package net.sakuragame.eternal.justmarket.file;

import net.sakuragame.eternal.justmarket.JustMarket;
import com.taylorswiftcn.justwei.file.JustConfiguration;
import net.sakuragame.eternal.justmarket.file.sub.ConfigFile;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;


public class FileManager extends JustConfiguration {

    @Getter private YamlConfiguration config;

    public FileManager(JustMarket plugin) {
        super(plugin);
    }

    public void init() {
        config = initFile("config.yml");

        ConfigFile.init();
    }
}
