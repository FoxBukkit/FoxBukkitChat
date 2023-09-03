/*
 * foxbukkit-chat - ${project.description}
 * Copyright © ${year} Doridian (git@doridian.net)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.doridian.foxbukkit.chat;

import net.doridian.foxbukkit.permissions.FoxBukkitPermissions;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class PermissionsAdapter {
    FoxBukkitPermissions plugin;
    PermissionsAdapter(Plugin plugin) {
        this.plugin = (FoxBukkitPermissions)plugin;
    }

    public String getUserTag(UUID uuid) {
        return plugin.getHandler().getGroupTag(plugin.getHandler().getGroup(uuid));
    }
}
