package de.eldoria.nashorn;

import de.eldoria.nashorn.command.CLI;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

@SuppressWarnings("unused")
public class Nashorn extends JavaPlugin {
    private final ScriptEngineFactory factory = new NashornScriptEngineFactory();

    @Override
    public void onLoad() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        var services = getServer().getServicesManager();
        ScriptEngineManager engine;
        if (services.isProvidedFor(ScriptEngineManager.class)) {
            var registration = services.getRegistration(ScriptEngineManager.class);
            engine = registration.getProvider();
        } else {
            engine = new ScriptEngineManager();
            services.register(ScriptEngineManager.class, engine, this, ServicePriority.Highest);
        }
        for (var names : getConfig().getStringList("names")) {
            engine.registerEngineName(names, factory);
            getLogger().info("Registered Nashorn engine with name " + names);
        }
    }

    @Override
    public void onEnable() {
        if (getConfig().getBoolean("cli", false)) {
            var cli = new CLI(factory, this);
            getServer().getPluginManager().registerEvents(cli, this);
            getCommand("js").setExecutor(cli);
        }
    }
}