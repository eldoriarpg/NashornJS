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
    private final Map<UUID, String> userInput = new HashMap<>();
    private final Map<UUID, ScriptEngine> userEngines = new HashMap<>();
    private final ScriptEngineFactory factory;
    private final ScriptEngine engine;
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

        var userInput = getUserInput(sender, args);
        if (sender instanceof Player) sender.sendMessage(ChatColor.DARK_GRAY + "> " + String.join(" ", args));
        if (userInput != null) {
            try {
                sender.sendMessage(ChatColor.GRAY + "> " + getEngine(sender).eval(userInput));
            } catch (ScriptException e) {
                sender.sendMessage(ChatColor.RED + "> " + e.getMessage());
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

    private ScriptEngine getEngine(CommandSender sender) {
        if (!plugin.getConfig().getBoolean("perUserCli", false)) return engine;
        return userEngines.computeIfAbsent(getSenderUUID(sender), k -> factory.getScriptEngine());
    }

    private String getUserInput(CommandSender sender, String[] args) {
        var expr = String.join(" ", args);
        var uuid = getSenderUUID(sender);
        if (expr.endsWith("\\")) {
            final var sExpr = expr.substring(0, expr.length() - 1);
            userInput.compute(uuid, (k, v) -> v == null ? sExpr : v + sExpr);
            return null;
        }

        return userInput.containsKey(uuid) ? userInput.remove(uuid) + " " + expr : expr;
    }

    private UUID getSenderUUID(CommandSender sender){
        return sender instanceof Player ? ((Player) sender).getUniqueId() : null;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        userEngines.remove(event.getPlayer().getUniqueId());
        userInput.remove(event.getPlayer().getUniqueId());
    }
}
