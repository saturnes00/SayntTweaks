"""
Draws the mod icon: a big version of the sort button, in the dark vanilla
style. Writes the 128px mod/pack icons and a 512px icon for Modrinth.

Usage: python3 tools/icon.py
"""
from PIL import Image

N = 32
OUTLINE, LIGHT, DARK, BG = 0x00, 0x55, 0x1C, 0x37
GLYPH, SHADOW = 0xD0, 0x14


def icon():
    img = Image.new("RGBA", (N, N), (0, 0, 0, 0))
    px = img.load()

    def put(x, y, v):
        px[x, y] = (v, v, v, 255)

    for y in range(N):
        for x in range(N):
            if (x in (0, N - 1)) and (y in (0, N - 1)):
                continue  # rounded corners
            if x in (0, N - 1) or y in (0, N - 1):
                put(x, y, OUTLINE)
            elif x <= 2 or y <= 2:
                put(x, y, LIGHT)
            elif x >= N - 3 or y >= N - 3:
                put(x, y, DARK)
            else:
                put(x, y, BG)

    glyph = set()
    # three bars getting shorter
    for top, length in ((7, 13), (13, 10), (19, 7)):
        glyph |= {(x, y) for x in range(6, 6 + length) for y in range(top, top + 3)}
    # down arrow on the right
    glyph |= {(x, y) for x in range(21, 24) for y in range(6, 19)}
    for i, y in enumerate(range(17, 23)):
        half = 5 - i
        glyph |= {(x, y) for x in range(22 - half, 23 + half)}

    for x, y in glyph:  # drop shadow first, then the glyph on top
        if (x + 1, y + 1) not in glyph:
            put(x + 1, y + 1, SHADOW)
    for x, y in glyph:
        put(x, y, GLYPH)
    return img


if __name__ == "__main__":
    img = icon()
    img.resize((128, 128), Image.NEAREST).save("src/main/resources/assets/sayntstweaks/icon.png")
    img.resize((128, 128), Image.NEAREST).save("src/main/resources/resourcepacks/dark_ui/pack.png")
    img.resize((512, 512), Image.NEAREST).save("modrinth/icon.png")
    print("icons written")
