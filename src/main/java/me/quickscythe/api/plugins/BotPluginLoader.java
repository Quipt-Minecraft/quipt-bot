package me.quickscythe.api.plugins;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

public class BotPluginLoader {
    private final Map<BotPlugin, ClassLoader> plugins = new HashMap<>();
    Logger logger = Logger.getLogger(getClass().getName());

    public BotPluginLoader() {
        registerPlugins();
        enablePlugins();
    }

    public void registerPlugin(BotPlugin plugin) {
        plugins.put(plugin, plugin.getClass().getClassLoader());
    }

    public void enablePlugin(BotPlugin plugin) {
        plugin.enable();
    }

    private void registerPlugins() {
        File plugin_folder = new File("plugins");
        if (!plugin_folder.exists()) plugin_folder.mkdir();
        for (File file : plugin_folder.listFiles()) {
            if (file.getName().endsWith(".jar")) {
                try {
                    URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader());
                    Properties properties = new Properties();
                    properties.load(classLoader.getResourceAsStream("plugin.properties"));
                    if (!properties.containsKey("main"))
                        throw new IOException("Plugin does not have a main class listed in plugin.properties");
                    if (!properties.containsKey("name"))
                        throw new IOException("Plugin does not have a name listed in plugin.properties");
                    Class<? extends BotPlugin> loadedClass = (Class<? extends BotPlugin>) classLoader.loadClass(properties.getProperty("main"));
                    BotPlugin instance = loadedClass.getDeclaredConstructor().newInstance();
                    instance.name(properties.getProperty("name"));
                    instance.logger().info("Loaded plugin {}.", instance.name());
                    plugins.put(instance, classLoader);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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
                logger.info("There was an error disabling a plugin (" + entry.getKey().name() + ").");
                logger.throwing(getClass().getName(), "disablePlugins", e);
            }
        }
        plugins.clear();
    }

    public void disablePlugin(BotPlugin plugin) throws IOException {
        plugin.disable();
        //todo remove any listeners
        String name = plugin.name();
        if (plugins.get(plugin) instanceof URLClassLoader urlClassLoader) urlClassLoader.close();
        logger.info("Plugin " + name + " disabled.");
    }

    private void enablePlugins() {
        for (BotPlugin plugin : getPlugins()) {
            plugin.enable();
        }
    }

    public void reloadPlugins() {

        disablePlugins();
        registerPlugins();
        enablePlugins();
    }
}

