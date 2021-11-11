package pl.chillcode.chillcodechat.command.sub;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillcodechat.command.SubCommand;
import pl.chillcode.chillcodechat.config.Config;
import pl.chillcode.chillcodechat.hook.VaultHook;
import pl.chillcode.chillcodechat.slowmode.SlowModeCache;
import pl.chillcode.chillcodechat.storage.Provider;
import pl.chillcode.chillcodechat.user.User;
import pl.crystalek.crcapi.lib.adventure.adventure.text.Component;
import pl.crystalek.crcapi.message.MessageAPI;
import pl.crystalek.crcapi.message.impl.ChatMessage;
import pl.crystalek.crcapi.message.loader.MessageUtil;
import pl.crystalek.crcapi.util.NumberUtil;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class SlowModeSubCommand implements SubCommand {
    Set<String> firstArgumentList = ImmutableSet.of("player", "server", "group", "info");
    Config config;
    Provider provider;
    SlowModeCache slowModeCache;
    JavaPlugin plugin;
    MessageAPI messageAPI;

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        switch (args[1].toLowerCase()) {
            case "player": {
                if (args.length != 4) {
                    messageAPI.sendMessage("slowmode.usage", sender);
                    return;
                }

                final Optional<Integer> slowModeTimeOptional = NumberUtil.getInt(args[3]);
                if (!slowModeTimeOptional.isPresent()) {
                    messageAPI.sendMessage("slowmode.timeError", sender);
                    return;
                }

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    final Integer slowModeTime = slowModeTimeOptional.get();
                    final Player player = Bukkit.getPlayer(args[2]);

                    if (player == null) {
                        final Optional<UUID> playerUUIDOptional = provider.getPlayerUUID(args[2]);
                        if (!playerUUIDOptional.isPresent()) {
                            messageAPI.sendMessage("playerNotExists", sender);
                            return;
                        }

                        provider.setPlayerDelay(playerUUIDOptional.get(), slowModeTime);
                    } else {
                        slowModeCache.getUser(player.getUniqueId()).setSlowDownTime(slowModeTime);
                        messageAPI.sendMessage("slowmode.setSlowModePlayer", player, ImmutableMap.of("{SLOWMODE}", slowModeTime, "{ADMIN_NAME}", sender.getName()));
                    }

                    messageAPI.sendMessage("slowmode.setSlowModeAdmin", sender, ImmutableMap.of("{SLOWMODE}", slowModeTime, "{PLAYER_NAME}", args[2]));
                });

                break;
            }
            case "server": {
                if (args.length != 3) {
                    messageAPI.sendMessage("slowmode.usage", sender);
                    return;
                }

                final Optional<Integer> slowModeTimeOptional = NumberUtil.getInt(args[2]);
                if (!slowModeTimeOptional.isPresent()) {
                    messageAPI.sendMessage("slowmode.timeError", sender);
                    return;
                }

                final Integer slowModeTime = slowModeTimeOptional.get();
                config.setServerSlowMode(slowModeTime);

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    messageAPI.sendMessage(config.saveServerSettings() ? "slowmode.setSlowModeSever" : "saveError", sender, ImmutableMap.of("{SLOWMODE}", slowModeTime));
                    if (config.isBroadcastAction()) {
                        messageAPI.broadcast("slowmode.serverSlowModeBroadcast", ImmutableMap.of("{SLOWMODE}", slowModeTime));
                    }
                });

                break;
            }
            case "group": {
                if (args.length != 4) {
                    messageAPI.sendMessage("slowmode.usage", sender);
                    return;
                }

                final Optional<Integer> slowModeTimeOptional = NumberUtil.getInt(args[3]);
                if (!slowModeTimeOptional.isPresent()) {
                    messageAPI.sendMessage("slowmode.timeError", sender);
                    return;
                }

                if (!VaultHook.isEnableVault()) {
                    messageAPI.sendMessage("slowmode.vaultNotFound", sender);
                    return;
                }

                final Permission permission = VaultHook.getPermission();
                final String[] groups = permission.getGroups();
                if (Arrays.stream(groups).noneMatch(group -> group.equalsIgnoreCase(args[2]))) {
                    messageAPI.sendMessage("slowmode.groupNotExists", sender);
                    return;
                }

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    final Integer slowModeTime = slowModeTimeOptional.get();
                    provider.setGroupDelay(args[2], slowModeTime);
                    slowModeCache.setGroupDelay(args[2], slowModeTime);

                    final Map<String, Object> replacements = ImmutableMap.of("{SLOWMODE}", slowModeTime, "{RANK}", args[2], "{ADMIN_NAME}", sender.getName());

                    Bukkit.getOnlinePlayers().stream()
                            .filter(player -> permission.playerInGroup(player, args[2]))
                            .forEach(player -> messageAPI.sendMessage("slowmode.setSlowModeGroupPlayer", player, replacements));

                    messageAPI.sendMessage("slowmode.setSlowModeGroupAdmin", sender, replacements);

                    if (config.isBroadcastAction()) {
                        messageAPI.broadcast("slowmode.groupSlowModeBroadcast", replacements);
                    }
                });

                break;
            }
            case "info": {
                if (args.length == 2) {
                    final Optional<Component> rankSlowModeFormatComponentOptional = messageAPI.getComponent("slowmode.rankSlowModeListFormat", ChatMessage.class);
                    if (!rankSlowModeFormatComponentOptional.isPresent()) {
                        messageAPI.sendMessage("slowmode.rankSlowModeListFormat", sender);
                        return;
                    }

                    final Component rankSlowModeFormatComponent = rankSlowModeFormatComponentOptional.get();
                    Component componentFormat = Component.empty();

                    final List<Map.Entry<String, Integer>> entrySet = new ArrayList<>(slowModeCache.getGroupDelayMap().entrySet());
                    final int entrySetSize = entrySet.size();
                    for (int i = 0; i < entrySetSize; i++) {
                        final Map.Entry<String, Integer> entry = entrySet.get(i);
                        final Map<String, Object> replacements = ImmutableMap.of("{RANK}", entry.getKey(), "{SLOWMODE}", entry.getValue());

                        componentFormat = componentFormat.append(MessageUtil.replace(rankSlowModeFormatComponent, replacements));

                        if (i < entrySetSize - 1) {
                            componentFormat = componentFormat.append(Component.newline());
                        }
                    }

                    final Map<String, Component> replacements = ImmutableMap.of("{SLOWMODE}", Component.text(config.getServerSlowMode()), "{RANKFORMAT_SLOWMODE}", componentFormat);

                    messageAPI.sendMessageComponent("slowmode.generalInfoList", sender, replacements);
                } else if (args.length == 4) {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        final Player player = Bukkit.getPlayer(args[3]);
                        final int playerDelay;
                        if (player == null) {
                            final Optional<User> userOptional = provider.getUser(args[3]);
                            if (!userOptional.isPresent()) {
                                messageAPI.sendMessage("playerNotExists", sender);
                                return;
                            }

                            playerDelay = userOptional.get().getSlowDownTime();
                        } else {
                            playerDelay = slowModeCache.getUser(player.getUniqueId()).getSlowDownTime();
                        }

                        messageAPI.sendMessage("slowmode.playerSlowModeInfo", sender, ImmutableMap.of("{SLOWMODE}", playerDelay));
                    });
                } else {
                    messageAPI.sendMessage("slowmode.usage", sender);
                }

                break;
            }
            default:
                messageAPI.sendMessage("slowmode.usage", sender);
        }
    }

    @Override
    public int maxArgumentLength() {
        return 4;
    }

    @Override
    public int minArgumentLength() {
        return 2;
    }

    @Override
    public String getPermission() {
        return "chillcode.chat.slowmode";
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return firstArgumentList.stream().filter(argument -> argument.startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        }

        if (args.length == 3) {
            switch (args[1].toLowerCase()) {
                case "player":
                    return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(player -> player.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
                case "group":
                    if (!VaultHook.isEnableVault()) {
                        return new ArrayList<>();
                    }

                    return Arrays.asList(VaultHook.getPermission().getGroups());
                case "info":
                    if ("player".startsWith(args[2].toLowerCase())) {
                        return ImmutableList.of("player");
                    }
            }
        }

        if (args.length == 4) {
            if (!args[1].equalsIgnoreCase("info")) {
                return new ArrayList<>();
            }

            if (args[2].equalsIgnoreCase("player")) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(player -> player.toLowerCase().startsWith(args[3].toLowerCase())).collect(Collectors.toList());
            }
        }

        return new ArrayList<>();
    }

    @Override
    public String usagePathMessage() {
        return "slowmode.usage";
    }
}
