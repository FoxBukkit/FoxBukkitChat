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

import java.util.*;
import java.util.regex.Pattern;

public class ChatMessageOut {
    public ChatMessageOut(FoxBukkitChat plugin, UserInfo from) {
        this(plugin.configuration.getValue("server-name", "Main"), from);
    }

    public ChatMessageOut(String server, UserInfo from) {
        this.timestamp = System.currentTimeMillis() / 1000;
        this.server = server;
        this.from = from;
        this.to = new MessageTarget(TargetType.ALL, null);
        this.context = UUID.randomUUID();
    }

    private ChatMessageOut() {

    }

    private static String[] xmlEscapeArray(String[] in) {
        final String[] out = new String[in.length];
        for(int i = 0; i < in.length; i++)
            out[i] = convertLegacyColors(Utils.XMLEscape(in[i]));
        return out;
    }

    public ChatMessageOut(ChatMessageIn messageIn, String formatXML, String[] formatXMLArgs) {
        this(messageIn);
        setContents(formatXML, formatXMLArgs);
    }

    public ChatMessageOut(ChatMessageIn messageIn) {
        this(messageIn.server, messageIn.from);
        if (messageIn.contents != null) {
            this.setContentsPlain(messageIn.contents);
        }
        this.context = messageIn.context;
    }

    public void setContents(String formatXML, String[] formatXMLArgs) {
        this.contents = String.format(formatXML, (Object[])xmlEscapeArray(formatXMLArgs));
    }

    public void setContentsPlain(String plain) {
        this.contents = convertLegacyColors(Utils.XMLEscape(plain));
    }

    private static final char COLOR_CHAR = '\u00a7';
    private static final Pattern FIX_REDUNDANT_TAGS = Pattern.compile("<([a-z]+)[^>]*>(\\s*)</\\1>", Pattern.CASE_INSENSITIVE);
    private static final Map<Character, String> colorNames = new HashMap<>();
    static {
        colorNames.put('0', "black");
        colorNames.put('1', "dark_blue");
        colorNames.put('2', "dark_green");
        colorNames.put('3', "dark_aqua");
        colorNames.put('4', "dark_red");
        colorNames.put('5', "dark_purple");
        colorNames.put('6', "gold");
        colorNames.put('7', "gray");
        colorNames.put('8', "dark_gray");
        colorNames.put('9', "blue");
        colorNames.put('a', "green");
        colorNames.put('b', "aqua");
        colorNames.put('c', "red");
        colorNames.put('d', "light_purple");
        colorNames.put('e', "yellow");
        colorNames.put('f', "white");
    }

    public static String convertLegacyColors(String in) {
        StringBuilder out = new StringBuilder("<color name=\"white\">");

        int lastPos = 0; char currentColor = 'f';

        Set<String> openTagsSet = new HashSet<>();
        Stack<String> openTags = new Stack<>();
        openTagsSet.add("color");
        openTags.push("color");

        while(true) {
            int pos = in.indexOf(COLOR_CHAR, lastPos);
            if(pos < 0) {
                if(lastPos == 0) {
                    return in;
                }
                break;
            }
            char newColor = in.charAt(pos + 1);

            if(pos > 0) {
                out.append(in.substring(lastPos, pos));
            }

            lastPos = pos + 2;

            if((newColor >= '0' && newColor <= '9') || (newColor >= 'a' && newColor <= 'f') || newColor == 'r') {

                boolean doesNotChangeColor = newColor == 'r' || currentColor == newColor;

                while(!openTags.empty()) {
                    String tag = openTags.pop();
                    if(doesNotChangeColor && tag.equals("color")) {
                        continue;
                    }
                    out.append("</");
                    out.append(tag);
                    out.append('>');
                }
                openTagsSet.clear();

                openTagsSet.add("color");
                openTags.push("color");

                if(doesNotChangeColor) {
                    continue;
                }

                out.append("<color name=\"");
                out.append(colorNames.get(newColor));
                out.append("\">");

                currentColor = newColor;
            } else {
                switch (newColor) {
                    case 'l':
                        if(!openTagsSet.contains("b")) {
                            openTags.push("b");
                            openTagsSet.add("b");
                            out.append("<b>");
                        }
                        break;
                    case 'm':
                        if(!openTagsSet.contains("s")) {
                            openTags.push("s");
                            openTagsSet.add("s");
                            out.append("<s>");
                        }
                        break;
                    case 'n':
                        if(!openTagsSet.contains("u")) {
                            openTags.push("u");
                            openTagsSet.add("u");
                            out.append("<u>");
                        }
                        break;
                    case 'o':
                        if(!openTagsSet.contains("i")) {
                            openTags.push("i");
                            openTagsSet.add("i");
                            out.append("<i>");
                        }
                        break;
                }
            }
        }

        if(lastPos < in.length()) {
            out.append(in.substring(lastPos));
        }
        while(!openTags.empty()) {
            String tag = openTags.pop();
            out.append("</");
            out.append(tag);
            out.append('>');
        }

        return FIX_REDUNDANT_TAGS.matcher(out.toString()).replaceAll("$2");
    }

    public String server;
    public UserInfo from;
    public MessageTarget to;

    public long timestamp = System.currentTimeMillis() / 1000;

    public UUID context;
    public boolean finalizeContext = false;
    public MessageType type = MessageType.TEXT;

    public int importance = 0;

    public String contents;
}
