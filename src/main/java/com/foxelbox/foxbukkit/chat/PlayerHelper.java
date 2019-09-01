/**
 * This file is part of FoxBukkitChat.
 *
 * FoxBukkitChat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkitChat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkitChat.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbukkit.chat;

import com.foxelbox.dependencies.config.Configuration;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerHelper {
    private FoxBukkitChat plugin;
    public Map<String,String> playerNameToUUID;
    public Map<String,String> playerUUIDToName;

    public Map<String,String> playerNicks;

    public Map<String,String> ignoredByList;
    private final Map<UUID, Set<UUID>> ignoreCache;

    public void refreshUUID(Player player) {
        playerUUIDToName.put(player.getUniqueId().toString(), player.getName());
        playerNameToUUID.put(player.getName().toLowerCase(), player.getUniqueId().toString());
    }

    public PlayerHelper(FoxBukkitChat plugin) {
        this.plugin = plugin;
        playerNameToUUID = new Configuration(plugin.getDataFolder(), "playerNameToUUID");
        playerUUIDToName = new Configuration(plugin.getDataFolder(), "playerUUIDToName");
        playerNicks = new Configuration(plugin.getDataFolder(), "playernicks");
        ignoreCache = new HashMap<>();

        Configuration ignoredByListC = new Configuration(plugin.getDataFolder(), "ignoredByList");
        ignoredByListC.addOnChangeHook((key, value) -> {
            synchronized (ignoreCache) {
                putIgnoreCache(UUID.fromString(key), value);
            }
        });
        ignoredByList = ignoredByListC;
    }

    private Set<UUID> putIgnoreCache(UUID uuid, String data) {
        HashSet<UUID> dataSet = new HashSet<>();
        if(data != null && !data.isEmpty()) {
            for (String entry : data.split(",")) {
                dataSet.add(UUID.fromString(entry));
            }
        }
        synchronized (ignoreCache) {
            ignoreCache.put(uuid, dataSet);
        }
        return dataSet;
    }

    public Set<UUID> getIgnoredBy(UUID uuid) {
        synchronized (ignoreCache) {
            Set<UUID> result = ignoreCache.get(uuid);
            if(result == null) {
                result = putIgnoreCache(uuid, ignoredByList.get(uuid.toString()));
            }
            return result;
        }
    }
}
