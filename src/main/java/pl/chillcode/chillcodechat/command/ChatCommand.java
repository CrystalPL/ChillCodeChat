package pl.chillcode.chillcodechat.command;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillcodechat.command.sub.ChatClearSubCommand;
import pl.chillcode.chillcodechat.command.sub.ChatStatusSubCommand;
import pl.chillcode.chillcodechat.command.sub.SlowModeSubCommand;
import pl.chillcode.chillcodechat.command.sub.StoneSubCommand;
import pl.chillcode.chillcodechat.config.Config;
import pl.chillcode.chillcodechat.slowmode.SlowModeCache;
import pl.chillcode.chillcodechat.storage.Provider;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.impl.MultiCommand;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.message.api.MessageAPI;

import java.util.Map;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class ChatCommand extends MultiCommand {
    public ChatCommand(final MessageAPI messageAPI, final Map<Class<? extends Command>, CommandData> commandDataMap, final Config config, final Provider provider, final SlowModeCache slowModeCache, final JavaPlugin plugin) {
        super(messageAPI, commandDataMap);

        registerSubCommand(new ChatClearSubCommand(messageAPI, commandDataMap));
        registerSubCommand(new ChatStatusSubCommand(messageAPI, commandDataMap, config, plugin));
        registerSubCommand(new SlowModeSubCommand(messageAPI, commandDataMap, config, provider, slowModeCache, plugin));
        registerSubCommand(new StoneSubCommand(messageAPI, commandDataMap, config, plugin));
    }

    @Override
    public String getPermission() {
        return "chillcode.chat";
    }

    @Override
    public boolean isUseConsole() {
        return true;
    }

    @Override
    public String getCommandUsagePath() {
        return "usage";
    }

    @Override
    public int maxArgumentLength() {
        return 4;
    }

    @Override
    public int minArgumentLength() {
        return 1;
    }
}
