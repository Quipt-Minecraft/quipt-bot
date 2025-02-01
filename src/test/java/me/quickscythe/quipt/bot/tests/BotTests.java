package me.quickscythe.quipt.bot.tests;

import me.quickscythe.Bot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BotTests {

    @Test
    public void testBot() {
        Bot.main(new String[0]);
        assertTrue(Bot.config().has("bot_token"));
    }
}
