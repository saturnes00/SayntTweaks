# Saynt's Tweaks

**Small, client-side inventory tweaks that work everywhere, including vanilla servers.**

Everything happens on your side using regular clicks, exactly like you would do by hand, just faster. The server doesn't need the mod and can't tell it's there.

## Features

### 📦 Sorting
A sort button on chests, barrels, ender chests, shulker boxes and your inventory. It merges partial stacks, then orders everything.

**Shift-click** the button to switch between four orders:
- **Minecraft**: the game's own item order, close to the creative menu
- **Name**: alphabetical (A→Z), accent-aware
- **Quantity**: what you have the most of comes first
- **Category**: blocks, tools & weapons, armor, food, potions, then the rest

Your hotbar, armor and offhand are never touched.

### 🔁 Transfers
Three buttons to move items between a chest and your inventory:
- **Take everything**: empties the chest into your inventory
- **Deposit everything**: empties your inventory into the chest
- **Deposit matching**: only moves items the chest already contains. Back from mining? One click and your stone, coal and iron each go to the right chest.

Your hotbar stays as it is, so you keep your tools and blocks in hand.

### 🔍 Search
A search box above chests and shulker boxes. Slots that don't match are dimmed. It ignores case and accents, and also matches the item's English id, so `iron` works in any language.

### 🔄 Hotbar refill
When the stack in your hand runs out (last block placed, last food eaten, tool broken), a matching stack from your inventory takes its place.

### 🌙 Dark UI
A built-in resource pack with the vanilla menus, just darker. Same textures, same bevels, easier on the eyes. On by default. Turn it off in *Options → Resource Packs* if you prefer the classic look.

## Compatibility
- **Client-side only**: install it on your game, not on the server
- Works in **singleplayer** and on **vanilla, Paper or Spigot servers**
- Requires **Fabric API**

## Safe mode for anti-cheat servers
By default, a sort or transfer happens instantly. Some servers with an anti-cheat may not like that many clicks at once. Enable **safe mode** to spread the clicks over a few seconds:

`config/sayntstweaks.json`
```json
{
  "sortMode": "MINECRAFT",
  "hotbarRefill": true,
  "searchBar": true,
  "safeMode": false,
  "safeModeClicksPerTick": 2
}
```

| Option | Default | Description |
|---|---|---|
| `sortMode` | `MINECRAFT` | `MINECRAFT`, `NAME`, `QUANTITY` or `CATEGORY` |
| `hotbarRefill` | `true` | Refill the held stack when it runs out |
| `searchBar` | `true` | Show the search box above chests |
| `safeMode` | `false` | Send clicks gradually instead of all at once |
| `safeModeClicksPerTick` | `2` | Clicks per tick in safe mode (20 ticks = 1 second) |

Always check a server's rules before using inventory mods.

## About
A personal project by Saynt, built in part with the help of AI (Claude). Feedback and bug reports are welcome!
