![GitHub Workflow Status](https://img.shields.io/github/workflow/status/eldoriarpg/NashornJs/Verify%20state?style=for-the-badge&label=Building)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/eldoriarpg/NashornJs/Publish%20to%20Nexus?style=for-the-badge&label=Publishing)
![Sonatype Nexus (Releases)](https://img.shields.io/nexus/maven-releases/de.eldoria/nashornjs?label=Release&logo=Release&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)
![Sonatype Nexus (Development)](https://img.shields.io/nexus/maven-dev/de.eldoria/nashornjs?label=DEV&logo=Release&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/de.eldoria/nashornjs?color=orange&label=Snapshot&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)

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

That's all. Now you can use your nashorn instance like you always did.

You won't need any external dependency for this way, which was a main point for this implementation.\
You may want to add `NashornJs` as a Softdepend in your plugin.yml

Of course there may be a better way, but the Bukkit Classloader is fucking around with everything, so I will stick with this easy any reliable way.\
If you know a better way please contribute to the git repository.

# What is this cli thingy OwO?
This plugin has also a small cli to use javascript on your server.\
It is disabled by default for obvious reasons.\
JS in the wrong hands, and the trouble can start.

You can use the cli with the `/js` or `/nashorn` command.\
Write a `\` at the end to add another line.

Every player can have its own engine instance.

You will need the `nashorn.eval` permission to access the cli.