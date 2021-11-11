package pl.chillcode.chillcodechat.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.chillcode.chillcodechat.slowmode.SlowModeCache;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class BlockBreakListener implements Listener {
    SlowModeCache slowModeCache;

    @EventHandler
    public void onBreak(final BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.STONE) {
            return;
        }

        slowModeCache.getUser(event.getPlayer().getUniqueId()).addStone();
    }
}
