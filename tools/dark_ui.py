"""
Generates the built-in "Dark UI" resource pack: the vanilla GUI textures,
just darker. Minecraft draws its menus with a small set of grays, so each
gray level is remapped to a darker one, keeping every bevel and detail.

Usage: python3 tools/dark_ui.py <minecraft.jar> <output pack dir>
"""
import io
import sys
import zipfile
from pathlib import Path

from PIL import Image

# Folders of the vanilla jar to darken
SOURCES = (
    "assets/minecraft/textures/gui/container/",
    "assets/minecraft/textures/gui/sprites/container/",
    "assets/minecraft/textures/gui/sprites/widget/",
    "assets/minecraft/textures/gui/recipe_book.png",
)

# Vanilla gray level -> dark gray level, for panels and slots
PANEL_LEVELS = [
    (0x00, 0x00),  # outline
    (0x21, 0x0E),
    (0x37, 0x12),  # slot inner shadow
    (0x55, 0x1A),  # panel shadow edge
    (0x8B, 0x22),  # slot background
    (0xC6, 0x31),  # panel background
    (0xFF, 0x4E),  # panel highlight edge
]

# Buttons keep their stone grain, just dimmed
WIDGET_FACTOR = 0.5
WIDGET_WHITE = 0xB4    # the white outline of hovered buttons


def interpolate(levels, level):
    for (l0, v0), (l1, v1) in zip(levels, levels[1:]):
        if level <= l1:
            return round(v0 + (v1 - v0) * (level - l0) / (l1 - l0))
    return levels[-1][1]


def is_gray(r, g, b):
    return max(r, g, b) - min(r, g, b) <= 6


def darken(img, widget):
    img = img.convert("RGBA")
    px = img.load()
    w, h = img.size
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            if a == 0 or not is_gray(r, g, b):
                continue
            level = (r + g + b) // 3
            if widget:
                v = WIDGET_WHITE if level == 0xFF else round(level * WIDGET_FACTOR)
            else:
                v = interpolate(PANEL_LEVELS, level)
            px[x, y] = (v, v, v, a)
    return img


def wanted(name):
    return name.endswith(".png") and any(
        name == s or (s.endswith("/") and name.startswith(s)) for s in SOURCES)


def main(jar, out):
    out = Path(out)
    with zipfile.ZipFile(jar) as zf:
        names = [n for n in zf.namelist() if wanted(n)]
        for name in names:
            img = Image.open(io.BytesIO(zf.read(name)))
            dest = out / name
            dest.parent.mkdir(parents=True, exist_ok=True)
            darken(img, widget="/sprites/widget/" in name).save(dest)
            meta = name + ".mcmeta"
            if meta in zf.namelist():
                (out / meta).write_bytes(zf.read(meta))
    print(f"{len(names)} textures written to {out}")


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
