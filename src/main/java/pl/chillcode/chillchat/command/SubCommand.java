package pl.chillcode.chillchat.command;

import org.bukkit.command.CommandSender;

public interface SubCommand {

    void execute(CommandSender sender, String[] args);

    String getName();

    String getPermission();

}
