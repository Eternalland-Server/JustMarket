package net.sakuragame.eternal.justmarket.commands.sub;

import com.taylorswiftcn.justwei.commands.sub.SubCommand;
import net.sakuragame.eternal.justmarket.commands.CommandPerms;
import org.bukkit.command.CommandSender;

public class HelpCommand extends SubCommand {

    private final String[] commands;

    public HelpCommand() {
        this.commands = new String[] {
                " §7/market open - 打开全球市场",
                " §7/market reload - 重载配置文件"
        };
    }

    @Override
    public String getIdentifier() {
        return "help";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        for (String s : this.commands) {
            sender.sendMessage(s);
        }
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
