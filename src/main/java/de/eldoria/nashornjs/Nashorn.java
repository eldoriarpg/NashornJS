package de.eldoria.nashornjs;

import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.eldoutilities.updater.Updater;
import de.eldoria.eldoutilities.updater.lynaupdater.LynaUpdateData;
import de.eldoria.nashornjs.command.CLI;
import org.bukkit.plugin.ServicePriority;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

@SuppressWarnings("unused")
public class Nashorn extends EldoPlugin {
    private final ScriptEngineFactory factory = new NashornScriptEngineFactory();

    @Override
    public void onPluginLoad() {
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
    public void onPluginEnable() {
        if (getConfig().getBoolean("cli", false)) {
            var cli = new CLI(factory, this);
            getServer().getPluginManager().registerEvents(cli, this);
            getCommand("js").setExecutor(cli);
        }
    }

    @Override
    public void onPostStart() throws Throwable {
        Updater.lyna(LynaUpdateData.builder(this, 12)
                        .notifyUpdate(getConfig().getBoolean("updateCheck", true))
                        .notifyPermission("nashorn.eval")
                        .build())
                .start();
    }
}
