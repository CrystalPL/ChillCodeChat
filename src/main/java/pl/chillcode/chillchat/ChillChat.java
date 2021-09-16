package pl.chillcode.chillchat;

import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillchat.command.chat.ChatCommand;

public class ChillChat extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("chat").setExecutor(new ChatCommand(this));
    }

}
