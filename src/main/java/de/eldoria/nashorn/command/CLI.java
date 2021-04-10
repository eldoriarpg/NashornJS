package de.eldoria.nashorn.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CLI implements TabExecutor, Listener {
    Map<UUID, String> userInput = new HashMap<>();
    Map<UUID, ScriptEngine> userEngines = new HashMap<>();
    ScriptEngineFactory factory;
    ScriptEngine engine;
    private final Plugin plugin;

    public CLI(ScriptEngineFactory factory, Plugin plugin) {
        this.factory = factory;
        this.plugin = plugin;
        engine = new NashornScriptEngineFactory().getScriptEngine();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("nashorn.eval")) {
            sender.sendMessage(ChatColor.RED + "You dont have the permission to do this.");
            return true;
        }
        try {
            String userInput = getUserInput(sender, args);
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.DARK_GRAY + "> " + String.join(" ", args));
            }
            if (userInput != null) {
                sender.sendMessage(ChatColor.GRAY + "> " + getEngine(sender).eval(userInput));
            }
        } catch (ScriptException e) {
            sender.sendMessage(ChatColor.RED + "> " + e.getMessage());
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

    private ScriptEngine getEngine(CommandSender sender) {
        if (!plugin.getConfig().getBoolean("perUserCli", false)) {
            return engine;
        }
        if (sender instanceof Player) {
            return userEngines.computeIfAbsent(((Player) sender).getUniqueId(), k -> factory.getScriptEngine());
        }
        if (engine == null) {
            engine = factory.getScriptEngine();
        }
        return engine;
    }

    private String getUserInput(CommandSender sender, String[] args) {
        String expr = String.join(" ", args);
        UUID uuid = sender instanceof Player ? ((Player) sender).getUniqueId() : null;
        if (expr.endsWith("\\")) {
            final String sExpr = expr.substring(0, expr.length() - 1);
            userInput.compute(uuid, (k, v) -> v == null ? sExpr : v + sExpr);
            return null;
        }

        return userInput.containsKey(uuid) ? userInput.remove(uuid) + " " + expr : expr;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        userEngines.remove(event.getPlayer().getUniqueId());
        userInput.remove(event.getPlayer().getUniqueId());
    }
}
