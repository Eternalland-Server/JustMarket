package net.sakuragame.eternal.justmarket.commands.sub;

import net.sakuragame.eternal.justmarket.JustMarket;
import net.sakuragame.eternal.justmarket.commands.CommandPerms;
import com.taylorswiftcn.justwei.commands.sub.SubCommand;
import net.sakuragame.eternal.justmarket.file.sub.ConfigFile;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand {

    @Override
    public String getIdentifier() {
        return "reload";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        JustMarket.getInstance().reload();
        sender.sendMessage(ConfigFile.prefix + "已重载配置文件");
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public String getPermission() {
        return CommandPerms.ADMIN.getNode();
    }
}
