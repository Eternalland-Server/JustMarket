package net.sakuragame.eternal.justmarket.commands;

import com.taylorswiftcn.justwei.commands.JustCommand;
import net.sakuragame.eternal.justmarket.commands.sub.HelpCommand;
import net.sakuragame.eternal.justmarket.commands.sub.OpenCommand;
import net.sakuragame.eternal.justmarket.commands.sub.ReloadCommand;

public class MainCommand extends JustCommand {

    public MainCommand() {
        super(new HelpCommand());
        register(new OpenCommand());
        register(new ReloadCommand());
    }
}
