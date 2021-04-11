package de.eldoria.javascript;

import com.oracle.truffle.js.scriptengine.GraalJSEngineFactory;
import de.eldoria.javascript.command.CLI;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

public class JavaScript extends JavaPlugin {
    ScriptEngineFactory nashornFactory = new NashornScriptEngineFactory();
    ScriptEngineFactory graalFactory = new GraalJSEngineFactory();
    ScriptEngineManager engineManager = null;

    @Override
    public void onLoad() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        var services = getServer().getServicesManager();

        if (services.isProvidedFor(ScriptEngineManager.class)) {
            var registration = services.getRegistration(ScriptEngineManager.class);
            engineManager = registration.getProvider();
        }

        if (engineManager == null) {
            engineManager = new ScriptEngineManager();
            services.register(ScriptEngineManager.class, engineManager, this, ServicePriority.Highest);
        }

        if ("graal".equalsIgnoreCase(getConfig().getString("default", "graal"))) {
            for (var name : graalFactory.getNames()) {
                registerFactoryByName(name, graalFactory);
            }
            nashornFactory.getNames().stream()
                    .filter(name -> !graalFactory.getNames().contains(name))
                    .forEach(name -> registerFactoryByName(name, nashornFactory));
        }

        if ("nashorn".equalsIgnoreCase(getConfig().getString("default", "graal"))) {
            for (var name : nashornFactory.getNames()) {
                registerFactoryByName(name, nashornFactory);
            }
            graalFactory.getNames().stream()
                    .filter(name -> !nashornFactory.getNames().contains(name))
                    .forEach(name -> registerFactoryByName(name, graalFactory));
        }
    }

    private void registerFactoryByName(String name, ScriptEngineFactory factory) {
        if (factory.getScriptEngine() == null) {
            getLogger().info("§cfactory §3" + factory.getEngineName() + "§c does not prvide any script engine");
        }
        engineManager.registerEngineName(name, factory);
        if (engineManager.getEngineByName(name) != null) {
            getLogger().info("§2Registered §b" + factory.getEngineName() + "§2 with name §b" + name);
        }
    }

    @Override
    public void onEnable() {
        if (getConfig().getBoolean("cli", false)) {
            var cli = new CLI(nashornFactory, this);
            getServer().getPluginManager().registerEvents(cli, this);
            getCommand("js").setExecutor(cli);
        }
    }
}
