package co.aikar.timings.lib;

import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class TimingManager {

    private static final Object LOCK = new Object();
    private static TimingType timingProvider;
    private final Plugin plugin;
    private final Map<String, MCTiming> timingCache = new HashMap<>(0);

    private TimingManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public static TimingManager of(Plugin plugin) {
        return new TimingManager(plugin);
    }

    public MCTiming ofStart(String name) {
        return ofStart(name, null);
    }

    public MCTiming ofStart(String name, MCTiming parent) {
        return of(name, parent).startTiming();
    }

    public MCTiming of(String name) {
        return of(name, null);
    }

    public MCTiming of(String name, MCTiming parent) {
        if (timingProvider == null) {
            synchronized (LOCK) {
                if (timingProvider == null) {
                    try {
                        Class<?> clazz = Class.forName("co.aikar.timings.Timing");
                        Method startTiming = clazz.getMethod("startTiming");
                        if (startTiming.getReturnType() != clazz) {
                            timingProvider = TimingType.MINECRAFT_18;
                        } else {
                            timingProvider = TimingType.MINECRAFT;
                        }
                    } catch (ClassNotFoundException | NoSuchMethodException ignored1) {
                        try {
                            Class.forName("org.spigotmc.CustomTimingsHandler");
                            timingProvider = TimingType.SPIGOT;
                        } catch (ClassNotFoundException ignored2) {
                            timingProvider = TimingType.EMPTY;
                        }
                    }
                }
            }
        }

        MCTiming timing;
        if (timingProvider.useCache()) {
            synchronized (timingCache) {
                String lowerKey = name.toLowerCase();
                timing = timingCache.get(lowerKey);
                if (timing == null) {
                    timing = timingProvider.newTiming(plugin, name, parent);
                    timingCache.put(lowerKey, timing);
                }
            }
            return timing;
        }

        return timingProvider.newTiming(plugin, name, parent);
    }
}
