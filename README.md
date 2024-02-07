# Singularity

A small mod to find item singularities.

[![CurseForge latest version](https://img.shields.io/curseforge/v/968634?style=for-the-badge&logo=curseforge&label=CurseForge&color=blue)](https://www.curseforge.com/minecraft/mc-mods/singularity-detector)
[![Modrinth latest version](https://img.shields.io/modrinth/v/singularity?style=for-the-badge&logo=modrinth&label=Modrinth&color=blue
)](https://modrinth.com/mod/singularity)

## Features

- [x] Automatic scheduled item removal
- [x] Farm leak detection
- [x] Fully configurable
- [x] Warns before clearing items
- [x] Easy to set up
- [x] Minimal dependencies
- [x] Runs on a separate thread
- [x] Very well optimized

## Why?

An item singularity is where there are tons (possibly thousands)
of items dropped on the ground in a single location. This can
reduce a server's TPS (Ticks Per Second) to a very low amount,
compared to the normal 20. This mod aims to fix that.

By using techniques such as dropped item removal on a timer, you
can reduce the amount of items in a location. The issue with this
is that it is a band-aid fix, and won't help in the long term.

The best way to fix it is to eliminate the problem at its source,
removing the need for the other approach. With Singularity, it can
find the coordinates of potential item singularities, allowing you
to find these and fix them.

## Roadmap

- [x] Implement item removal timer
- [x] Implement singularity detection
- [x] MORE CONFIGURATION!!

## References

This project includes modified portions of:

- [Server Translations API](https://github.com/NucleoidMC/Server-Translations) ([MIT License](./licenses/server-translations-api.txt)) 
- [Fabric API](https://github.com/FabricMC/fabric) ([Apache 2.0 License](./licenses/fabric-api.txt))

Dependencies for this mod include [Cloth Config](https://modrinth.com/mod/cloth-config)
and [Architectury API](https://modrinth.com/mod/architectury-api).

## Debugging

There is a new command! `/singularity debug`

It takes 4 arguments:

- Level: The ResourceLocation of the dimension to spawn items in
- Position: Where to spawn the items (x, y, z) in that dimension
- Item ID: The item's ID (ResourceLocation)
- Count: How many items to spawn (min: 0, max: Infinity)

Example:

```txt
/singularity debug minecraft:overworld 0 100 0 minecraft:stick 8000
```
