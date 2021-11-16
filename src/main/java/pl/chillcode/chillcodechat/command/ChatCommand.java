package pl.chillcode.chillcodechat.command;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillcodechat.command.sub.ChatClearCommand;
import pl.chillcode.chillcodechat.command.sub.ChatStatusSubCommand;
import pl.chillcode.chillcodechat.command.sub.SlowModeSubCommand;
import pl.chillcode.chillcodechat.command.sub.StoneSubCommand;
import pl.chillcode.chillcodechat.config.Config;
import pl.chillcode.chillcodechat.slowmode.SlowModeCache;
import pl.chillcode.chillcodechat.storage.Provider;
import pl.crystalek.crcapi.message.MessageAPI;

import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class ChatCommand extends Command {
    Map<String, SubCommand> subCommandMap = new HashMap<>();
    Set<String> argumentList;
    MessageAPI messageAPI;

    public ChatCommand(final Config config, final JavaPlugin plugin, final Provider provider, final SlowModeCache slowModeCache, final MessageAPI messageAPI) {
        super(config.getCommandName());
        setAliases(config.getCommandAliases());
        this.messageAPI = messageAPI;

        subCommandMap.put("clear", new ChatClearCommand(messageAPI));
        final ChatStatusSubCommand chatStatusSubCommand = new ChatStatusSubCommand(config, plugin, messageAPI);
        subCommandMap.put("on", chatStatusSubCommand);
        subCommandMap.put("off", chatStatusSubCommand);
        subCommandMap.put("slowmode", new SlowModeSubCommand(config, provider, slowModeCache, plugin, messageAPI));
        subCommandMap.put("stone", new StoneSubCommand(config, plugin, messageAPI));

        this.argumentList = subCommandMap.keySet();
    }

    @Override
    public boolean execute(final CommandSender sender, final String commandLabel, final String[] args) {
        if (!sender.hasPermission("chillcode.chat.base")) {
            messageAPI.sendMessage("noPermission", sender, ImmutableMap.of("{PERMISSION}", "chillcode.chat.base"));
            return true;
        }

        final int argLength = args.length;
        if (argLength < 1 || argLength > 4) {
            messageAPI.sendMessage("usage", sender);
            return true;
        }

        final String firstArgument = args[0].toLowerCase();
        if (!subCommandMap.containsKey(firstArgument)) {
            messageAPI.sendMessage("usage", sender);
            return true;
        }
        final SubCommand subCommand = subCommandMap.get(firstArgument);

        final String permission = subCommand.getPermission();
        if (!sender.hasPermission(permission)) {
            messageAPI.sendMessage("noPermission", sender, ImmutableMap.of("{PERMISSION}", permission));
            return true;
        }

        if (argLength < subCommand.minArgumentLength() || argLength > subCommand.maxArgumentLength()) {
            messageAPI.sendMessage(subCommand.usagePathMessage(), sender);
            return true;
        }

        subCommand.execute(sender, args);
        return true;
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return argumentList.stream().filter(argument -> argument.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }

        if ((args.length == 2 || args.length == 3 || args.length == 4) && args[0].equalsIgnoreCase("slowmode")) {
            return subCommandMap.get("slowmode").tabComplete(sender, args);
        }

        return new ArrayList<>();
    }
}
