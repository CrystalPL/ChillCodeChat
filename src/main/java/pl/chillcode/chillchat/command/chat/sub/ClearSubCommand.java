package pl.chillcode.chillchat.command.chat.sub;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import pl.chillcode.chillchat.ChillChat;
import pl.chillcode.chillchat.command.SubCommand;
import pl.chillcode.chillchat.command.chat.ChatCommand;
import pl.chillcode.chillchat.config.PluginConfiguration;

public class ClearSubCommand implements SubCommand {

    private final ChillChat plugin;

    private final PluginConfiguration pluginConfiguration;

    public ClearSubCommand(ChillChat plugin) {
        this.plugin = plugin;

        this.pluginConfiguration = plugin.getPluginConfiguration();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ChatCommand.clearChat(this.pluginConfiguration.chat.clear.iterations);

        this.pluginConfiguration.messages.chat.cleared.forEach(message -> Bukkit.broadcastMessage(message.replace("{ADMIN}", sender.getName())));
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getPermission() {
        return "chillchat.chat.clear";
    }

}
