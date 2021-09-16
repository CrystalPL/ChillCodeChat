package pl.chillcode.chillchat.command.chat.sub;

import org.bukkit.command.CommandSender;
import pl.chillcode.chillchat.command.SubCommand;

public class EnableSubCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("wlomczono");
    }

    @Override
    public String getName() {
        return "enable";
    }

    @Override
    public String getPermission() {
        return "chillchat.chat.enable";
    }

}
