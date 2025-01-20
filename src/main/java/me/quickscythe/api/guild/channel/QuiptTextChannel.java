/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package me.quickscythe.api.guild.channel;

import me.quickscythe.api.embed.Embed;
import me.quickscythe.logger.LoggerUtils;
import me.quickscythe.quipt.api.NetworkUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class QuiptTextChannel {

    private final TextChannel channel;

    public QuiptTextChannel(TextChannel channel) {
        this.channel = channel;
    }

    public String getName() {
        return channel.getName();
    }

    public void sendMessage(String s) {
        channel.sendMessage(s).queue();
    }

    public String getId() {
        return channel.getId();
    }

    public long getIdLong() {
        return channel.getIdLong();
    }

    public void sendMessageEmbeds(Embed... embeds) {
        for (Embed embed : embeds) {
            EmbedBuilder builder = new EmbedBuilder(EmbedBuilder.fromData(DataObject.fromJson(embed.json().toString())));
            channel.sendMessageEmbeds(builder.build()).queue();
        }
    }

    public void sendMessage(Embed embed) {
        EmbedBuilder builder = new EmbedBuilder(EmbedBuilder.fromData(DataObject.fromJson(embed.json().toString())));
        channel.sendMessageEmbeds(builder.build()).queue();
    }

    public void sendPlayerMessage(UUID uid, String playerName, String message) {
        deleteOldWebhooks();
        Webhook hook = getWebhook(playerName, uid);

        hook.sendMessage(message).queue();
    }

    private Webhook getWebhook(String playerName, UUID uid) {
        List<Webhook> webhooks = channel.retrieveWebhooks().complete();
        for (Webhook hook : webhooks)
            if (hook.getName().equals(playerName)) return hook;

        WebhookAction hookAction = channel.createWebhook(playerName);
        try {
            InputStream in = NetworkUtils.downloadFile("https://crafatar.com/avatars/" + uid + "?size=128&overlay");
            File playerAssetFolder = new File("player_assets");
            if (!playerAssetFolder.exists())
                LoggerUtils.log("Bot", "Creating player assets folder: " + playerAssetFolder.mkdir());
            FileOutputStream out = new FileOutputStream("player_assets/" + uid + ".png");
            NetworkUtils.saveStream(in, out);
            hookAction.setAvatar(Icon.from(new File("player_assets/" + uid + ".png"))).queue();
        } catch (IOException ex) {
            LoggerUtils.log("Bot", "Error downloading player asset");
        }
        return hookAction.complete();
    }

    private void deleteOldWebhooks() {
        for(Webhook hook : channel.retrieveWebhooks().complete()) {
            long creationEpoch = hook.getTimeCreated().toInstant().toEpochMilli();
            long currentEpoch = Instant.now().toEpochMilli();

            long diff = currentEpoch - creationEpoch;
            long check = TimeUnit.MILLISECONDS.convert(5, TimeUnit.DAYS);
            System.out.println("Diff: " + diff + " Check: " + check);
            if(diff > check){
                System.out.println("Hook old, deleting.");
                hook.delete().queue();
            }

        }
    }

}
