[ci]: http://ci.craftationgaming.com/job/FakeCreative/
[ciImg]: http://ci.craftationgaming.com/buildStatus/icon?job=FakeCreative

[releaseImg]: https://img.shields.io/github/release/RockinChaos/FakeCreative.svg?label=spigot%20release
[release]: https://github.com/RockinChaos/FakeCreative/releases/latest

[APIversionImg]: https://img.shields.io/nexus/craftationgaming/me.RockinChaos/fakecreative?server=https%3A%2F%2Frepo.craftationgaming.com&label=API%20Version

[issues]: https://github.com/RockinChaos/FakeCreative/issues
[licenseImg]: https://img.shields.io/github/license/RockinChaos/FakeCreative.svg
[license]: https://github.com/RockinChaos/FakeCreative/blob/master/LICENSE

![](https://i.imgur.com/u52QqZj.png)
[![ciImg]][ci] [![releaseImg]][release] ![APIversionImg] [![licenseImg]][license]

<p align="center">
 See <a href="https://github.com/RockinChaos/FakeCreative/wiki">FakeCreative's Wiki</a> for the full detailed documentation on the plugin.<br>
</p>

## FakeCreative - Creative Mode Emulation.
-----

### Description
-----
```
FakeCreative is a comprehensive creative mode emulation plugin designed to replace the default Minecraft 
creative mode with all included features and additional preferences! This plugin allows a high degree of 
admin and per-user customization and resolves known issues with exploits in the default creative mode. 
Creative mode was never designed by the Minecraft developers to be used on such a mass-scale that it is 
currently being used, rather it was mean't to be a single player use-case. The goal of this plugin is to 
completely work-around the broken, buggy, and easily exploitable default creative mode by giving players 
a creative experience in survival mode!
```
-----
### Installation
```
1) Once the FakeCreative.jar is downloaded, the jar must be placed into your server's plugins folder.
2) Double-check the server's plugins folder for any duplicate FakeCreative.jar files, such as FakeCreative(1).jar.
3) Delete any found duplicate instances or there will be conflicting errors in the console window.
4) Once this is completed, the server must be restarted to register and enable the plugin. The plugin
   will then attempt to find your server version and generate a configuration based on the found version.
5) From this point, it is up to you to configure the plugin or join the discord for additional help.
```

### Developer Notes
-----
This plugin has taken up many countless hours and has had continued support, please consider [donating](https://www.paypal.me/RockinChaos) as it is the best way to support the plugin.

Required Libraries when compiling (there are no required dependencies, only softDepends):
```
* Bukkit/Spigot (Latest Official)
* BetterNick (Latest Official)
* PlaceholderAPI (Latest Official)
* ProtocolLib (Latest Official)
* SkinsRestorer (Latest Official)
```

### Import with Maven
-----
If you are using FakeCreative's API, you first have to import it into your project.

To import FakeCreative, simply add the following code to your pom.xml
Replace {VERSION} with the version with the current release or snapshot version.
This should look like `1.0.2-RELEASE` or `1.0.3-SNAPSHOT` as an example.
```
    <repositories>
    <!--CraftationGaming Repository-->
        <repository>
            <id>CraftationGaming-chaos</id>
            <url>https://repo.craftationgaming.com/chaos</url>
        </repository>
    </repositories>
    <dependencies>
    <!--FakeCreative API-->
        <dependency>
            <groupId>me.RockinChaos.fakecreative</groupId>
            <artifactId>FakeCreative</artifactId>
            <version>{VERSION}</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
```

![](https://i.imgur.com/vFllc29.png)![](https://i.imgur.com/vFllc29.png)[<img src="https://i.imgur.com/WR5dVKN.png">](https://discord.gg/D5FnJ7C)[<img src="https://i.imgur.com/LJsmwSd.png">](http://ci.craftationgaming.com/)
