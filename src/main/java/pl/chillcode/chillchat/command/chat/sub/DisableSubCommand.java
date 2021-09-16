package pl.chillcode.chillchat.command.chat.sub;

import org.bukkit.command.CommandSender;
import pl.chillcode.chillchat.command.SubCommand;

public class DisableSubCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("wylomczono");
    }

    @Override
    public String getName() {
        return "disable";
    }

    @Override
    public String getPermission() {
        return "chillchat.chat.disable";
    }

}
