package de.eldoria.nashorn;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Nashorn {
    public void example() {
        RegisteredServiceProvider<ScriptEngineManager> registration = Bukkit.getServer().getServicesManager().getRegistration(ScriptEngineManager.class);
        ScriptEngineManager scriptEngineManager = registration.getProvider();
        ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");
    }
}
