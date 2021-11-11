package pl.chillcode.chillcodechat.task;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.chillcode.chillcodechat.slowmode.SlowModeCache;
import pl.chillcode.chillcodechat.storage.Provider;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class AutoSaveTask implements Runnable {
    Provider provider;
    SlowModeCache slowModeCache;

    @Override
    public void run() {
        slowModeCache.getUserDelayMap().forEach(provider::saveUser);
    }
}
