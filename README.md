# OwO What is dis?

Some Minecraft Plugins out there used the Nashorn JS Engine in the past to evaluate some stuff.\
This Engine was removed in Java 15, which caused some problems for these plugins.

The NashornJS Plugin provides a ScriptEngineManager to create new script engines.

# How does it work?

NashornJS registers a ScriptEngineManager as a service on load.\
You can use this service to retrieve your own nashorn engine instance.

``` java
    var reg = Bukkit.getServer().getServicesManager().getRegistration(ScriptEngineManager.class);
    var managerChan = reg.getProvider();
    var nashornChan = managerChan.getEngineByName("nashorn");
```

Thats all. Now you can use your nashorn instance like you always did.

# What is this cli thingy OwO?
This plugin has also a small cli to use javascript on your server.\
It is disabled by default for obvious reasons.\
JS in the wrong hands and the trouble can start.

You can use the cli with the `/js` or `/nashorn` command.\
Write a `\` at the end to add another line.

Every player can have its own engine instance.