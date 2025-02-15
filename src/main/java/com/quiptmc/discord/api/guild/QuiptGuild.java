/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.quiptmc.discord.api.guild;

import com.quiptmc.discord.api.guild.channel.QuiptTextChannel;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;

public class QuiptGuild {

    private final Guild guild;

    public QuiptGuild(Guild guild) {
        this.guild = guild;
    }

    public List<QuiptTextChannel> getTextChannels() {
        List<QuiptTextChannel> channels = new ArrayList<>();
        guild.getTextChannels().forEach(channel -> channels.add(new QuiptTextChannel(channel)));
        return channels;
    }
}
