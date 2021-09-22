package pl.chillcode.chillchat.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.chillcode.chillchat.ChillChat;
import pl.chillcode.chillchat.config.PluginConfiguration;
import pl.chillcode.chillchat.data.PluginData;

public class AsyncPlayerChatListener implements Listener {

    private final ChillChat plugin;

    private final PluginConfiguration pluginConfiguration;

    private final PluginData pluginData;

    public AsyncPlayerChatListener(ChillChat plugin) {
        this.plugin = plugin;

        this.pluginConfiguration = plugin.getPluginConfiguration();

        this.pluginData = plugin.getPluginData();
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if(this.pluginData.chat.enabled) {
            return;
        }

        event.setCancelled(true);
    }

}
