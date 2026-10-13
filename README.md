# Saynt's Tweaks

Client-side quality-of-life mod for Minecraft **26.3** (Fabric). Works in singleplayer and on **vanilla servers**: the server never needs it installed.

## Features

- **Container sorting**: glossy sort buttons on chests, barrels, ender chests, shulker boxes and the player inventory (hotbar, armor and offhand are left alone). Merges partial stacks and orders items using only regular clicks.
- **Sort orders**: Minecraft, name (A→Z), quantity or category. Shift-click a sort button to cycle.
- **Inventory transfers** (hotbar always left alone):
  - *Deposit matching*: moves inventory items already present in the open chest into it.
  - *Deposit everything*: empties the main inventory into the chest.
  - *Take everything*: empties the chest into the main inventory.
- **Container search**: a search box above chests; non-matching slots are dimmed. Ignores case and accents.
- **Hotbar refill**: when the held stack runs out (last block, last food, broken tool), a matching stack from the inventory takes its place.
- **Safe mode**: spreads clicks over several ticks for servers with an anti-cheat.

## Configuration

`config/sayntstweaks.json`:

| Option | Default | Description |
|---|---|---|
| `sortMode` | `MINECRAFT` | `MINECRAFT`, `NAME`, `QUANTITY` or `CATEGORY` |
| `hotbarRefill` | `true` | Refill the held stack when it runs out |
| `searchBar` | `true` | Show the search box above chests |
| `safeMode` | `false` | Send clicks gradually instead of all at once |
| `safeModeClicksPerTick` | `2` | Clicks per tick in safe mode (20 ticks = 1 s) |
- **Dark UI**: a built-in resource pack (on by default, toggleable in *Resource Packs*) with the vanilla menus, just darker.

## Building

Requires JDK 25.

```sh
./gradlew build      # jar in build/libs/
./gradlew runClient  # launch a dev game
```

## Regenerating the theme

The dark textures are generated from the vanilla ones by `tools/dark_ui.py` (needs Python 3 and Pillow). Tweak the gray levels at the top of the script, then:

```sh
rm -rf src/main/resources/resourcepacks/dark_ui/assets
python3 tools/dark_ui.py <path to the Minecraft 26.3 jar> src/main/resources/resourcepacks/dark_ui
```

## Note

This is a personal experiment, built in part with the help of AI (Claude).

## License

MIT
