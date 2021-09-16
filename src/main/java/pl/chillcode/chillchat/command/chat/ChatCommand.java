package pl.chillcode.chillchat.command.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import pl.chillcode.chillchat.ChillChat;
import pl.chillcode.chillchat.command.SubCommand;
import pl.chillcode.chillchat.command.chat.sub.DisableSubCommand;
import pl.chillcode.chillchat.command.chat.sub.EnableSubCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatCommand implements CommandExecutor, TabExecutor {

    private final ChillChat plugin;

    private final Map<String, SubCommand> subCommandMap = new HashMap<>();
    private final Set<SubCommand> argumentSet = new HashSet<>();

    public ChatCommand(ChillChat plugin) {
        this.plugin = plugin;

        this.registerSubCommand(new EnableSubCommand(), "enable", "on", "wlacz");
        this.registerSubCommand(new DisableSubCommand(), "disable", "off", "wylacz");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            SubCommand subCommand = this.subCommandMap.get(args[0]);
            if (subCommand != null) {
                if (!sender.hasPermission(subCommand.getPermission())) {
                    sender.sendMessage("Nie masz uprawnien gosciu no");
                    return true;
                }

                subCommand.execute(sender, args);
            }
            return true;
        }

        sender.sendMessage("Nie znalezlismy komedny lol");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(
                    args[0],
                    this.argumentSet.stream()
                            .filter(subCommand -> sender.hasPermission(subCommand.getPermission()))
                            .map(SubCommand::getName)
                            .collect(Collectors.toList()),
                    completions
            );
        }

        return completions;
    }

    private void registerSubCommand(SubCommand subCommand, String... argumentNames) {
        for (String argumentName : argumentNames) {
            this.subCommandMap.put(argumentName, subCommand);

            this.argumentSet.add(subCommand);
        }
    }

}
