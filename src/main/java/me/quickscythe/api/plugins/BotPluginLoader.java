package me.quickscythe.api.plugins;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class BotPluginLoader {
    private final Map<BotPlugin, ClassLoader> plugins = new HashMap<>();
    Logger logger = LoggerFactory.getLogger("PluginLoader");

    public BotPluginLoader() {
        registerPlugins();
        enablePlugins();
    }

    public void registerPlugin(BotPlugin plugin) {
        plugins.put(plugin, plugin.getClass().getClassLoader());
    }

    public void enablePlugin(BotPlugin plugin) {
        logger.info("Enabling plugin {}...", plugin.name());
        plugin.enable();
        logger.info("Enabled {}.", plugin.name());
    }

    private void registerPlugins() {
        File plugin_folder = new File("bot_plugins");
        if (!plugin_folder.exists()) logger.info("Creating plugin folder: {}", plugin_folder.mkdir());
        logger.info("Initializing plugins...");
        for (File file : Objects.requireNonNull(plugin_folder.listFiles())) {
            if (file.getName().endsWith(".jar")) {
                try {
                    URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
                    Properties properties = new Properties();

                    InputStream inputStream = classLoader.getResourceAsStream("bot.plugin.properties");
                    if (inputStream == null)
                        throw new IOException("bot.plugin.properties not found in the JAR file");

                    properties.load(inputStream);
                    if (!properties.containsKey("main"))
                        throw new IOException("Plugin " + file.getName() + " does not have a main class listed in bot.plugin.properties");
                    if (!properties.containsKey("name"))
                        throw new IOException("Plugin " + file.getName() + " does not have a name listed in bot.plugin.properties");
                    Class<?> loadedClass = classLoader.loadClass(properties.getProperty("main"));
                    Object instance = loadedClass.getDeclaredConstructor().newInstance();
                    if(instance instanceof BotPlugin plugin){
                        plugin.name(properties.getProperty("name"));
                        plugin.logger().info("Initialized plugin {}.", plugin.name());
                        plugins.put(plugin, classLoader);
                    }


                } catch (Exception e) {
                    logger.error("There was an error registering plugin {}.", file.getName(), e);
                }
            }
        }
        logger.info("Initialized {} plugins.", plugins.size());
    }

    public Set<BotPlugin> getPlugins() {
        return plugins.keySet();
    }

    public BotPlugin getPlugin(String name) {
        for (BotPlugin plugin : getPlugins()) {
            if (plugin.name().equalsIgnoreCase(name)) return plugin;
        }
        return null;
    }

    public void disablePlugins() {
        logger.info("Disabling plugins...");
        for (Map.Entry<BotPlugin, ClassLoader> entry : plugins.entrySet()) {
            try {
                disablePlugin(entry.getKey());

            } catch (IOException e) {
                logger.info("There was an error disabling a plugin ({}).", entry.getKey().name());
                logger.error(getClass().getName(), "disablePlugins", e);
            }
        }
        plugins.clear();
    }

    public void disablePlugin(BotPlugin plugin) throws IOException {
        plugin.disable();
        //todo remove any listeners
        String name = plugin.name();
        if (plugins.get(plugin) instanceof URLClassLoader urlClassLoader) urlClassLoader.close();
        logger.info("Plugin {} disabled.", name);
    }

    private void enablePlugins() {
        for (BotPlugin plugin : getPlugins()) {
            enablePlugin(plugin);
        }
    }

    public void reloadPlugins() {

        disablePlugins();
        registerPlugins();
        enablePlugins();
    }
}

