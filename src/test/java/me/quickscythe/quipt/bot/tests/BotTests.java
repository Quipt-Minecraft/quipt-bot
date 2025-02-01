package me.quickscythe.quipt.bot.tests;

import me.quickscythe.Bot;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BotTests {

    @Test
    public void testBot() {
        try {
            Bot.main(new String[0]);
        }catch (JSONException | InvalidTokenException e){
            System.out.println("Invalid token");
        }
        assertTrue(Bot.config().has("bot_token"));
    }
}
