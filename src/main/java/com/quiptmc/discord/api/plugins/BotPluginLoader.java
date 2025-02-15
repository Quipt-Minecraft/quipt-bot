package com.quiptmc.discord.api.plugins;


import com.quiptmc.discord.logger.LoggerUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class BotPluginLoader {
    private final Map<BotPlugin, ClassLoader> plugins = new HashMap<>();

    public BotPluginLoader() {
        registerPlugins();
        enablePlugins();
    }

    public BotPlugin registerPlugin(File pluginFile) {
        if (pluginFile.getName().endsWith(".jar")) {
            try {
                URLClassLoader classLoader = new URLClassLoader(new URL[]{pluginFile.toURI().toURL()});
                Properties properties = new Properties();

                InputStream inputStream = classLoader.getResourceAsStream("bot.plugin.properties");
                if (inputStream == null)
                    throw new IOException("bot.plugin.properties not found in the JAR file");

                properties.load(inputStream);
                if (!properties.containsKey("main"))
                    throw new IOException("Plugin " + pluginFile.getName() + " does not have a main class listed in bot.plugin.properties");
                if (!properties.containsKey("name"))
                    throw new IOException("Plugin " + pluginFile.getName() + " does not have a name listed in bot.plugin.properties");
                Class<?> loadedClass = classLoader.loadClass(properties.getProperty("main"));
                Object instance = loadedClass.getDeclaredConstructor().newInstance();
                classLoader.close();
                assert instance instanceof BotPlugin;
                BotPlugin plugin = (BotPlugin) instance;
                plugin.name(properties.getProperty("name"));
                plugin.logger().info("Initialized plugin {}.", plugin.name());
                plugins.put(plugin, classLoader);
                return plugin;

            } catch (Exception e) {
                LoggerUtils.error("PluginLoader", "There was an error registering plugin {}.", pluginFile.getName(), e);
            }

        }
        return null;
    }

    public void enablePlugin(BotPlugin plugin) {
        LoggerUtils.log("PluginLoader", "Enabling plugin {}...", plugin.name());
        plugin.enable();
        LoggerUtils.log("PluginLoader", "Enabled {}.", plugin.name());
    }

    private void registerPlugins() {
        File plugin_folder = new File("bot_plugins");
        if (!plugin_folder.exists()) LoggerUtils.log("PluginLoader", "Creating plugin folder: {}", plugin_folder.mkdir());
        LoggerUtils.log("PluginLoader", "Initializing plugins...");
        for (File file : Objects.requireNonNull(plugin_folder.listFiles())) {
            registerPlugin(file);
        }
        LoggerUtils.log("PluginLoader", "Initialized {} plugins.", plugins.size());
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
        LoggerUtils.log("PluginLoader", "Disabling plugins...");
        for (Map.Entry<BotPlugin, ClassLoader> entry : plugins.entrySet()) {
            try {
                disablePlugin(entry.getKey());

            } catch (IOException e) {
                LoggerUtils.log("PluginLoader", "There was an error disabling a plugin ({}).", entry.getKey().name());
                LoggerUtils.log("PluginLoader", getClass().getName(), "disablePlugins", e);
            }
        }
        plugins.clear();
    }

    public void disablePlugin(BotPlugin plugin) throws IOException {
        plugin.disable();
        //todo remove any listeners
        String name = plugin.name();
        if (plugins.get(plugin) instanceof URLClassLoader urlClassLoader) urlClassLoader.close();
        LoggerUtils.log("PluginLoader", "Plugin {} disabled.", name);
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

