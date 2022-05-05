package net.sakuragame.eternal.justmarket.commands.sub;

import net.sakuragame.eternal.justmarket.JustMarket;
import net.sakuragame.eternal.justmarket.commands.CommandPerms;
import net.sakuragame.eternal.justmarket.ui.UserView;
import com.taylorswiftcn.justwei.commands.sub.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenCommand extends SubCommand {
    @Override
    public String getIdentifier() {
        return "open";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 1) return;

        Player player = Bukkit.getPlayerExact(args[0]);
        if (player == null) return;
        UserView view = JustMarket.getUserManager().getCache(player);
        view.open();
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
