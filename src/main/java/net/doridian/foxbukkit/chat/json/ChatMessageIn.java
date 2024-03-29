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
package net.doridian.foxbukkit.chat.json;

import net.doridian.foxbukkit.chat.FoxBukkitChat;
import net.doridian.foxbukkit.chat.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ChatMessageIn {
    public ChatMessageIn(FoxBukkitChat plugin, CommandSender commandSender) {
        this(plugin);
        if(commandSender instanceof Player) {
            this.from = new UserInfo(Utils.getCommandSenderUUID(commandSender), commandSender.getName());
        } else {
            this.from = new UserInfo(Utils.CONSOLE_UUID, commandSender.getName());
        }
    }

    public ChatMessageIn(FoxBukkitChat plugin) {
        this.timestamp = System.currentTimeMillis() / 1000;
        this.context = UUID.randomUUID();
        this.server = plugin.configuration.getValue("server-name", "Main");
        this.type = MessageType.TEXT;
    }

    private ChatMessageIn() {

    }

    public String server;
    public UserInfo from;

    public Long timestamp;

    public UUID context;
    public MessageType type;

    public String contents;
}
