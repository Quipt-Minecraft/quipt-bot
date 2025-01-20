/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package me.quickscythe.api;

import me.quickscythe.api.guild.QuiptGuild;
import net.dv8tion.jda.api.JDA;

import java.util.ArrayList;
import java.util.List;

public class QDA {
    private final JDA jda;
    public QDA(JDA jda){
        this.jda = jda;
    }

    public List<QuiptGuild> getGuilds(){
        List<QuiptGuild> guilds = new ArrayList<>();
        jda.getGuilds().forEach(guild -> guilds.add(new QuiptGuild(guild)));
        return guilds;
    }



}
