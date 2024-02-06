# Singularity

A small mod to find item singularities.

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

Dependencies for this mod include [Cloth Config](https://modrinth.com/mod/cloth-config)
and [Architectury API](https://modrinth.com/mod/architectury-api).
