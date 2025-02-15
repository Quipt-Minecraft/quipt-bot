package com.quiptmc.discord.api.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BotPlugin {

    private String name = null;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ClassLoader loader = this.getClass().getClassLoader();


    public abstract void enable();

    public abstract void disable();

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public Logger logger(){
        return logger;
    }

    public ClassLoader loader(){
        return loader;
    }
}