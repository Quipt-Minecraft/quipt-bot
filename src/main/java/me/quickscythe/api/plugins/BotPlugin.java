package me.quickscythe.api.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotPlugin {

    private String name = null;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ClassLoader loader = this.getClass().getClassLoader();


    public void enable(){
        logger.info("{} enabled.", name);
    }

    public void disable(){
        logger.info("{} disabled.", name);
    }

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