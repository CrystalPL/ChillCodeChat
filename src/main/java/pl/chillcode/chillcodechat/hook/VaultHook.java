package pl.chillcode.chillcodechat.hook;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

@UtilityClass
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaultHook {
    @Getter
    Permission permission;
    @Getter
    boolean enableVault;

    public void init() {
        try {
            final RegisteredServiceProvider<Permission> registration = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
            if (registration == null) {
                enableVault = false;
                throw new ClassNotFoundException();
            }

            permission = registration.getProvider();
            enableVault = true;

            Bukkit.getLogger().info("[ChillCodeChat] Vault został poprawnie załadowany");
        } catch (final NoClassDefFoundError | ClassNotFoundException exception) {
            Bukkit.getLogger().severe("[ChillCodeChat] Nie odnaleziono plugin vault, ustawianie opóźnienia dla grupy rang jest niemożliwe!");
        }
    }
}