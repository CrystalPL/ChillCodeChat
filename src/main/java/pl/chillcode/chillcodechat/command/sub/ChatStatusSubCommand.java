package pl.chillcode.chillcodechat.command.sub;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillcodechat.command.SubCommand;
import pl.chillcode.chillcodechat.config.Config;
import pl.crystalek.crcapi.message.MessageAPI;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class ChatStatusSubCommand implements SubCommand {
    Config config;
    JavaPlugin plugin;
    MessageAPI messageAPI;

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final boolean changeStatus = args[0].equalsIgnoreCase("on") ?
                changeStatus(sender, true, "status.onError", "status.Enable", "enableBroadcast") :
                changeStatus(sender, false, "status.offError", "status.Disable", "disableBroadcast");

        if (!changeStatus) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!config.saveServerSettings()) {
                messageAPI.sendMessage("saveError", sender);
            }
        });
    }

    private boolean changeStatus(final CommandSender sender, final boolean newStatus, final String messagePathIfError, final String messagePathIfSuccess, final String broadcastPath) {
        if (config.isChatEnable() == newStatus) {
            messageAPI.sendMessage(messagePathIfError, sender);
            return false;
        }

        config.setChatEnable(newStatus);
        messageAPI.sendMessage(messagePathIfSuccess, sender);

        if (config.isBroadcastAction()) {
            messageAPI.broadcast(broadcastPath, ImmutableMap.of("{ADMIN_NAME}", sender.getName()));
        }

        return true;
    }

    @Override
    public int maxArgumentLength() {
        return 1;
    }

    @Override
    public int minArgumentLength() {
        return 1;
    }

    @Override
    public String getPermission() {
        return "chillcode.chat.status";
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String usagePathMessage() {
        return "status.usage";
    }
}
