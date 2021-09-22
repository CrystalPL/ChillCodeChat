package pl.chillcode.chillchat.command.chat.sub;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.chillcode.chillchat.ChillChat;
import pl.chillcode.chillchat.command.SubCommand;
import pl.chillcode.chillchat.command.chat.ChatCommand;
import pl.chillcode.chillchat.config.PluginConfiguration;
import pl.chillcode.chillchat.data.PluginData;

public class DisableSubCommand implements SubCommand {

    private final ChillChat plugin;

    private final PluginConfiguration pluginConfiguration;

    private final PluginData pluginData;

    public DisableSubCommand(ChillChat plugin) {
        this.plugin = plugin;

        this.pluginConfiguration = plugin.getPluginConfiguration();

        this.pluginData = plugin.getPluginData();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(this.pluginConfiguration.chat.clear.onDisable) {
            ChatCommand.clearChat(this.pluginConfiguration.chat.clear.iterations);
        }

        this.pluginData.chat.enabled = false;
        this.pluginData.save();

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.pluginConfiguration.messages.chat.disabled.forEach(message -> player.sendMessage(message.replace("{ADMIN}", sender.getName())));
        }
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
